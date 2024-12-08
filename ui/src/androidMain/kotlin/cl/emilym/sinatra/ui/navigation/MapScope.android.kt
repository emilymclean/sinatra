package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.toMaps
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.format.Padding
import kotlin.coroutines.CoroutineContext

actual class MapScope(
    private val cameraPositionState: CameraPositionState
) {

    @Composable
    @GoogleMapComposable
    actual fun Marker(location: Location) {
        com.google.maps.android.compose.Marker(
            rememberMarkerState(position = location.toMaps())
        )
    }

    @Composable
    @GoogleMapComposable
    actual fun Line(points: List<Location>, color: Color) {
        Polyline(
            points.map { it.toMaps() },
            color = color
        )
    }

    actual fun ZoomToArea(
        topLeft: Location,
        bottomRight: Location,
        padding: Int
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(
                LatLngBounds(
                    LatLng(bottomRight.lat, topLeft.lng),
                    LatLng(topLeft.lat, bottomRight.lng)
                ),
                padding
            ))
        }
    }

    actual fun ZoomToArea(bounds: Bounds, padding: Int) {
        ZoomToArea(bounds.topLeft, bounds.bottomRight, padding)
    }

    actual fun ZoomToPoint(
        location: Location,
        zoom: Float
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(location.toMaps(), zoom)
            )
        }
    }


}