package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.presentation.theme.defaultLineColor

expect class MapScope {

    fun zoomToArea(bounds: Bounds, padding: Int)

    fun zoomToArea(topLeft: Location, bottomRight: Location, padding: Int)

    @Composable
    fun DebugZoomToArea(bounds: Bounds)

    fun zoomToPoint(location: Location, zoom: Float = 16f)

    @Composable
    fun Marker(location: Location)

    @Composable
    fun Line(
        points: List<Location>,
        color: Color = defaultLineColor()
    )

}