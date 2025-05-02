package cl.emilym.sinatra.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.nullIf
import cl.emilym.sinatra.nullIfBlank

@Entity
data class PlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val displayName: String,
    val lat: Double,
    val lng: Double
) {

    fun toModel(): Place {
        return Place(
            id,
            name.nullIfBlank(),
            displayName,
            MapLocation(
                lat, lng
            )
        )
    }

    companion object {

        fun fromModel(place: Place): PlaceEntity {
            return PlaceEntity(
                place.id,
                place.name ?: "",
                place.displayName,
                place.location.lat,
                place.location.lng
            )
        }

    }

}