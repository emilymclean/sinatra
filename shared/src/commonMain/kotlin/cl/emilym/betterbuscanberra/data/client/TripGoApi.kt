package cl.emilym.betterbuscanberra.data.client

import cl.emilym.betterbuscanberra.data.models.Latitude
import cl.emilym.betterbuscanberra.data.models.Longitude
import cl.emilym.betterbuscanberra.data.models.Meters
import cl.emilym.betterbuscanberra.data.models.Mode
import cl.emilym.betterbuscanberra.data.models.RouteBasic
import cl.emilym.betterbuscanberra.data.models.RouteDetail
import cl.emilym.betterbuscanberra.data.models.RouteDetailRequest
import cl.emilym.betterbuscanberra.data.models.RoutesRequest
import cl.emilym.betterbuscanberra.data.models.SearchResultResponse
import cl.emilym.betterbuscanberra.data.models.TimetableRequest
import cl.emilym.betterbuscanberra.data.models.TimetableResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

const val CANBERRA_REGION = "AU_ACT_Canberra"

interface TripGoApi {

    @POST("v1/info/routes.json")
    suspend fun routes(
        @Body request: RoutesRequest
    ): List<RouteBasic>

    @POST("v1/info/routeInfo.json")
    suspend fun routeDetails(
        @Body request: RouteDetailRequest
    ): RouteDetail

    @POST("v1/departures.json")
    suspend fun timetable(
        @Body request: TimetableRequest
    ): TimetableResponse

    @POST("v1/geocode.json")
    suspend fun search(
        @Query("q") search: String,
        @Query("allowGoogle") allowGoogle: Boolean = false,
        @Query("mode") mode: List<Mode> = listOf("pt_pub")
    ): SearchResultResponse

    @GET("v1/locations.json")
    suspend fun nearby(
        @Query("lat") lat: Latitude,
        @Query("lng") lng: Longitude,
        @Query("radius") radius: Meters
    )

}