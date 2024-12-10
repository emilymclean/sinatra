package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.maps.MarkerIcon
import cl.emilym.sinatra.ui.presentation.theme.defaultLineColor

interface MapControl {
    fun zoomToArea(bounds: Bounds, padding: Int)

    fun zoomToArea(topLeft: Location, bottomRight: Location, padding: Int)

    fun zoomToPoint(location: Location, zoom: Float = 16f)
}

expect class MapScope: MapControl {

    override fun zoomToArea(bounds: Bounds, padding: Int)

    override fun zoomToArea(topLeft: Location, bottomRight: Location, padding: Int)

    override fun zoomToPoint(location: Location, zoom: Float)

    @Composable
    fun Marker(location: Location, icon: MarkerIcon? = null, zoomThreshold: Float? = null, onClick: (() -> Unit)? = null)

    @Composable
    fun Line(
        points: List<Location>,
        color: Color = defaultLineColor()
    )

}