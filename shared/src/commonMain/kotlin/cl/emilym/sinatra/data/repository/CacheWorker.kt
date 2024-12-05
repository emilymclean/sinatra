package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.EndpointDigestPair
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.FlowState
import cl.emilym.sinatra.data.models.ResourceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

abstract class CacheWorker<T> {
    abstract val cacheWorkerPersistence: CacheWorkerPersistence
    abstract val shaRepository: ShaRepository

    abstract val clock: Clock
    abstract val cacheCategory: CacheCategory

    open val expireTime: Duration = 24.hours

    abstract suspend fun saveToPersistence(data: T)

    protected fun run(pair: EndpointDigestPair<T>): Flow<FlowState<T>> {
        return flow {
            val info = shaRepository.cached(cacheCategory, pair.resource)
            if (info.shouldCheckForUpdate()) {
                try {

                } catch (e: Exception) {

                }
            }
        }
    }

    private fun CacheInformation.shouldCheckForUpdate(): Boolean {
        return when (this) {
            is CacheInformation.Unavailable -> true
            is CacheInformation.Available -> (added + expireTime) < clock.now()
        }
    }

}