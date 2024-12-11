package cl.emilym.sinatra.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class RecentVisitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val type: String,
    val routeId: String?,
    val stopId: String?,
)

data class RecentVisitEntityWithStopAndRoute(
    @Embedded val recentVisit: RecentVisitEntity,
    @Relation(
        parentColumn = "stopId",
        entityColumn = "id"
    )
    val stop: StopEntity?,
    @Relation(
        parentColumn = "routeId",
        entityColumn = "id"
    )
    val route: RouteEntity?
)