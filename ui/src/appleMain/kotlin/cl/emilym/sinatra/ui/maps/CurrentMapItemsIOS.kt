package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.sinatra.ui.navigation.MapScreen

@Composable
fun iosCurrentMapItems(): List<MapItem> {
    val navigator = LocalNavigator.currentOrThrow
    val currentScreen = navigator.lastItem
    val mapScreen = (currentScreen as? MapScreen) ?: return emptyList()

    return with(mapScreen) { mapItems() }
}