package cl.emilym.sinatra.data.client

import cl.emilym.gtfs.RouteEndpoint
import cl.emilym.gtfs.StopEndpoint
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.ShaDigest
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.network.GtfsApi
import pbandk.decodeFromByteArray

class RouteClient(
    val gtfsApi: GtfsApi
) {

    val routesEndpointPair by lazy {
        object : EndpointDigestPair<List<Route>>() {
            override val endpoint = ::routes
            override val digest = ::routesDigest
        }
    }

    suspend fun routes(): List<Route> {
        val pbStops = RouteEndpoint.decodeFromByteArray(gtfsApi.routes())
        return pbStops.route.map { Route.fromPB(it) }
    }

    suspend fun routesDigest(): ShaDigest {
        return gtfsApi.routesDigest()
    }

}