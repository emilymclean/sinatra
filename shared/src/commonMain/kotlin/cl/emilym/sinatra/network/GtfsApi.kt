package cl.emilym.sinatra.network

import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GtfsApi {

    @GET("stops.pb")
    suspend fun stops(): ByteArray

    @GET("stops.pb.sha")
    suspend fun stopsDigest(): String

    @GET("stop/{stopId}/timetable.pb")
    suspend fun stopTimetable(
        @Path("stopId") stopId: StopId
    ): ByteArray

    @GET("stop/{stopId}/timetable.pb.sha")
    suspend fun stopTimetableDigest(
        @Path("stopId") stopId: StopId
    ): String

    @GET("routes.pb")
    suspend fun routes(): ByteArray

    @GET("routes.pb.sha")
    suspend fun routesDigest(): String

    @GET("route/{routeId}/services.pb")
    suspend fun routeServices(
        @Path("routeId") routeId: RouteId
    ): ByteArray

    @GET("route/{routeId}/services.pb.sha")
    suspend fun routeServicesDigest(
        @Path("routeId") routeId: RouteId
    ): String

    @GET("route/{routeId}/service/{serviceId}/timetable.pb")
    suspend fun routeServiceTimetable(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId
    ): ByteArray

    @GET("route/{routeId}/service/{serviceId}/timetable.pb")
    suspend fun routeServiceTimetableDigest(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId
    ): String

    @GET("services.pb")
    suspend fun services(): ByteArray

    @GET("services.pb.sha")
    suspend fun servicesDigest(): String

}