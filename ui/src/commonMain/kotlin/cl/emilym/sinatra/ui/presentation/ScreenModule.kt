package cl.emilym.sinatra.ui.presentation

import cafe.adriel.voyager.core.registry.screenModule
import cl.emilym.sinatra.data.repository.ContentRepository
import cl.emilym.sinatra.ui.ScreenRoute
import cl.emilym.sinatra.ui.presentation.screens.AboutScreen
import cl.emilym.sinatra.ui.presentation.screens.ContentScreen
import cl.emilym.sinatra.ui.presentation.screens.FavouriteScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigateEntryScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.search.MapSearchScreen

val sharedScreenModule = screenModule {
    register<ScreenRoute.MapSearch> {
        MapSearchScreen()
    }
    register<ScreenRoute.About> {
        AboutScreen()
    }
    register<ScreenRoute.Favourite> {
        FavouriteScreen()
    }
    register<ScreenRoute.Content> {
        when (it.id) {
            ContentRepository.ABOUT_ID -> AboutScreen()
            else -> ContentScreen(it.id)
        }
    }
    register<ScreenRoute.RouteDetail> {
        RouteDetailScreen(it.routeId)
    }
    register<ScreenRoute.TripDetail> {
        RouteDetailScreen(
            it.routeId,
            it.serviceId,
            it.tripId,
            it.stopId,
            it.startOfDay
        )
    }
    register<ScreenRoute.StopDetail> {
        StopDetailScreen(
            it.stopId
        )
    }
    register<ScreenRoute.NavigationEntry> {
        NavigateEntryScreen(
            it.destination,
            it.origin
        )
    }
}