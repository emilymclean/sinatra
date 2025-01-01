package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.EndpointDigestPair
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.CacheState
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.ShaDigest
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Single
class CacheWorkerLockProvider {
    private val locks = mutableMapOf<ResourceKey, Mutex>()
    private val accessLock = Mutex()

    suspend fun lock(resource: ResourceKey): Mutex {
        locks[resource]?.let { return it }
        return accessLock.withLock {
            locks.getOrPut(resource) { Mutex() }
        }
    }
}

@Factory
class CacheWorkerDependencies(
    val shaRepository: ShaRepository,
    val remoteConfigRepository: RemoteConfigRepository,
    val cacheWorkerLockProvider: CacheWorkerLockProvider,
)

abstract class BaseCacheWorker<T, E> {
    abstract val cacheWorkerDependencies: CacheWorkerDependencies
    private val shaRepository: ShaRepository get() = cacheWorkerDependencies.shaRepository
    private val remoteConfigRepository: RemoteConfigRepository
        get() = cacheWorkerDependencies.remoteConfigRepository

    abstract val clock: Clock
    abstract val cacheCategory: CacheCategory

    open val expireTime: Duration = 24.hours
    private suspend fun adjustedExpireTime() = expireTime * remoteConfigRepository.dataCachePeriodMultiplier()

    abstract suspend fun saveToPersistence(data: T, resource: ResourceKey)
    abstract suspend fun getFromPersistence(resource: ResourceKey, extras: E): T?
    open suspend fun existsInPersistence(resource: ResourceKey): Boolean { return true }

    protected suspend fun run(pair: EndpointDigestPair<T>, resource: ResourceKey, extras: E): Cachable<T> {
        val info = shaRepository.cached(cacheCategory, resource)

        if (!info.shouldCheckForUpdate(resource))
            return getCached(resource, extras)

        return cacheWorkerDependencies.cacheWorkerLockProvider.lock(resource).withLock {
            val digest = try {
                pair.digest()
            } catch (e: Throwable) {
                return failure(info, e, resource, extras)
            }

            when(info) {
                is CacheInformation.Unavailable -> fetch(digest, info, pair, resource, extras)
                is CacheInformation.Available -> when {
                    digest != info.digest -> fetch(digest, info, pair, resource, extras)
                    else -> getCached(resource, extras)
                }
            }
        }
    }

    private suspend fun getCached(resource: ResourceKey, extras: E): Cachable<T> {
        return getFromPersistence(resource, extras)?.let { Cachable(it, CacheState.CACHED) } ?:
            throw IllegalStateException("Resource was reported as cached, but could not be retrieved")
    }

    private suspend fun CacheInformation.shouldCheckForUpdate(resource: ResourceKey): Boolean {
        return when (this) {
            is CacheInformation.Unavailable -> true
            is CacheInformation.Available -> expired(adjustedExpireTime(), clock.now()) || !existsInPersistence(resource)
        }
    }

    private suspend fun fetch(
        digest: ShaDigest,
        info: CacheInformation,
        pair: EndpointDigestPair<T>,
        resource: ResourceKey,
        extras: E
    ): Cachable<T> {
        val data = try {
            pair.endpoint()
        } catch (e: Throwable) {
            return failure(info, e, resource, extras)
        }
        saveToPersistence(data, resource)
        shaRepository.save(digest, cacheCategory, resource)
        // We get from persistence anyway to ensure any joined tables are included
        return Cachable.live(getFromPersistence(resource, extras) ?: data)
    }

    private suspend fun failure(
        info: CacheInformation,
        e: Throwable,
        resource: ResourceKey,
        extras: E
    ): Cachable<T> {
        Napier.e(e)
        return when (info) {
            is CacheInformation.Unavailable -> throw e
            is CacheInformation.Available -> Cachable(
                getFromPersistence(resource, extras) ?: throw e,
                when (info.expired(adjustedExpireTime(), clock.now())) {
                    true -> CacheState.EXPIRED_CACHE
                    false -> CacheState.CACHED
                }
            )
        }
    }

}

abstract class CacheWorker<T>: BaseCacheWorker<T, Unit>() {
    abstract suspend fun getFromPersistence(resource: ResourceKey): T?

    override suspend fun getFromPersistence(resource: ResourceKey, extras: Unit): T? {
        return getFromPersistence(resource)
    }

    protected suspend fun run(pair: EndpointDigestPair<T>, resource: ResourceKey): Cachable<T> {
        return run(pair, resource, Unit)
    }
}

typealias TimeCacheWorker<T> = BaseCacheWorker<T, Instant>