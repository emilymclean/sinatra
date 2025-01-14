package cl.emilym.sinatra.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.Stop.Companion.importantStops
import cl.emilym.sinatra.data.models.StopAccessibility
import cl.emilym.sinatra.data.models.StopVisibility
import cl.emilym.sinatra.data.models.StopWheelchairAccessibility

@Entity
class StopEntity(
    @PrimaryKey val id: String,
    val parentStation: String?,
    val name: String,
    val simpleName: String? = null,
    val lat: Double,
    val lng: Double,
    val wheelchairAccessible: String,
    @ColumnInfo(defaultValue = "NULL")
    val visibleZoomedOut: Boolean? = null,
    @ColumnInfo(defaultValue = "1")
    val visibleZoomedIn: Boolean = StopVisibility.VISIBLE_ZOOMED_IN_DEFAULT,
    @ColumnInfo(defaultValue = "1")
    val showChildren: Boolean = StopVisibility.SHOW_CHILDREN_DEFAULT,
    @ColumnInfo(defaultValue = "NULL")
    val searchWeight: Double? = StopVisibility.SEARCH_WEIGHT_DEFAULT
) {

    fun toModel(): Stop {
        return Stop(
            id,
            parentStation,
            name,
            simpleName,
            MapLocation(lat, lng),
            StopAccessibility(
                StopWheelchairAccessibility.valueOf(wheelchairAccessible)
            ),
            StopVisibility(
                visibleZoomedOut ?: (id in importantStops),
                visibleZoomedIn,
                showChildren,
                searchWeight,
            )
        )
    }

    companion object {
        fun fromModel(stop: Stop): StopEntity {
            return StopEntity(
                stop.id,
                stop.parentStation,
                stop.name,
                stop._simpleName,
                stop.location.lat,
                stop.location.lng,
                stop.accessibility.wheelchair.name,
                stop.visibility.visibleZoomedOut,
                stop.visibility.visibleZoomedIn,
                stop.visibility.showChildren,
                stop.visibility.searchWeight
            )
        }
    }

}

data class StopEntityWithChildren(
    @Embedded val stop: StopEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentStation"
    )
    val children: List<StopEntity>,
)