package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.maps.MarkerIcon

actual class MapScope: MapControl {
    actual override fun zoomToArea(bounds: Bounds, padding: Int) {
    }

    actual override fun zoomToArea(
        topLeft: Location,
        bottomRight: Location,
        padding: Int
    ) {
    }

    actual override fun zoomToPoint(location: Location, zoom: Float) {
    }

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
        onClick: (() -> Unit)?
    ) {
    }

}