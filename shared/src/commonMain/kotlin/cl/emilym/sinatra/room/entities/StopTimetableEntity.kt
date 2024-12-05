package cl.emilym.sinatra.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.time
import cl.emilym.sinatra.data.models.toLong
import kotlin.time.Duration.Companion.milliseconds

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
            sequence
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