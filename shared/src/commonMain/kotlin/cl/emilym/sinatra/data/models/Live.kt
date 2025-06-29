package cl.emilym.sinatra.data.models

data class LiveRouteTripInformation(
    override val startTime: Time?,
    override val endTime: Time?,
    override val accessibility: RouteServiceAccessibility,
    override val heading: String?,
    override val stops: List<LiveRouteTripStop>,
    override val stationTimes: List<TimetableStationTime>
): IRouteTripInformation {

    companion object {
        fun fromOther(item: IRouteTripInformation, stationTimes: List<TimetableStationTime>): LiveRouteTripInformation {
            return LiveRouteTripInformation(
                item.startTime,
                item.endTime,
                item.accessibility,
                item.heading,
                item.stops.mapIndexed { i, it ->
                    LiveRouteTripStop.fromOther(
                        it, stationTimes.getOrNull(i)
                    )
                },
                stationTimes
            )
        }
    }

}

data class LiveRouteTripStop(
    override val stopId: StopId,
    override val arrivalTime: Time?,
    override val departureTime: Time?,
    override val sequence: Int,
    override val stop: Stop?,
    override val stationTime: TimetableStationTime?
): IRouteTripStop {

    companion object {
        fun fromOther(item: IRouteTripStop, stationTime: TimetableStationTime?): LiveRouteTripStop {
            return LiveRouteTripStop(
                item.stopId,
                item.arrivalTime,
                item.departureTime,
                item.sequence,
                item.stop,
                stationTime
            )
        }
    }

}

data class LiveStopTimetable(
    override val times: List<LiveStopTimetableTime>,
    override val stationTimes: List<TimetableStationTime>
): IStopTimetable {

    companion object {
        fun fromOther(item: IStopTimetable, stationTimes: List<TimetableStationTime>): LiveStopTimetable {
            return LiveStopTimetable(
                item.times.mapIndexed { i, it ->
                    LiveStopTimetableTime.fromOther(it, stationTimes[i])
                },
                stationTimes
            )
        }
    }

}

data class LiveStopTimetableTime(
    override val childStopId: StopId?,
    override val routeId: RouteId,
    override val routeCode: RouteCode,
    override val serviceId: ServiceId,
    override val tripId: TripId,
    override val arrivalTime: Time,
    override val departureTime: Time,
    override val heading: String,
    override val sequence: Int,
    override val route: Route?,
    override val stationTime: TimetableStationTime,
    override val last: Boolean
): IStopTimetableTime {

    companion object {
        fun fromOther(item: IStopTimetableTime, stationTime: TimetableStationTime): LiveStopTimetableTime {
            return LiveStopTimetableTime(
                item.childStopId,
                item.routeId,
                item.routeCode,
                item.serviceId,
                item.tripId,
                item.arrivalTime,
                item.departureTime,
                item.heading,
                item.sequence,
                item.route,
                stationTime,
                last = item.last
            )
        }
    }

}