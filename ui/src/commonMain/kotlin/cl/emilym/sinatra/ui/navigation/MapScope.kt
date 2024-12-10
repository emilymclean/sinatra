package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.maps.MarkerIcon
import cl.emilym.sinatra.ui.presentation.theme.defaultLineColor

data class MapClusterItem<T>(
    val location: Location,
    val data: T
)

expect class MapScope {

    fun zoomToArea(bounds: Bounds, padding: Int)

    fun zoomToArea(topLeft: Location, bottomRight: Location, padding: Int)

    fun zoomToPoint(location: Location, zoom: Float = 16f)

    @Composable
    fun Marker(location: Location, icon: MarkerIcon? = null)

    @Composable
    fun <T> Cluster(
        items: List<MapClusterItem<T>>,
        singleMarkerIcon: (item: MapClusterItem<T>) -> MarkerIcon,
        clusterMarkerIcon: (items: List<MapClusterItem<T>>) -> MarkerIcon
    )

    @Composable
    fun Line(
        points: List<Location>,
        color: Color = defaultLineColor()
    )

}