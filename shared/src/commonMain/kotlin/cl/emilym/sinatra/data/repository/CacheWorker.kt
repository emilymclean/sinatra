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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

abstract class CacheWorker<T> {
    abstract val shaRepository: ShaRepository

    abstract val clock: Clock
    abstract val cacheCategory: CacheCategory

    open val expireTime: Duration = 24.hours

    abstract suspend fun saveToPersistence(data: T, resource: ResourceKey)
    abstract suspend fun getFromPersistence(resource: ResourceKey): T

    protected suspend fun run(pair: EndpointDigestPair<T>, resource: ResourceKey): Cachable<T> {
        val info = shaRepository.cached(cacheCategory, resource)

        if (info.shouldCheckForUpdate()) {
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

        return Cachable(getFromPersistence(resource), CacheState.CACHED)
    }

    private fun CacheInformation.shouldCheckForUpdate(): Boolean {
        return when (this) {
            is CacheInformation.Unavailable -> true
            is CacheInformation.Available -> expired(expireTime, clock.now())
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
        return Cachable.live(getFromPersistence(resource))
    }

    private suspend fun failure(info: CacheInformation, e: Throwable, resource: ResourceKey): Cachable<T> {
        Napier.e(e)
        return when (info) {
            is CacheInformation.Unavailable -> throw e
            is CacheInformation.Available -> Cachable(
                getFromPersistence(resource),
                when (info.expired(expireTime, clock.now())) {
                    true -> CacheState.EXPIRED_CACHE
                    false -> CacheState.CACHED
                }
            )
        }
    }

}