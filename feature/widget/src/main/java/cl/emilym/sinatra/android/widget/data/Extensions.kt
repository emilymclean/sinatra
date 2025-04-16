package cl.emilym.sinatra.android.widget.data

import cl.emilym.sinatra.android.widget.data.proto.UpcomingVehicleStopTime
import cl.emilym.sinatra.data.models.IStopTimetableTime

fun IStopTimetableTime.toProto(): UpcomingVehicleStopTime {
    return UpcomingVehicleStopTime.newBuilder()
        .setRouteCode(route?.displayCode ?: routeCode)
        .setRouteName(route?.name)
        .setArrivalTime(arrivalTime.instant.toEpochMilliseconds())
        .setDepartureTime(departureTime.instant.toEpochMilliseconds())
        .setHeading(heading)
        .build()
}