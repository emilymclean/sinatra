package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.PlaceClient
import cl.emilym.sinatra.data.client.RemoteConfigClient
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.persistence.PlacePersistence
import org.koin.core.annotation.Factory

@Factory
class PlaceRepository(
    private val placeClient: PlaceClient,
    private val remoteConfigClient: RemoteConfigRepository,
    private val placePersistence: PlacePersistence
) {

    suspend fun available(): Boolean = remoteConfigClient.nominatimUrl() != null

    suspend fun search(query: String): List<Place> {
        return placeClient.search(query).also {
            placePersistence.save(it)
        }
    }

}