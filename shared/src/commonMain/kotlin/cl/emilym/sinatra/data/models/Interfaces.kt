package cl.emilym.sinatra.data.models

import cl.emilym.sinatra.nullIfEmpty
import kotlinx.datetime.Instant

interface IRouteTripInformation {
    val startTime: Time?
    val endTime: Time?
    val accessibility: RouteServiceAccessibility
    val heading: String?
    val stops: List<IRouteTripStop>

    val stationTimes: List<TimetableStationTime>? get() = stops.mapNotNull {
        it.stationTime
    }.nullIfEmpty()

    fun startTime(startOfDay: Instant): Instant? = startTime?.forDay(startOfDay)
    fun endTime(startOfDay: Instant): Instant? = endTime?.forDay(startOfDay)
    fun active(current: Instant, startOfDay: Instant): Boolean? {
        val start = startTime(startOfDay)
        val end = endTime(startOfDay)
        if (start == null || end == null) return null
        return current in start..end
    }
}

interface IRouteTripStop: StopTime {
    val stopId: StopId
    val sequence: Int
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
    val sequence: Int
    val route: Route?

    override val stationTime: TimetableStationTime
        get() = TimetableStationTime(
            StationTime.Scheduled(arrivalTime),
            StationTime.Scheduled(departureTime),
        )
}