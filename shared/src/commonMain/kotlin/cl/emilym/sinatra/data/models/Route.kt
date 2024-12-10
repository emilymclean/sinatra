package cl.emilym.sinatra.data.models

import cl.emilym.sinatra.nullIfEmpty
import kotlinx.datetime.Instant
import kotlin.text.Typography.times

data class Route(
    val id: RouteId,
    val code: RouteCode,
    val displayCode: String,
    val colors: ColorPair?,
    val name: String,
    val realTimeUrl: String?,
    val type: RouteType,
    val designation: String?
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.Route): Route {
            return Route(
                pb.id,
                pb.code,
                pb.displayCode ?: pb.code,
                pb.colors?.let { ColorPair.fromPB(it) },
                pb.name,
                pb.realTimeUrl,
                RouteType.fromPB(pb.type),
                pb.designation
            )
        }
    }

}

enum class RouteType {
    LIGHT_RAIL, BUS, OTHER;

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.RouteType): RouteType {
            return when(pb) {
                is cl.emilym.gtfs.RouteType.BUS -> BUS
                is cl.emilym.gtfs.RouteType.TRAM -> LIGHT_RAIL
                else -> OTHER
            }
        }
    }
}

data class RouteServiceTimetable(
    val trips: List<RouteTripInformation>
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.RouteTimetableEndpoint): RouteServiceTimetable {
            return RouteServiceTimetable(
                pb.trips.map { RouteTripInformation.fromPB(it) }
            )
        }
    }
}

data class RouteServiceCanonicalTimetable(
    val trip: RouteTripInformation
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.RouteCanonicalTimetableEndpoint): RouteServiceCanonicalTimetable {
            return RouteServiceCanonicalTimetable(
                RouteTripInformation.fromPB(pb.trip)
            )
        }
    }

}

data class RouteTripTimetable(
    val trip: RouteTripInformation,
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.RouteTripTimetableEndpoint): RouteTripTimetable {
            return RouteTripTimetable(
                RouteTripInformation.fromPB(pb.trip)
            )
        }
    }

}

data class RouteServiceAccessibility(
    override val bikesAllowed: ServiceBikesAllowed,
    override val wheelchairAccessible: ServiceWheelchairAccessible
): ServiceAccessibility {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.ServiceAccessibility): RouteServiceAccessibility {
            return RouteServiceAccessibility(
                ServiceBikesAllowed.fromPB(pb.bikesAllowed),
                ServiceWheelchairAccessible.fromPB(pb.wheelchairAccessible)
            )
        }
    }

}

data class RouteTripInformation(
    val startTime: Time?,
    val endTime: Time?,
    val accessibility: RouteServiceAccessibility,
    val heading: String?,
    val stops: List<RouteTripStop>
) {

    fun startTime(startOfDay: Instant) = startTime?.forDay(startOfDay)
    fun endTime(startOfDay: Instant) = endTime?.forDay(startOfDay)

    fun active(current: Instant, startOfDay: Instant): Boolean? {
        val start = startTime(startOfDay)
        val end = endTime(startOfDay)
        if (start == null || end == null) return null
        return start <= current && end >= current
    }

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.RouteTripInformation): RouteTripInformation {
            return RouteTripInformation(
                pb.startTime?.let { parseTime(it) },
                pb.endTime?.let { parseTime(it) },
                RouteServiceAccessibility.fromPB(pb.accessibility),
                pb.heading,
                pb.stops.map { RouteTripStop.fromPB(it) }
            )
        }
    }

    val stationTimes: List<TimetableStationTime>? get() = stops.mapNotNull {
        it.stationTime
    }.nullIfEmpty()

}

data class RouteTripStop(
    val stopId: StopId,
    override val arrivalTime: Time?,
    override val departureTime: Time?,
    val sequence: Int,
    val stop: Stop?
): StopTime {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.RouteTripStop): RouteTripStop {
            return RouteTripStop(
                pb.stopId,
                pb.arrivalTime?.let { parseTime(it) },
                pb.departureTime?.let { parseTime(it) },
                pb.sequence,
                null
            )
        }
    }

    val stationTime: TimetableStationTime? get() = when {
        arrivalTime != null && departureTime != null -> TimetableStationTime(
            StationTime.Scheduled(arrivalTime),
            StationTime.Scheduled(departureTime),
        )
        else -> null
    }

}