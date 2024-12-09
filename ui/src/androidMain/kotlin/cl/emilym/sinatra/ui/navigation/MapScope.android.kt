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
import com.google.maps.android.compose.Polygon
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

    val aspect get() = width.toFloat() / height
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
        val boxAspect = box.aspect
        val width = if (boxAspect > aspect) box.width.toFloat() else (box.height * aspect)
        val height = if (boxAspect <= aspect) box.height.toFloat() else (box.width / aspect)

        return BoxDescriptor(
            topRight = Point(
                (box.bottomLeft.x + (width / 2) + (box.width / 2)).toInt(),
                (box.topRight.y - (height / 2) + (box.height / 2)).toInt()
            ),
            bottomLeft = Point(
                (box.bottomLeft.x - (width / 2) + (box.width / 2)).toInt(),
                (box.topRight.y + (height / 2) + (box.height / 2)).toInt()
            )
        )
    }

    private val moveDownPx: Float
        get() = ((screenSize.height / 2) - (viewportSize.height / 2))

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
                    0
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
                viewportBox.bottomLeft.x,
                (viewportBox.bottomLeft.y + (viewportBox.width / screenAspect)).toInt(),
            )
        )

        CoroutineScope(Dispatchers.Main).launch {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(
                LatLngBounds(
                    projection.fromScreenLocation(screenBox.bottomLeft),
                    projection.fromScreenLocation(screenBox.topRight)
                ),
                padding
            ))
        }
    }

    actual fun zoomToArea(bounds: Bounds, padding: Int) {
        zoomToArea(bounds.topLeft, bounds.bottomRight, padding)
    }

    @Composable
    @GoogleMapComposable
    actual fun DebugZoomToArea(bounds: Bounds) {
//        val original = listOf(bounds.topLeft.toMaps(), bounds.bottomRight.toMaps()).toBounds()
//        val projection = cameraPositionState.projection ?: return
//        val viewportBox = boxOverOther(
//            BoxDescriptor(
//                projection.toScreenLocation(original.northeast),
//                projection.toScreenLocation(original.southwest),
//            ),
//            viewportAspect
//        )
//
//        val screenBox = viewportBox
//        val screenBox = viewportBox.copy(
//            bottomLeft = Point(
//                viewportBox.bottomLeft.x,
//                (viewportBox.bottomLeft.y + (viewportBox.width / screenAspect)).toInt(),
//            )
//        )

//        val sBTopRight = projection.fromScreenLocation(screenBox.topRight)
//        val sBBottomLeft = projection.fromScreenLocation(screenBox.bottomLeft)
//        Polygon(
//            listOf(
//                LatLng(sBTopRight.latitude, sBBottomLeft.longitude),
//                LatLng(sBTopRight.latitude, sBTopRight.longitude),
//                LatLng(sBBottomLeft.latitude, sBTopRight.longitude),
//                LatLng(sBBottomLeft.latitude, sBBottomLeft.longitude),
//            )
//        )
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