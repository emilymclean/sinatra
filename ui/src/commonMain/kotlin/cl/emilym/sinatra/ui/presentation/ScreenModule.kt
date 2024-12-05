package cl.emilym.sinatra.ui.presentation

import cafe.adriel.voyager.core.registry.screenModule
import cl.emilym.sinatra.ui.ScreenRoute
import cl.emilym.sinatra.ui.presentation.screens.HomeScreen

val sharedScreenModule = screenModule {
    register<ScreenRoute.HomeScreen> {
        HomeScreen()
    }
}