package cl.emilym.sinatra.ui.presentation.screens.maps.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.NativeMapScope
import cl.emilym.sinatra.ui.maps.stopMarkerIcon
import cl.emilym.sinatra.ui.maps.toNative
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen
import cl.emilym.sinatra.ui.toNative
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState

@Composable
@GoogleMapComposable
actual fun NativeMapScope.DrawMapSearchScreenMapNative(stops: List<Stop>) {
    val navigator = LocalNavigator.currentOrThrow
    val icon = stopMarkerIcon()!!
    val bitmapDescriptor = remember { icon.bitmapDescriptor }
    val anchor = remember { icon.anchor.toNative() }
    val markerStates = stops.map { rememberMarkerState(position = it.location.toNative()) }

    val visible = remember(zoom) { zoom >= zoomThreshold }
    for (i in stops.indices) {
        val stop = stops[i]
        Marker(
            markerStates[i],
            icon = bitmapDescriptor,
            anchor = anchor,
            onClick = {
                navigator.push(StopDetailScreen(stop.id))
                true
            },
            visible = (visible && stop.visibility.visibleZoomedIn) || (!visible && stop.visibility.visibleZoomedOut),
            contentDescription = "Stop ${stop.name}"
        )
    }
}

@Composable
actual fun mapSearchScreenMapItems(stops: List<Stop>): List<MarkerItem> = listOf()