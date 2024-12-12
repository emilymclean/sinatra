package cl.emilym.sinatra.ui.presentation.screens.maps.search

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.NativeMapScope
import cl.emilym.sinatra.ui.maps.stopMarkerIcon
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen

@Composable
actual fun NativeMapScope.DrawMapSearchScreenMapNative(stops: List<Stop>) {
}

@Composable
actual fun mapSearchScreenMapItems(stops: List<Stop>): List<MarkerItem> {
    val navigator = LocalNavigator.currentOrThrow
    val icon = stopMarkerIcon() ?: return listOf()

    return stops.map {
        MarkerItem(
            it.location,
            icon = icon,
            onClick = {
                navigator.push(StopDetailScreen(it.id))
                true
            },
        )
    }
}