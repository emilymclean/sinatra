package cl.emilym.sinatra.ui.presentation.decompose.main

import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.TripId
import kotlinx.datetime.Instant

interface NavigationInstruction {
    data class RouteDetail(
        val routeId: RouteId,
        val serviceId: ServiceId? = null,
        val tripId: TripId? = null,
        val stopId: StopId? = null,
        val startOfDay: Instant? = null
    ): NavigationInstruction
}