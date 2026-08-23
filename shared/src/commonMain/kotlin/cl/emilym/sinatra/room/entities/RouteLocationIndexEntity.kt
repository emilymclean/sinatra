package cl.emilym.sinatra.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.RouteLocationIndex

@Entity
class RouteLocationIndexEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lat: Double,
    val lng: Double,
    val routeIds: String
) {

    fun toModel(): RouteLocationIndex {
        return RouteLocationIndex(
            point = MapLocation(
                lat = lat,
                lng = lng
            ),
            routeIds = when (routeIds) {
                "" -> emptyList()
                else -> routeIds.split(",")
            }
        )
    }

    companion object {
        fun fromModel(model: RouteLocationIndex): RouteLocationIndexEntity {
            return RouteLocationIndexEntity(
                id = 0,
                lat = model.point.lat,
                lng = model.point.lng,
                routeIds = model.routeIds.joinToString(",")
            )
        }
    }

}