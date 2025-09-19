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
import cl.emilym.sinatra.network.validated
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory

@Factory
class RouteClient(
    val gtfsApi: GtfsApi
) {

    val routesEndpointPair by lazy {
        object : ValidatedEndpointDigestPair<List<Route>>() {
            override val endpoint = ::routes
            override val digest = ::routesDigest
        }
    }

    fun routeServicesEndpointPair(routeId: RouteId) = object : ValidatedEndpointDigestPair<List<ServiceId>>() {
        override val endpoint: suspend (ShaDigest) -> List<ServiceId> = { digest -> routeServices(digest, routeId) }
        override val digest = suspend { routeServicesDigest(routeId) }
    }

    fun routeServiceTimetableEndpointPair(routeId: RouteId, serviceId: ServiceId) =
        object : ValidatedEndpointDigestPair<RouteServiceTimetable>() {
            override val endpoint: suspend (ShaDigest) -> RouteServiceTimetable = { digest -> routeServiceTimetable(digest, routeId, serviceId) }
            override val digest = suspend { routeServiceTimetableDigest(routeId, serviceId) }
        }

    fun routeServiceCanonicalTimetableEndpointPair(routeId: RouteId, serviceId: ServiceId) =
        object : ValidatedEndpointDigestPair<RouteServiceCanonicalTimetable>() {
            override val endpoint: suspend (ShaDigest) -> RouteServiceCanonicalTimetable = { digest ->
                routeServiceCanonicalTimetable(digest, routeId, serviceId)
            }
            override val digest = suspend { routeServiceCanonicalTimetableDigest(routeId, serviceId) }
        }

    fun routeTripTimetableEndpointPair(routeId: RouteId, serviceId: ServiceId, tripId: TripId) =
        object : ValidatedEndpointDigestPair<RouteTripTimetable>() {
            override val endpoint: suspend (ShaDigest) -> RouteTripTimetable = { digest ->
                routeTripTimetable(digest, routeId, serviceId, tripId)
            }
            override val digest = suspend { routeTripTimetableDigest(routeId, serviceId, tripId) }
        }

    suspend fun routes(digest: ShaDigest): List<Route> {
        val pbStops = gtfsApi.routes().validated(digest)
        return pbStops.route.map { Route.fromPB(it) }
    }

    suspend fun routesDigest(): ShaDigest {
        return gtfsApi.routesDigest()
    }

    suspend fun routeServices(digest: ShaDigest, routeId: RouteId): List<ServiceId> {
        return gtfsApi.routeServices(routeId).validated(digest).serviceIds
    }

    suspend fun routeServicesDigest(routeId: RouteId): ShaDigest {
        return gtfsApi.routeServicesDigest(routeId)
    }

    suspend fun routeServiceTimetable(digest: ShaDigest, routeId: RouteId, serviceId: ServiceId): RouteServiceTimetable {
        val pb = gtfsApi.routeServiceTimetable(
            routeId, serviceId
        ).validated(digest)
        return RouteServiceTimetable.fromPB(pb)
    }

    suspend fun routeServiceTimetableDigest(routeId: RouteId, serviceId: ServiceId): ShaDigest {
        return gtfsApi.routeServiceTimetableDigest(routeId, serviceId)
    }

    suspend fun routeServiceCanonicalTimetable(digest: ShaDigest, routeId: RouteId, serviceId: ServiceId): RouteServiceCanonicalTimetable {
        val pb = gtfsApi.routeServiceCanonicalTimetableV2(
            routeId, serviceId
        ).validated(digest)
        return RouteServiceCanonicalTimetable.fromPB(pb)
    }

    suspend fun routeServiceCanonicalTimetableDigest(routeId: RouteId, serviceId: ServiceId): ShaDigest {
        return gtfsApi.routeServiceCanonicalTimetableV2Digest(routeId, serviceId)
    }

    suspend fun routeTripTimetable(digest: ShaDigest, routeId: RouteId, serviceId: ServiceId, tripId: TripId): RouteTripTimetable {
        val pb = gtfsApi.routeTripTimetable(
            routeId, serviceId, tripId
        ).validated(digest)
        return RouteTripTimetable.fromPB(pb)
    }

    suspend fun routeTripTimetableDigest(routeId: RouteId, serviceId: ServiceId, tripId: TripId): ShaDigest {
        return gtfsApi.routeTripTimetableDigest(routeId, serviceId, tripId)
    }

}