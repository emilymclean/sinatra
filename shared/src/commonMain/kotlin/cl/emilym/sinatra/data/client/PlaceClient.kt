package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.network.NominatimApi
import cl.emilym.sinatra.nullIfEmpty
import io.github.aakira.napier.Napier
import io.ktor.serialization.JsonConvertException
import org.koin.core.annotation.Factory
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Factory
class PlaceClient(
    private val nominatimApi: NominatimApi,
    private val remoteConfigRepository: RemoteConfigRepository
) {

    companion object {
        private val NOMINATIM_STATION_TYPES = listOf("bus_stop", "platform", "stop", "station")
        private val REMOVED_CATEGORIES = listOf<String>()
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun search(query: String): List<Place> {
        val response = nominatimApi.search(
            "https://${remoteConfigRepository.nominatimUrl() ?: return emptyList()}/search",
            query,
            userAgent = remoteConfigRepository.nominatimUserAgent(),
            email = remoteConfigRepository.nominatimEmail()
        )
        return response
            .asSequence()
            .filterNot { it.type in NOMINATIM_STATION_TYPES }
            .filterNot { it.category in REMOVED_CATEGORIES }
            .distinctBy { it.displayName }
            .distinctBy { it.dedupeKeys.nullIfEmpty() ?: listOf(Uuid.random().toHexString()) }
            .map { Place.fromDto(it) }
            .toList()
    }

    suspend fun reverse(location: MapLocation): Place? {
        try {
            val response = nominatimApi.reverse(
                "https://${remoteConfigRepository.nominatimUrl() ?: return null}/reverse",
                location.lat,
                location.lng,
                userAgent = remoteConfigRepository.nominatimUserAgent(),
                email = remoteConfigRepository.nominatimEmail()
            )
            return Place.fromDto(response)
        } catch (e: JsonConvertException) {
            return null
        }
    }

}