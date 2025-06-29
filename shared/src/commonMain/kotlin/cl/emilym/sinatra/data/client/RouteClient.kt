package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteServiceCanonicalTimetable
import cl.emilym.sinatra.data.models.RouteServiceTimetable
import cl.emilym.sinatra.data.models.RouteTripTimetable
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.ShaDigest
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.network.GtfsApi
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory

@Factory
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

    fun routeServiceCanonicalTimetableEndpointPair(routeId: RouteId, serviceId: ServiceId) =
        object : EndpointDigestPair<RouteServiceCanonicalTimetable>() {
            override val endpoint = suspend { routeServiceCanonicalTimetable(routeId, serviceId) }
            override val digest = suspend { routeServiceCanonicalTimetableDigest(routeId, serviceId) }
        }

    fun routeTripTimetableEndpointPair(routeId: RouteId, serviceId: ServiceId, tripId: TripId) =
        object : EndpointDigestPair<RouteTripTimetable>() {
            override val endpoint = suspend { routeTripTimetable(routeId, serviceId, tripId) }
            override val digest = suspend { routeTripTimetableDigest(routeId, serviceId, tripId) }
        }

    suspend fun routes(): List<Route> {
        val pbStops = gtfsApi.routes()
        return pbStops.route.map { Route.fromPB(it) }
    }

    suspend fun routesDigest(): ShaDigest {
        return gtfsApi.routesDigest()
    }

    suspend fun routeServices(routeId: RouteId): List<ServiceId> {
        return gtfsApi.routeServices(routeId).serviceIds.apply {
            Napier.d("Services for route = $this")
        }
    }

    suspend fun routeServicesDigest(routeId: RouteId): ShaDigest {
        return gtfsApi.routeServicesDigest(routeId)
    }

    suspend fun routeServiceTimetable(routeId: RouteId, serviceId: ServiceId): RouteServiceTimetable {
        val pb = gtfsApi.routeServiceTimetable(
            routeId, serviceId
        )
        return RouteServiceTimetable.fromPB(pb)
    }

    suspend fun routeServiceTimetableDigest(routeId: RouteId, serviceId: ServiceId): ShaDigest {
        return gtfsApi.routeServiceTimetableDigest(routeId, serviceId)
    }

    suspend fun routeServiceCanonicalTimetable(routeId: RouteId, serviceId: ServiceId): RouteServiceCanonicalTimetable {
        val pb = gtfsApi.routeServiceCanonicalTimetableV2(
            routeId, serviceId
        )
        return RouteServiceCanonicalTimetable.fromPB(pb)
    }

    suspend fun routeServiceCanonicalTimetableDigest(routeId: RouteId, serviceId: ServiceId): ShaDigest {
        return gtfsApi.routeServiceCanonicalTimetableV2Digest(routeId, serviceId)
    }

    suspend fun routeTripTimetable(routeId: RouteId, serviceId: ServiceId, tripId: TripId): RouteTripTimetable {
        val pb = gtfsApi.routeTripTimetable(
            routeId, serviceId, tripId
        )
        return RouteTripTimetable.fromPB(pb)
    }

    suspend fun routeTripTimetableDigest(routeId: RouteId, serviceId: ServiceId, tripId: TripId): ShaDigest {
        return gtfsApi.routeTripTimetableDigest(routeId, serviceId, tripId)
    }

}