package cl.emilym.sinatra.data.models

import cl.emilym.gtfs.WheelchairStopAccessibility
import cl.emilym.kmp.serializable.Serializable
import kotlinx.datetime.Instant
import kotlin.time.Duration

private val SIMPLE_TERMINATORS = arrayOf(" Platform", " at", " Plt")

data class Stop(
    override val id: StopId,
    val parentStation: StopId?,
    val name: String,
    val _simpleName: String?,
    val location: MapLocation,
    val accessibility: StopAccessibility,
    val visibility: StopVisibility
): Serializable, Identifiable<StopId>, NavigationObject {

    companion object {

        fun fromPB(pb: cl.emilym.gtfs.Stop): Stop {
            return Stop(
                pb.id,
                pb.parentStation,
                pb.name,
                pb.simpleName,
                MapLocation.fromPB(pb.location),
                StopAccessibility.fromPB(pb.accessibility),
                StopVisibility.fromPB(pb, pb.visibility)
            )
        }
    }

    val simpleName: String get() {
        _simpleName?.let { return it }
        for (t in SIMPLE_TERMINATORS) {
            if (!name.contains(t)) continue
            return name.substring(0, name.indexOf(t))
        }
        return name
    }

}

data class StopVisibility(
    val visibleZoomedOut: Boolean,
    val visibleZoomedIn: Boolean,
    val showChildren: Boolean,
    val searchWeight: Double?
): Serializable {

    companion object {
        const val SHOW_CHILDREN_DEFAULT = false
        val SEARCH_WEIGHT_DEFAULT: Double? = null

        fun fromPB(stopPb: cl.emilym.gtfs.Stop, pb: cl.emilym.gtfs.StopVisibility?): StopVisibility {
            return StopVisibility(
                pb?.visibleZoomedOut ?: false,
                pb?.visibleZoomedIn ?: (stopPb.parentStation == null),
                pb?.showChildren ?: SHOW_CHILDREN_DEFAULT,
                pb?.searchWeight ?: SEARCH_WEIGHT_DEFAULT
            )
        }
    }

}

data class StopWithDistance(
    val stop: Stop,
    val distance: Kilometer
)

data class StopAccessibility(
    val wheelchair: StopWheelchairAccessibility
): Serializable {

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

data class StopWithChildren(
    val stop: Stop,
    val children: List<Stop>
)

data class StopTimetable(
    override val times: List<StopTimetableTime>
) : IStopTimetable {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.StopTimetable): StopTimetable {
            return StopTimetable(
                pb.times.map { StopTimetableTime.fromPB(it) }
            )
        }
    }

}

data class StopTimetableTime(
    override val childStopId: StopId?,
    override val routeId: RouteId,
    override val routeCode: RouteCode,
    override val serviceId: ServiceId,
    override val tripId: TripId,
    override val arrivalTime: Time,
    override val departureTime: Time,
    override val heading: String,
    override val sequence: Int,
    override val route: Route?
): IStopTimetableTime {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.StopTimetableTime): StopTimetableTime {
            return StopTimetableTime(
                pb.childStopId,
                pb.routeId,
                pb.routeCode,
                pb.serviceId,
                pb.tripId,
                Time.parse(pb.arrivalTime),
                Time.parse(pb.departureTime),
                pb.heading,
                pb.sequence,
                null
            )
        }
    }

    fun withTimeReference(startOfDay: Instant): StopTimetableTime {
        return copy(
            arrivalTime = arrivalTime.forDay(startOfDay),
            departureTime = departureTime.forDay(startOfDay),
        )
    }

    override val stationTime: TimetableStationTime
        get() = TimetableStationTime(
            StationTime.Scheduled(arrivalTime),
            StationTime.Scheduled(departureTime),
        )

}

sealed interface StationTime {
    val time: Time

    data class Scheduled(
        override val time: Time,
        val approximate: Boolean = false
    ): StationTime
    data class Live(
        override val time: Time,
        val delay: Duration
    ): StationTime
}

data class TimetableStationTime(
    val arrival: StationTime,
    val departure: StationTime
) {

    val times: List<StationTime> get() = listOf(arrival, departure)

}

fun StationTime.merge(route: Route): StationTime {
    return when (this) {
        is StationTime.Scheduled -> StationTime.Scheduled(
            time,
            route.approximateTimings
        )
        else -> this
    }
}