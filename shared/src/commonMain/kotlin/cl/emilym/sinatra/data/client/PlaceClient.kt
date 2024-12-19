package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.BuildInformation
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.network.NominatimApi
import org.koin.core.annotation.Factory

@Factory
class PlaceClient(
    private val nominatimApi: NominatimApi,
    private val buildInformation: BuildInformation
) {

    suspend fun search(query: String): List<Place> {
        val response = nominatimApi.search(
            query,
            userAgent = buildInformation.nominatimUserAgent,
            email = buildInformation.nominatimEmail
        )
        return response.map { Place.fromDto(it) }
    }

}