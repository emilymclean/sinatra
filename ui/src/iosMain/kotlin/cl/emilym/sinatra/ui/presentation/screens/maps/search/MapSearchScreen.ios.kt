package cl.emilym.sinatra.ui.presentation.screens.maps.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.ui.maps.MapScope
import cl.emilym.sinatra.ui.maps.MarkerIcon
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.NativeMapScope
import cl.emilym.sinatra.ui.maps.stopMarkerIcon
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen

@Composable
actual fun NativeMapScope.DrawMapSearchScreenMapNative(stops: List<Stop>) {
}

@Composable
actual fun MapScope.mapSearchScreenMapItems(stops: List<Stop>): List<MarkerItem> {
    val navigator = LocalNavigator.currentOrThrow
    val icon = stopMarkerIcon() ?: return listOf()

    val important = remember { stops.filter { it.important }.map {
        it.toMarkerItem(navigator, icon)
    } }
    val all = remember { stops.map {
        it.toMarkerItem(navigator, icon)
    } }

    return when {
        camera.zoom >= zoomThreshold -> all
        else -> important
    }
}

private fun Stop.toMarkerItem(
    navigator: Navigator,
    icon: MarkerIcon
): MarkerItem {
    return MarkerItem(
        location,
        icon = icon,
        onClick = {
            navigator.push(StopDetailScreen(id))
        },
        id = "mapSearchScreen-stop-${id}"
    )
}