package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.maps.MarkerIcon

actual class MapScope {
    actual fun zoomToArea(bounds: Bounds, padding: Int) {
    }

    actual fun zoomToArea(
        topLeft: Location,
        bottomRight: Location,
        padding: Int
    ) {
    }

    @Composable
    actual fun DebugZoomToArea(bounds: Bounds) {
    }

    actual fun zoomToPoint(location: Location, zoom: Float) {
    }

    @Composable
    actual fun Marker(
        location: Location,
        icon: MarkerIcon?
    ) {
    }

    @Composable
    actual fun Line(
        points: List<Location>,
        color: Color
    ) {
    }

}