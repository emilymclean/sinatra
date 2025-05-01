package cl.emilym.sinatra.ui

import cafe.adriel.voyager.core.registry.ScreenProvider
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.kmp.serializable.Serializable
import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.ui.presentation.screens.maps.place.PlaceDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigateEntryScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigationLocation

sealed interface ScreenRoute: ScreenProvider, Serializable {}

fun Navigator.stopJourneyNavigation(stop: Stop) {
    push(NavigateEntryScreen(NavigationLocation.Stop(stop)))
}

fun Navigator.placeJourneyNavigation(place: Place) {
    push(NavigateEntryScreen(NavigationLocation.Place(place)))
}

fun Navigator.placeCardDefaultNavigation(place: Place) {
    when (FeatureFlags.PLACE_DETAIL_ENABLED) {
        true -> push(PlaceDetailScreen(place.id))
        else -> placeJourneyNavigation(place)
    }
}

fun Navigator.stopCardDefaultNavigation(stop: Stop) {
    push(StopDetailScreen(stop.id))
}

fun Navigator.routeCardDefaultNavigation(route: Route) {
    push(RouteDetailScreen(route.id))
}