package cl.emilym.sinatra.network

import cl.emilym.gtfs.RouteEndpoint
import cl.emilym.gtfs.RouteServicesEndpoint
import cl.emilym.gtfs.RouteTimetableEndpoint
import cl.emilym.gtfs.ServiceEndpoint
import cl.emilym.gtfs.StopEndpoint
import cl.emilym.gtfs.StopTimetable
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GtfsApi {

    @GET("stops.pb")
    suspend fun stops(): StopEndpoint

    @GET("stops.pb.sha")
    suspend fun stopsDigest(): String

    @GET("stop/{stopId}/timetable.pb")
    suspend fun stopTimetable(
        @Path("stopId") stopId: StopId
    ): StopTimetable

    @GET("stop/{stopId}/timetable.pb.sha")
    suspend fun stopTimetableDigest(
        @Path("stopId") stopId: StopId
    ): String

    @GET("routes.pb")
    suspend fun routes(): RouteEndpoint

    @GET("routes.pb.sha")
    suspend fun routesDigest(): String

    @GET("route/{routeId}/services.pb")
    suspend fun routeServices(
        @Path("routeId") routeId: RouteId
    ): RouteServicesEndpoint

    @GET("route/{routeId}/services.pb.sha")
    suspend fun routeServicesDigest(
        @Path("routeId") routeId: RouteId
    ): String

    @GET("route/{routeId}/service/{serviceId}/timetable.pb")
    suspend fun routeServiceTimetable(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId
    ): RouteTimetableEndpoint

    @GET("route/{routeId}/service/{serviceId}/timetable.pb.sha")
    suspend fun routeServiceTimetableDigest(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId
    ): String

    @GET("services.pb")
    suspend fun services(): ServiceEndpoint

    @GET("services.pb.sha")
    suspend fun servicesDigest(): String

}