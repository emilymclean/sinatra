package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.LiveServiceClient
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.persistence.LiveServicePersistence
import com.google.transit.realtime.FeedMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import org.koin.core.annotation.Factory

@Factory
class LiveServiceRepository(
    private val liveServicePersistence: LiveServicePersistence,
    private val liveServiceClient: LiveServiceClient
) {

    val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun getRealtimeUpdates(url: String): Flow<FeedMessage> {
        return liveServicePersistence.get(url) { trigger ->
            trigger
                .mapLatest {
                    try {
                        Result.success(liveServiceClient.getLiveUpdates(url))
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                }
                .shareIn(coroutineScope, SharingStarted.WhileSubscribed(), replay = 1)
        }
    }

    suspend fun getMultipleRealtimeUpdates(vararg url: String): Flow<List<FeedMessage>> {
        return return combine(
            *url.map { url ->
                getRealtimeUpdates(url)
            }.toTypedArray()
        ) { it.toList() }
    }

}