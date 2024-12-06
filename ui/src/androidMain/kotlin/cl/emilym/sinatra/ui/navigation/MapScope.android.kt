package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.toMaps
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberMarkerState

actual class MapScope {
    @Composable
    @GoogleMapComposable
    actual fun Marker(location: Location) {
        com.google.maps.android.compose.Marker(
            rememberMarkerState(position = location.toMaps())
        )
    }

    @Composable
    @GoogleMapComposable
    actual fun Line(points: List<Location>) {
        Polyline(points.map { it.toMaps() })
    }

}