package cl.emilym.sinatra.data.client

import cl.emilym.gtfs.RouteEndpoint
import cl.emilym.gtfs.RouteServicesEndpoint
import cl.emilym.gtfs.RouteTimetableEndpoint
import cl.emilym.gtfs.StopEndpoint
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteServiceTimetable
import cl.emilym.sinatra.data.models.ServiceId
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

    fun routeServicesEndpointPair(routeId: RouteId) = object : EndpointDigestPair<List<ServiceId>>() {
        override val endpoint = suspend { routeServices(routeId) }
        override val digest = suspend { routeServicesDigest(routeId) }
    }

    fun routeServiceTimetableEndpointPair(routeId: RouteId, serviceId: ServiceId) =
        object : EndpointDigestPair<RouteServiceTimetable>() {
            override val endpoint = suspend { routeServiceTimetable(routeId, serviceId) }
            override val digest = suspend { routeServiceTimetableDigest(routeId, serviceId) }
        }

    suspend fun routes(): List<Route> {
        val pbStops = RouteEndpoint.decodeFromByteArray(gtfsApi.routes())
        return pbStops.route.map { Route.fromPB(it) }
    }

    suspend fun routesDigest(): ShaDigest {
        return gtfsApi.routesDigest()
    }

    suspend fun routeServices(routeId: RouteId): List<ServiceId> {
        return RouteServicesEndpoint.decodeFromByteArray(gtfsApi.routeServices(routeId)).serviceIds
    }

    suspend fun routeServicesDigest(routeId: RouteId): ShaDigest {
        return gtfsApi.routeServicesDigest(routeId)
    }

    suspend fun routeServiceTimetable(routeId: RouteId, serviceId: ServiceId): RouteServiceTimetable {
        val pb = RouteTimetableEndpoint.decodeFromByteArray(gtfsApi.routeServiceTimetable(
            routeId, serviceId
        ))
        return RouteServiceTimetable.fromPB(pb)
    }

    suspend fun routeServiceTimetableDigest(routeId: RouteId, serviceId: ServiceId): ShaDigest {
        return gtfsApi.routeServiceTimetableDigest(routeId, serviceId)
    }

}