package cl.emilym.sinatra.data.models

import kotlinx.datetime.Instant

data class Route(
    val id: RouteId,
    val code: RouteCode,
    val displayCode: String,
    val colors: ColorPair?,
    val name: String,
    val realTimeUrl: String?,
    val type: RouteType
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
                RouteType.fromPB(pb.type)
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
    val startTime: Time,
    val endTime: Time,
    val accessibility: RouteServiceAccessibility,
    val stops: List<RouteTripStop>
) {

    fun startTime(startOfDay: Instant) = startTime.forDay(startOfDay)
    fun endTime(startOfDay: Instant) = endTime.forDay(startOfDay)

    fun active(current: Instant, startOfDay: Instant): Boolean {
        return startTime(startOfDay) <= current && endTime(startOfDay) >= current
    }

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.RouteTripInformation): RouteTripInformation {
            return RouteTripInformation(
                parseTime(pb.startTime),
                parseTime(pb.endTime),
                RouteServiceAccessibility.fromPB(pb.accessibility),
                pb.stops.map { RouteTripStop.fromPB(it) }
            )
        }
    }

}

data class RouteTripStop(
    val stopId: StopId,
    override val arrivalTime: Time,
    override val departureTime: Time,
    val sequence: Int
): StopTime {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.RouteTripStop): RouteTripStop {
            return RouteTripStop(
                pb.stopId,
                parseTime(pb.arrivalTime),
                parseTime(pb.departureTime),
                pb.sequence
            )
        }
    }

}