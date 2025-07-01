package cl.emilym.sinatra.data.models

import cl.emilym.sinatra.nullIfEmpty
import kotlinx.datetime.Instant

interface Identifiable<T> {
    val id: T
}

interface NavigationObject

interface IRouteTripInformation {
    val startTime: Time?
    val endTime: Time?
    val accessibility: RouteServiceAccessibility
    val heading: Heading?
    val stops: List<IRouteTripStop>

    val stationTimes: List<TimetableStationTime>? get() = stops.mapNotNull {
        it.stationTime
    }.nullIfEmpty()

    fun active(current: Instant): Boolean? {
        val startTime = startTime
        val endTime = endTime
        if (startTime == null || endTime == null) return null
        return current in startTime.instant..endTime.instant
    }
}

interface IRouteTripStop: StopTime {
    val stopId: StopId
    val stop: Stop?
}

interface IStopTimetable {
    val times: List<IStopTimetableTime>
    val stationTimes: List<TimetableStationTime> get() = times.map {
        TimetableStationTime(
            StationTime.Scheduled(it.arrivalTime),
            StationTime.Scheduled(it.departureTime),
        )
    }
}

interface IStopTimetableTime: StopTime {
    val childStopId: StopId?
    val routeId: RouteId
    val routeCode: RouteCode
    val serviceId: ServiceId
    val tripId: TripId
    override val arrivalTime: Time
    override val departureTime: Time
    val heading: String
    val last: Boolean
    val route: Route?
    val childStop: Stop?

    override val stationTime: TimetableStationTime
}