package cl.emilym.sinatra.data.models

data class Route(
    override val id: RouteId,
    val code: RouteCode,
    val displayCode: String,
    val colors: ColorPair?,
    val name: String,
    val realTimeUrl: String?,
    val type: RouteType,
    val designation: String?,
    val routeVisibility: RouteVisibility
): Identifiable<RouteId>, NavigationObject {

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
                pb.designation,
                RouteVisibility.fromPB(pb.routeVisibility)
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

data class RouteVisibility(
    val hidden: Boolean,
    val searchWeight: Double?
) {

    companion object {
        const val HIDDEN_DEFAULT = false
        val SEARCH_WEIGHT_DEFAULT: Double? = null

        fun fromPB(pb: cl.emilym.gtfs.RouteVisibility?): RouteVisibility {
            return RouteVisibility(
                pb?.hidden ?: HIDDEN_DEFAULT,
                pb?.searchWeight ?: SEARCH_WEIGHT_DEFAULT
            )
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
    override val startTime: Time?,
    override val endTime: Time?,
    override val accessibility: RouteServiceAccessibility,
    override val heading: String?,
    override val stops: List<RouteTripStop>
) : IRouteTripInformation {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.RouteTripInformation): RouteTripInformation {
            return RouteTripInformation(
                pb.startTime?.let { Time.parse(it) },
                pb.endTime?.let { Time.parse(it) },
                RouteServiceAccessibility.fromPB(pb.accessibility),
                pb.heading,
                pb.stops.map { RouteTripStop.fromPB(it) }
            )
        }
    }

}

data class RouteTripStop(
    override val stopId: StopId,
    override val arrivalTime: Time?,
    override val departureTime: Time?,
    override val sequence: Int,
    override val stop: Stop?
): IRouteTripStop {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.RouteTripStop): RouteTripStop {
            return RouteTripStop(
                pb.stopId,
                pb.arrivalTime?.let { Time.parse(it) },
                pb.departureTime?.let { Time.parse(it) },
                pb.sequence,
                null
            )
        }
    }

}