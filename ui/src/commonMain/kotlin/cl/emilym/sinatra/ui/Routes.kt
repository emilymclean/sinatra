package cl.emilym.sinatra.ui

import cafe.adriel.voyager.core.registry.ScreenProvider
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.kmp.serializable.Serializable
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigateEntryScreen

sealed interface ScreenRoute: ScreenProvider, Serializable {
    data object HomeScreen: ScreenRoute
}

fun Navigator.placeCardDefaultNavigation(place: Place) {
    push(NavigateEntryScreen(place.location, place))
}

fun Navigator.stopJourneyNavigation(stop: Stop) {
    push(NavigateEntryScreen(stop.location, stop))
}

fun Navigator.stopCardDefaultNavigation(stop: Stop) {
    push(StopDetailScreen(stop.id))
}

fun Navigator.routeCardDefaultNavigation(route: Route) {
    push(RouteDetailScreen(route.id))
}