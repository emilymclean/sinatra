package cl.emilym.sinatra.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class FavouriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val type: String,
    val routeId: String?,
    val stopId: String?,
    val placeId: String?,
    @ColumnInfo(defaultValue = "null")
    val heading: String?,
    @ColumnInfo(defaultValue = "null")
    val extra: String?,
    @ColumnInfo(defaultValue = "0")
    val order: Int
)

data class FavouriteEntityEntityWithStopAndRoute(
    @Embedded val favourite: FavouriteEntity,
    @Relation(
        parentColumn = "stopId",
        entityColumn = "id"
    )
    val stop: StopEntity?,
    @Relation(
        parentColumn = "routeId",
        entityColumn = "id"
    )
    val route: RouteEntity?,
    @Relation(
        parentColumn = "placeId",
        entityColumn = "id"
    )
    val place: PlaceEntity?
)