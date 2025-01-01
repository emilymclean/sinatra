package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.EndpointDigestPair
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.CacheState
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.ShaDigest
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Factory
class CacheWorkerDependencies(
    val shaRepository: ShaRepository,
    val remoteConfigRepository: RemoteConfigRepository
)

abstract class CacheWorker<T> {
    abstract val cacheWorkerDependencies: CacheWorkerDependencies
    private val shaRepository: ShaRepository get() = cacheWorkerDependencies.shaRepository
    private val remoteConfigRepository: RemoteConfigRepository
        get() = cacheWorkerDependencies.remoteConfigRepository

    abstract val clock: Clock
    abstract val cacheCategory: CacheCategory

    open val expireTime: Duration = 24.hours
    private suspend fun adjustedExpireTime() = expireTime * remoteConfigRepository.dataCachePeriodMultiplier()

    abstract suspend fun saveToPersistence(data: T, resource: ResourceKey)
    abstract suspend fun getFromPersistence(resource: ResourceKey): T?
    open suspend fun existsInPersistence(resource: ResourceKey): Boolean { return true }

    protected suspend fun run(pair: EndpointDigestPair<T>, resource: ResourceKey): Cachable<T> {
        val info = shaRepository.cached(cacheCategory, resource)

        if (info.shouldCheckForUpdate(resource)) {
            val digest = try {
                pair.digest()
            } catch (e: Throwable) {
                return failure(info, e, resource)
            }

            when(info) {
                is CacheInformation.Unavailable -> return fetch(digest, info, pair, resource)
                is CacheInformation.Available -> when {
                    digest != info.digest -> return fetch(digest, info, pair, resource)
                }
            }
        }

        return getFromPersistence(resource)?.let { Cachable(it, CacheState.CACHED) } ?:
            throw IllegalStateException("Resource was reported as cached, but could not be retrieved")
    }

    private suspend fun CacheInformation.shouldCheckForUpdate(resource: ResourceKey): Boolean {
        return when (this) {
            is CacheInformation.Unavailable -> true
            is CacheInformation.Available -> expired(adjustedExpireTime(), clock.now()) || existsInPersistence(resource)
        }
    }

    private suspend fun fetch(digest: ShaDigest, info: CacheInformation, pair: EndpointDigestPair<T>, resource: ResourceKey): Cachable<T> {
        val data = try {
            pair.endpoint()
        } catch (e: Throwable) {
            return failure(info, e, resource)
        }
        saveToPersistence(data, resource)
        shaRepository.save(digest, cacheCategory, resource)
        // We get from persistence anyway to ensure any joined tables are included
        return Cachable.live(getFromPersistence(resource) ?: data)
    }

    private suspend fun failure(info: CacheInformation, e: Throwable, resource: ResourceKey): Cachable<T> {
        Napier.e(e)
        return when (info) {
            is CacheInformation.Unavailable -> throw e
            is CacheInformation.Available -> Cachable(
                getFromPersistence(resource) ?: throw e,
                when (info.expired(adjustedExpireTime(), clock.now())) {
                    true -> CacheState.EXPIRED_CACHE
                    false -> CacheState.CACHED
                }
            )
        }
    }

}