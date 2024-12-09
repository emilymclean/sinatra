package cl.emilym.sinatra.ui.navigation

import android.graphics.Point
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

data class BoxDescriptor(
    val topRight: Point,
    val bottomLeft: Point
) {
    val width get() = topRight.x - bottomLeft.x
    val height get() = bottomLeft.y - topRight.y
}

actual class MapScope(
    private val cameraPositionState: CameraPositionState,
    private val screenSize: Size,
    private val bottomSheetHalfHeight: Float
) {

    private val viewportSize: Size
        get() = Size(screenSize.width, screenSize.height * (1 - bottomSheetHalfHeight))

    private val screenAspect: Float get() = screenSize.width / screenSize.height
    private val viewportAspect: Float get() = viewportSize.width / viewportSize.height

    private fun boxOverOther(box: BoxDescriptor, aspect: Float): BoxDescriptor {
        val width = box.width
        val height = box.height

        Napier.d("Box = ${box}, width = $width, height = $height")

        if (width > height) {
            val nHeight = width * aspect
            return BoxDescriptor(
                topRight = Point(box.topRight.x, (box.bottomLeft.y + ((height - nHeight) / 2)).toInt()),
                bottomLeft = Point(box.bottomLeft.x, (box.bottomLeft.y + ((nHeight - height) / 2)).toInt())
            )
        } else {
            val nWidth = height * aspect
            return BoxDescriptor(
                topRight = Point((box.bottomLeft.x + ((width - nWidth) / 2)).toInt(), box.topRight.y),
                bottomLeft = Point((box.bottomLeft.x + ((nWidth - width) / 2)).toInt(), box.bottomLeft.y)
            )
        }
    }

    private val moveDownPx: Float
        get() = ((screenSize.height / 2) - (viewportSize.height / 2)) * 4

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
        val original = listOf(topLeft.toMaps(), bottomRight.toMaps()).toBounds()
        val projection = cameraPositionState.projection ?: let {
            CoroutineScope(Dispatchers.Main).launch {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(
                    original,
                    padding
                ))
            }
            return
        }
        val viewportBox = boxOverOther(
            BoxDescriptor(
                projection.toScreenLocation(original.northeast),
                projection.toScreenLocation(original.southwest),
            ),
            viewportAspect
        )

        val screenBox = viewportBox.copy(
            bottomLeft = Point(
                viewportBox.topRight.x,
                (viewportBox.topRight.y + (viewportBox.width * screenAspect)).toInt(),
            )
        )

        CoroutineScope(Dispatchers.Main).launch {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(
                LatLngBounds(
                    projection.fromScreenLocation(screenBox.topRight),
                    projection.fromScreenLocation(screenBox.bottomLeft)
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

fun List<LatLng>.toBounds(): LatLngBounds {
    val topLeft = get(0)
    val bottomRight = get(1)
    return LatLngBounds(
        LatLng(bottomRight.latitude, topLeft.longitude),
        LatLng(topLeft.latitude, bottomRight.longitude)
    )
}