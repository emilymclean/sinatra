package cl.emilym.sinatra.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.time
import cl.emilym.sinatra.data.models.toLong

@Entity
class StopTimetableTimeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val resource: String,
    val childStopId: String?,
    val routeId: String,
    val routeCode: String,
    val serviceId: String,
    val arrivalTime: Long,
    val departureTime: Long,
    val heading: String,
    val sequence: Int
) {

    fun toModel(): StopTimetableTime {
        return StopTimetableTime(
            childStopId,
            routeId,
            routeCode,
            serviceId,
            arrivalTime.time,
            departureTime.time,
            heading,
            sequence,
            null
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
                stop.arrivalTime.toLong(),
                stop.departureTime.toLong(),
                stop.heading,
                stop.sequence
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

    fun toModel(): StopTimetableTime {
        return stopTimetableTimeEntity.toModel().copy(
            route = route?.toModel()
        )
    }

}