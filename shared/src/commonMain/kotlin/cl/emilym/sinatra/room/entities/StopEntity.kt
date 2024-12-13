package cl.emilym.sinatra.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopAccessibility
import cl.emilym.sinatra.data.models.StopWheelchairAccessibility

@Entity
class StopEntity(
    @PrimaryKey val id: String,
    val parentStation: String?,
    val name: String,
    val lat: Double,
    val lng: Double,
    val wheelchairAccessible: String
) {

    fun toModel(): Stop {
        return Stop(
            id,
            parentStation,
            name,
            MapLocation(lat, lng),
            StopAccessibility(
                StopWheelchairAccessibility.valueOf(wheelchairAccessible)
            )
        )
    }

    companion object {
        fun fromModel(stop: Stop): StopEntity {
            return StopEntity(
                stop.id,
                stop.parentStation,
                stop.name,
                stop.location.lat,
                stop.location.lng,
                stop.accessibility.wheelchair.name
            )
        }
    }

}