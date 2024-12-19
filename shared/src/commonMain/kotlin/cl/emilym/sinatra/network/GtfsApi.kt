package cl.emilym.sinatra.network

import cl.emilym.gtfs.RouteCanonicalTimetableEndpoint
import cl.emilym.gtfs.RouteEndpoint
import cl.emilym.gtfs.RouteServicesEndpoint
import cl.emilym.gtfs.RouteTimetableEndpoint
import cl.emilym.gtfs.RouteTripTimetableEndpoint
import cl.emilym.gtfs.ServiceEndpoint
import cl.emilym.gtfs.StopEndpoint
import cl.emilym.gtfs.StopTimetable
import cl.emilym.gtfs.content.Pages
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.TripId
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Url

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

    @GET("route/{routeId}/service/{serviceId}/canonical.pb")
    suspend fun routeServiceCanonicalTimetable(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId
    ): RouteCanonicalTimetableEndpoint

    @GET("route/{routeId}/service/{serviceId}/canonical.pb.sha")
    suspend fun routeServiceCanonicalTimetableDigest(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId
    ): String

    @GET("route/{routeId}/service/{serviceId}/trip/{tripId}/timetable.pb")
    suspend fun routeTripTimetable(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId,
        @Path("tripId") tripId: TripId
    ): RouteTripTimetableEndpoint

    @GET("route/{routeId}/service/{serviceId}/trip/{tripId}/timetable.pb.sha")
    suspend fun routeTripTimetableDigest(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId,
        @Path("tripId") tripId: TripId
    ): String

    @GET("services.pb")
    suspend fun services(): ServiceEndpoint

    @GET("services.pb.sha")
    suspend fun servicesDigest(): String

    @GET
    suspend fun markdownContent(@Url url: String): String

    @GET("content.pb")
    suspend fun content(): Pages

    @GET("content.pb.sha")
    suspend fun contentDigest(): String

}