package cl.emilym.sinatra.ui.presentation.decompose.main

import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigationLocation
import kotlinx.datetime.Instant

interface NavigationInstruction {

    data class StopDetail(
        val stopId: StopId
    ): NavigationInstruction

    data class RouteDetail(
        val routeId: RouteId,
        val serviceId: ServiceId? = null,
        val tripId: TripId? = null,
        val stopId: StopId? = null,
        val startOfDay: Instant? = null
    ): NavigationInstruction

    data class NavigationEntry(
        val destination: NavigationLocation? = null,
        val origin: NavigationLocation? = null
    ): NavigationInstruction

    data class SetSpecialFavourite(
        val type: SpecialFavouriteType
    ): NavigationInstruction

    data object ServiceAlerts: NavigationInstruction

}