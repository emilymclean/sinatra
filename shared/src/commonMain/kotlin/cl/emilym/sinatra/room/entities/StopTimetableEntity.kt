package cl.emilym.sinatra.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.room.referenced
import cl.emilym.sinatra.room.time
import cl.emilym.sinatra.room.toLong
import kotlinx.datetime.Instant

@Entity
class StopTimetableTimeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val resource: String,
    val childStopId: String?,
    val routeId: String,
    val routeCode: String,
    val serviceId: String,
    val tripId: String,
    val arrivalTime: Long,
    val departureTime: Long,
    val heading: String,
    val sequence: Int,
    @ColumnInfo(defaultValue = "0")
    val last: Boolean
) {

    fun toModel(
        startOfDay: Instant? = null
    ): StopTimetableTime {
        return StopTimetableTime(
            childStopId,
            routeId,
            routeCode,
            serviceId,
            tripId,
            arrivalTime.time.referenced(startOfDay),
            departureTime.time.referenced(startOfDay),
            heading,
            sequence,
            null,
            last
        )
    }

    companion object {
        fun fromModel(stop: StopTimetableTime, resource: ResourceKey): StopTimetableTimeEntity {
            return StopTimetableTimeEntity(
                0,
                resource,
                stop.childStopId,
                stop.routeId,
                stop.routeCode,
                stop.serviceId,
                stop.tripId,
                stop.arrivalTime.toLong(),
                stop.departureTime.toLong(),
                stop.heading,
                stop.sequence,
                stop.last
            )
        }
    }

}

class StopTimetableTimeEntityWithRouteEntity(
    @Embedded val stopTimetableTimeEntity: StopTimetableTimeEntity,
    @Relation(
        parentColumn = "routeId",
        entityColumn = "id"
    )
    val route: RouteEntity?
) {

    fun toModel(startOfDay: Instant? = null): StopTimetableTime {
        return stopTimetableTimeEntity.toModel(startOfDay).copy(
            route = route?.toModel()
        )
    }

}