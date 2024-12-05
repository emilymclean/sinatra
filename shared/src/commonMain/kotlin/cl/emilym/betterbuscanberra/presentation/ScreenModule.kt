package cl.emilym.betterbuscanberra.presentation

import cafe.adriel.voyager.core.registry.screenModule
import cl.emilym.betterbuscanberra.ScreenRoute
import cl.emilym.betterbuscanberra.presentation.screens.HomeScreen

val sharedScreenModule = screenModule {
    register<ScreenRoute.HomeScreen> {
        HomeScreen()
    }
}