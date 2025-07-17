package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteRealtimeInformation
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopRealtimeInformation
import cl.emilym.sinatra.network.GtfsApi
import com.google.transit.realtime.FeedMessage
import org.koin.core.annotation.Factory

@Factory
class LiveServiceClient(
    private val api: GtfsApi
) {

    suspend fun tripUpdates(): FeedMessage {
        return api.tripUpdates()
    }

    suspend fun getRouteRealtime(routeId: RouteId): RouteRealtimeInformation {
        return RouteRealtimeInformation.fromPb(
            api.routeRealtime(routeId)
        )
    }

    suspend fun getStopRealtime(stopId: StopId): StopRealtimeInformation {
        return StopRealtimeInformation.fromPb(
            api.stopRealtime(stopId)
        )
    }

}