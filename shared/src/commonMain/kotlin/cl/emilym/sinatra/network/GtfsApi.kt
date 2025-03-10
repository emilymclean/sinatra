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
import com.google.transit.realtime.FeedMessage
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Url

interface GtfsApi {

    @GET("v1/stops.pb")
    suspend fun stops(): StopEndpoint

    @GET("v1/stops.pb.sha")
    suspend fun stopsDigest(): String

    @GET("v1/stop/{stopId}/timetable.pb")
    suspend fun stopTimetable(
        @Path("stopId") stopId: StopId
    ): StopTimetable

    @GET("v1/stop/{stopId}/timetable.pb.sha")
    suspend fun stopTimetableDigest(
        @Path("stopId") stopId: StopId
    ): String

    @GET("v1/routes.pb")
    suspend fun routes(): RouteEndpoint

    @GET("v1/routes.pb.sha")
    suspend fun routesDigest(): String

    @GET("v1/route/{routeId}/services.pb")
    suspend fun routeServices(
        @Path("routeId") routeId: RouteId
    ): RouteServicesEndpoint

    @GET("v1/route/{routeId}/services.pb.sha")
    suspend fun routeServicesDigest(
        @Path("routeId") routeId: RouteId
    ): String

    @GET("v1/route/{routeId}/service/{serviceId}/timetable.pb")
    suspend fun routeServiceTimetable(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId
    ): RouteTimetableEndpoint

    @GET("v1/route/{routeId}/service/{serviceId}/timetable.pb.sha")
    suspend fun routeServiceTimetableDigest(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId
    ): String

    @GET("v1/route/{routeId}/service/{serviceId}/canonical.pb")
    suspend fun routeServiceCanonicalTimetable(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId
    ): RouteCanonicalTimetableEndpoint

    @GET("v1/route/{routeId}/service/{serviceId}/canonical.pb.sha")
    suspend fun routeServiceCanonicalTimetableDigest(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId
    ): String

    @GET("v1/route/{routeId}/service/{serviceId}/trip/{tripId}/timetable.pb")
    suspend fun routeTripTimetable(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId,
        @Path("tripId") tripId: TripId
    ): RouteTripTimetableEndpoint

    @GET("v1/route/{routeId}/service/{serviceId}/trip/{tripId}/timetable.pb.sha")
    suspend fun routeTripTimetableDigest(
        @Path("routeId") routeId: RouteId,
        @Path("serviceId") serviceId: ServiceId,
        @Path("tripId") tripId: TripId
    ): String

    @GET("v1/services.pb")
    suspend fun services(): ServiceEndpoint

    @GET("v1/services.pb.sha")
    suspend fun servicesDigest(): String

    @GET
    suspend fun markdownContent(@Url url: String): String

    @GET
    suspend fun contentDynamic(@Url url: String): Pages

    @GET("v1/content.pb")
    suspend fun content(): Pages

    @GET("v1/content.pb.sha")
    suspend fun contentDigest(): String

    @GET("v1/content.android.pb")
    suspend fun contentAndroid(): Pages

    @GET("v1/content.android.pb.sha")
    suspend fun contentAndroidDigest(): String

    @GET("v1/content.ios.pb")
    suspend fun contentIos(): Pages

    @GET("v1/content.ios.pb.sha")
    suspend fun contentIosDigest(): String

    @GET("v1/network_graph.eng")
    suspend fun networkGraph(): ByteArray

    @GET("v1/network_graph.eng.sha")
    suspend fun networkGraphDigest(): String

    @GET("journey-config.pb")
    suspend fun journeyConfig(): ByteArray

    @GET("journey-config.pb.sha")
    suspend fun journeyConfigDigest(): String

    @GET
    @Headers("Accept: */*")
    suspend fun getLiveUpdates(@Url url: String): FeedMessage

}