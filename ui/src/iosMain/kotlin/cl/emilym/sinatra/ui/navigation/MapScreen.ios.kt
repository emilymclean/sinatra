package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
actual fun MapScope.CurrentMapContent() {
    val navigator = LocalNavigator.currentOrThrow
    val currentScreen = navigator.lastItem
    val mapScreen = (currentScreen as? MapScreen) ?: return

    navigator.saveableState("mapOverlayScreen") {
        with(mapScreen) {
            MapContent()
        }
    }
}