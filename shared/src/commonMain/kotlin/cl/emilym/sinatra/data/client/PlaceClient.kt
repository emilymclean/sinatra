package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.BuildInformation
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.network.NominatimApi
import org.koin.core.annotation.Factory

@Factory
class PlaceClient(
    private val nominatimApi: NominatimApi,
    private val remoteConfigRepository: RemoteConfigRepository
) {

    companion object {
        private val NOMINATIM_STATION_TYPES = listOf("bus_stop", "platform", "railway", "stop", "station")
    }

    suspend fun search(query: String): List<Place> {
        val response = nominatimApi.search(
            query,
            userAgent = remoteConfigRepository.nominatimUserAgent(),
            email = remoteConfigRepository.nominatimEmail()
        )
        return response
            .filterNot { it.type in NOMINATIM_STATION_TYPES }
            .distinctBy { it.displayName }
            .map { Place.fromDto(it) }
    }

}