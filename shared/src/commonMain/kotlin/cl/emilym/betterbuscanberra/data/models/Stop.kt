package cl.emilym.betterbuscanberra.data.models

import cl.emilym.gtfs.WheelchairStopAccessibility
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

data class Stop(
    val id: StopId,
    val parentStation: StopId?,
    val name: String,
    val location: Location,
    val accessibility: StopAccessibility
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.Stop): Stop {
            return Stop(
                pb.id,
                pb.parentStation,
                pb.name,
                Location.fromPB(pb.location),
                StopAccessibility.fromPB(pb.accessibility)
            )
        }
    }

}

data class StopAccessibility(
    val wheelchair: StopWheelchairAccessibility
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.StopAccessibility): StopAccessibility {
            return StopAccessibility(
                StopWheelchairAccessibility.fromPB(pb.stopWheelchairAccessible)
            )
        }
    }

}

enum class StopWheelchairAccessibility {
    UNKNOWN, NONE, PARTIAL, FULL;

    companion object {

        fun fromPB(pb: WheelchairStopAccessibility): StopWheelchairAccessibility {
            return when (pb) {
                is WheelchairStopAccessibility.UNKNOWN, is WheelchairStopAccessibility.UNRECOGNIZED ->
                    UNKNOWN
                is WheelchairStopAccessibility.FULL -> FULL
                is WheelchairStopAccessibility.PARTIAL -> PARTIAL
                is WheelchairStopAccessibility.NONE -> NONE
            }
        }

    }
}

data class StopTimetable(
    val times: List<StopTimetableTime>
) {

    companion object {

        fun fromPB(pb: cl.emilym.gtfs.StopTimetable): StopTimetable {
            return StopTimetable(
                pb.times.map { StopTimetableTime.fromPB(it) }
            )
        }

    }

}

data class StopTimetableTime(
    val childStopId: StopId?,
    val routeId: RouteId,
    val routeCode: RouteCode,
    val serviceId: ServiceId,
    private val arrivalTime: Duration,
    private val departureTime: Duration,
    val heading: String,
    val sequence: Int
) {

    fun arrivalTime(date: Instant): Instant {
        return date + arrivalTime
    }

    fun departureTime(date: Instant): Instant {
        return date + departureTime
    }

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.StopTimetableTime): StopTimetableTime {
            return StopTimetableTime(
                pb.childStopId,
                pb.routeId,
                pb.routeCode,
                pb.serviceId,
                Duration.parseIsoString(pb.arrivalTime),
                Duration.parseIsoString(pb.departureTime),
                pb.heading,
                pb.sequence
            )
        }
    }

}