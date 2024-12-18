package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.PlaceClient
import cl.emilym.sinatra.data.client.RemoteConfigClient
import cl.emilym.sinatra.data.models.Place
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory

@Factory
class PlaceRepository(
    private val placeClient: PlaceClient,
    private val remoteConfigClient: RemoteConfigClient
) {

    suspend fun available(): Boolean = remoteConfigClient.nominatimUrl() != null

    suspend fun search(query: String): List<Place> {
        return placeClient.search(query).also {
            Napier.d("Searched for \"$query\", found $it")
        }
    }

}