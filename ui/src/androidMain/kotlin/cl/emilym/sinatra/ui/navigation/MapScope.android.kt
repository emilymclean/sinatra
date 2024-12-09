package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.toMaps
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberMarkerState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.pow

actual class MapScope(
    private val cameraPositionState: CameraPositionState,
    private val screenSize: Size,
    private val bottomSheetHalfHeight: Float
) {

    private val moveDownPx: Float
        get() = ((screenSize.height / 2) - (screenSize.height * (1 - bottomSheetHalfHeight) / 2)) * 4

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

    actual fun zoomToArea(
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

    actual fun zoomToArea(bounds: Bounds, padding: Int) {
        zoomToArea(bounds.topLeft, bounds.bottomRight, padding)
    }

    actual fun zoomToPoint(
        location: Location,
        zoom: Float
    ) {
        val original = location.toMaps()
        val metersPerPx = metersPerPxAtZoom(zoom)
        Napier.d(
            "Screen height = ${screenSize.height}, visible height = ${screenSize.height * (1 - bottomSheetHalfHeight)}, moveDownPx = ${moveDownPx}, metersPerPx = ${metersPerPx} at zoom ${zoom}}"
        )
        CoroutineScope(Dispatchers.Main).launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(original.addMetersLatitude(metersPerPx * -moveDownPx), zoom)
            )
            cameraPositionState.position
        }
    }


}

const val EARTH_CIRCUMFERENCE = 6378137

fun metersPerPxAtZoom(zoom: Float): Float {
    return EARTH_CIRCUMFERENCE / (256 * 2f.pow(zoom))
}

fun LatLng.addMetersLatitude(meters: Float): LatLng {
    return LatLng(
        latitude + (meters / EARTH_CIRCUMFERENCE) * (180 / Math.PI),
        longitude
    )
}