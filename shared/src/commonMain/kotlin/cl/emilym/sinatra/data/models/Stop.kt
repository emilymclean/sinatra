package cl.emilym.sinatra.data.models

import cl.emilym.gtfs.WheelchairStopAccessibility
import kotlinx.datetime.Instant
import kotlin.time.Duration

private val SIMPLE_TERMINATORS = arrayOf(" Platform", " at", " Plt")

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

    val simpleName: String get() {
        for (t in SIMPLE_TERMINATORS) {
            if (!name.contains(t)) continue
            return name.substring(0, name.indexOf(t))
        }
        return name
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

enum class StopWheelchairAccessibility(
    val isAccessible: Boolean
) {
    UNKNOWN(false), NONE(false), PARTIAL(true), FULL(true);

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
    override val arrivalTime: Time,
    override val departureTime: Time,
    val heading: String,
    val sequence: Int,
    val route: Route?
): StopTime {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.StopTimetableTime): StopTimetableTime {
            return StopTimetableTime(
                pb.childStopId,
                pb.routeId,
                pb.routeCode,
                pb.serviceId,
                parseTime(pb.arrivalTime),
                parseTime(pb.departureTime),
                pb.heading,
                pb.sequence,
                null
            )
        }
    }

}

sealed interface StationTime {
    val time: Time

    class Scheduled(
        override val time: Time
    ): StationTime
    class Live(
        override val time: Time
    ): StationTime
}