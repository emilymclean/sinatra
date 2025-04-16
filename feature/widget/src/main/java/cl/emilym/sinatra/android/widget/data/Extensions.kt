package cl.emilym.sinatra.android.widget.data

import cl.emilym.sinatra.android.widget.data.proto.UpcomingVehicleStopTime
import cl.emilym.sinatra.data.models.IStopTimetableTime

fun IStopTimetableTime.toProto(): UpcomingVehicleStopTime {
    return UpcomingVehicleStopTime.newBuilder()
        .setChildStopId(childStopId)
        .setRouteId(routeId)
        .setRouteCode(routeCode)
        .setServiceId(serviceId)
        .setTripId(tripId)
        .setArrivalTime(arrivalTime.instant.toEpochMilliseconds())
        .setDepartureTime(departureTime.instant.toEpochMilliseconds())
        .setHeading(heading)
        .setSequence(sequence)
        .build()
}