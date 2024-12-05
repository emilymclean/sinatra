package cl.emilym.betterbuscanberra

import cafe.adriel.voyager.core.registry.ScreenProvider
import cl.emilym.kmp.serializable.Serializable

sealed interface ScreenRoute: ScreenProvider, Serializable {
    data object HomeScreen: ScreenRoute
}