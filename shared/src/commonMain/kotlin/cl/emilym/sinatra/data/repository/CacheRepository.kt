package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.CacheClient
import cl.emilym.sinatra.data.persistence.CachePersistence
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Factory

@Factory
class CacheRepository(
    private val cacheClient: CacheClient,
    private val cachePersistence: CachePersistence
) {

    private val lock = Mutex()

    suspend fun shouldInvalidate(): Boolean {
        lock.withLock {
            val current = try {
                cacheClient.cacheInvalidationKey()
            } catch (e: Exception) {
                Napier.e(e)
                return false
            }

            val stored = cachePersistence.get()
            if (stored == null) {
                cachePersistence.save(current)
                return false
            }

            if (stored != current) {
                cachePersistence.save(current)
                return true
            }

            return false
        }
    }

}