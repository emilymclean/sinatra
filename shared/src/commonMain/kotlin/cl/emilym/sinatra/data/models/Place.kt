package cl.emilym.sinatra.data.models

import cl.emilym.kmp.serializable.Serializable
import cl.emilym.sinatra.data.models.dto.NominatimPlace

data class Place(
    override val id: PlaceId,
    val name: String,
    val displayName: String,
    val location: MapLocation
): Serializable, Identifiable<PlaceId>, NavigationObject {

    companion object {

        fun fromDto(nominatimPlace: NominatimPlace): Place {
            return Place(
                "${nominatimPlace.placeId}",
                nominatimPlace.name,
                nominatimPlace.displayName,
                MapLocation(
                    nominatimPlace.lat.toDouble(),
                    nominatimPlace.lon.toDouble()
                )
            )
        }

    }

}