package cl.emilym.sinatra.ui.navigation

import android.graphics.Point
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.maps.MarkerIcon
import cl.emilym.sinatra.ui.maps.toMaps
import cl.emilym.sinatra.ui.toMaps
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
    private val contentViewportSize: Size,
    private val bottomSheetHalfHeight: Float
): MapControl {
    
    private val visibleMapSize: Size
        get() = Size(contentViewportSize.width, contentViewportSize.height * (1 - bottomSheetHalfHeight))

    private val contentViewportAspect: Float get() = contentViewportSize.width / contentViewportSize.height
    private val visibleMapAspect: Float get() = visibleMapSize.width / visibleMapSize.height

    private val moveDownPx: Float
        get() = ((contentViewportSize.height / 2) - (visibleMapSize.height / 2)) * 1.75f

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

    @Composable
    @GoogleMapComposable
    actual fun Marker(location: Location, icon: MarkerIcon?, zoomThreshold: Float?, onClick: (() -> Unit)?) {
        com.google.maps.android.compose.Marker(
            rememberMarkerState(position = location.toMaps()),
            icon = icon?.bitmapDescriptor,
            anchor = icon?.anchor?.toMaps() ?: Offset(0.5f, 1.0f),
            onClick = onClick?.let { {
                onClick()
                true
            } } ?: { false },
            visible = zoomThreshold == null || cameraPositionState.position.zoom >= zoomThreshold
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

    actual override fun zoomToArea(
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
            visibleMapAspect
        )

        val screenBox = viewportBox.copy(
            bottomLeft = Point(
                viewportBox.bottomLeft.x,
                (viewportBox.bottomLeft.y + (viewportBox.width / contentViewportAspect)).toInt(),
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

    actual override fun zoomToArea(bounds: Bounds, padding: Int) {
        zoomToArea(bounds.topLeft, bounds.bottomRight, padding)
    }

    actual override fun zoomToPoint(
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

    @Composable
    @GoogleMapComposable
    actual fun Native(init: @GoogleMapComposable @Composable NativeMapScope.() -> Unit) {
        NativeMapScope(cameraPositionState).init()
    }

}

actual class NativeMapScope(
    val cameraPositionState: CameraPositionState
)

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