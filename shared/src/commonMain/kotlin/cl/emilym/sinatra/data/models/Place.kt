package cl.emilym.sinatra.data.models

import cl.emilym.sinatra.data.models.dto.NominatimPlace

data class Place(
    val id: String,
    val name: String,
    val displayName: String,
    val location: MapLocation
): NavigationLocation {

    override val navigationName: String
        get() = name

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