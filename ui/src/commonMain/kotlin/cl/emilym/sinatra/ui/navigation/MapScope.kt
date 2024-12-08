package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.presentation.theme.defaultLineColor

expect class MapScope {

    fun ZoomToArea(bounds: Bounds, padding: Int)

    fun ZoomToArea(topLeft: Location, bottomRight: Location, padding: Int)

    @Composable
    fun Marker(location: Location)

    @Composable
    fun Line(
        points: List<Location>,
        color: Color = defaultLineColor()
    )

}