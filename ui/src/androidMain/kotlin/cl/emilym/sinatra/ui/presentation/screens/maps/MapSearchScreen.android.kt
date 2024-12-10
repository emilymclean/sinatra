package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.ui.maps.stopMarkerIcon
import cl.emilym.sinatra.ui.maps.toMaps
import cl.emilym.sinatra.ui.navigation.NativeMapScope
import cl.emilym.sinatra.ui.toMaps
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState

@Composable
@GoogleMapComposable
actual fun NativeMapScope.MapSearchScreenMapNative(
    stops: List<Stop>
) {
    val navigator = LocalNavigator.currentOrThrow
    val icon = stopMarkerIcon()
    val bitmapDescriptor = remember { icon.bitmapDescriptor }
    val anchor = remember { icon.anchor.toMaps() }
    val markerStates = stops.map { rememberMarkerState(position = it.location.toMaps()) }

    val visible = remember(cameraPositionState.position.zoom) { cameraPositionState.position.zoom >= 14f }
    for (i in stops.indices) {
        Marker(
            markerStates[i],
            icon = bitmapDescriptor,
            anchor = anchor,
            onClick = {
                navigator.push(StopDetailScreen(stops[i].id))
                true
            },
            visible = visible
        )
    }
}