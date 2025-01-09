package cl.emilym.sinatra.ui

import cafe.adriel.voyager.core.registry.ScreenProvider
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.kmp.serializable.Serializable
import cl.emilym.sinatra.data.models.ContentId
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigateEntryScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigationLocation
import kotlinx.datetime.Instant

sealed interface ScreenRoute: ScreenProvider, Serializable {
    data object MapSearch: ScreenRoute
    data class Content(
        val id: ContentId
    ): ScreenRoute
    data object About: ScreenRoute
    data object Favourite: ScreenRoute
    data class RouteDetail(
        val routeId: RouteId
    ): ScreenRoute
    data class TripDetail(
        val routeId: RouteId,
        val serviceId: ServiceId,
        val tripId: TripId,
        val stopId: StopId,
        val startOfDay: Instant
    ): ScreenRoute
    data class StopDetail(
        val stopId: StopId
    ): ScreenRoute
    data class NavigationEntry(
        val destination: NavigationLocation,
        val origin: NavigationLocation? = null
    ): ScreenRoute
}

fun Navigator.placeCardDefaultNavigation(place: Place) {
    push(NavigateEntryScreen(NavigationLocation.Place(place)))
}

fun Navigator.stopJourneyNavigation(stop: Stop) {
    push(NavigateEntryScreen(NavigationLocation.Stop(stop)))
}

fun Navigator.stopCardDefaultNavigation(stop: Stop) {
    push(StopDetailScreen(stop.id))
}

fun Navigator.routeCardDefaultNavigation(route: Route) {
    push(RouteDetailScreen(route.id))
}