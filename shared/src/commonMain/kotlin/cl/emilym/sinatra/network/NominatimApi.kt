package cl.emilym.sinatra.network

import cl.emilym.sinatra.data.models.dto.NominatimPlace
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Query

interface NominatimApi {

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "jsonv2",
        @Query("countrycodes") countryCodes: List<String> = listOf("au"),
        @Query("viewbox") viewBox: String = "148.730747,-35.107753,149.227330,-35.928921",
        @Query("bounded") bounded: Boolean = true,
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("email") email: String? = null,
        @Header("User-Agent") userAgent: String? = null
    ): List<NominatimPlace>

}