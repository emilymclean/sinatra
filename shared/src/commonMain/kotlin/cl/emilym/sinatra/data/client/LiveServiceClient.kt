package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.network.GtfsApi
import com.google.transit.realtime.FeedMessage
import org.koin.core.annotation.Factory

@Factory
class LiveServiceClient(
    private val api: GtfsApi
) {

    suspend fun getLiveUpdates(url: String): FeedMessage {
        return api.getLiveUpdates(url)
    }

}