package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.LiveServiceClient
import cl.emilym.sinatra.data.persistence.LiveServicePersistence
import com.google.transit.realtime.FeedMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import org.koin.core.annotation.Factory

@Factory
class LiveServiceRepository(
    private val liveServicePersistence: LiveServicePersistence,
    private val liveServiceClient: LiveServiceClient
) {

    val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
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

}