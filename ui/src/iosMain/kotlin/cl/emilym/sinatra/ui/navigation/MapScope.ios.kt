package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.maps.MapKitState
import cl.emilym.sinatra.ui.maps.MarkerIcon

actual class MapScope(
    val mapState: MapKitState,
    val mapControl: MapControl,
): MapControl by mapControl {

    @Composable
    actual fun Line(
        points: List<Location>,
        color: Color
    ) {
    }

    @Composable
    actual fun Marker(
        location: Location,
        icon: MarkerIcon?,
        zoomThreshold: Float?,
        onClick: (() -> Unit)?
    ) {
    }

    @Composable
    actual fun Native(init: @Composable NativeMapScope.() -> Unit) {
    }

}

actual class NativeMapScope