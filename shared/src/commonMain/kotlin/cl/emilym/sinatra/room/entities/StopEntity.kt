package cl.emilym.sinatra.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Stop
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
    @ColumnInfo(defaultValue = "NULL")
    val visibleZoomedIn: Boolean? = null,
    @ColumnInfo(defaultValue = "0")
    val showChildren: Boolean = StopVisibility.SHOW_CHILDREN_DEFAULT,
    @ColumnInfo(defaultValue = "NULL")
    val searchWeight: Double? = StopVisibility.SEARCH_WEIGHT_DEFAULT,
    @ColumnInfo(defaultValue = "0")
    val hasRealtime: Boolean
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
                visibleZoomedOut ?: false,
                visibleZoomedIn ?: (parentStation == null),
                showChildren,
                searchWeight,
            ),
            hasRealtime
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
                stop.visibility.searchWeight,
                stop.hasRealtime
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