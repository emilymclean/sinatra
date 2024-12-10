package cl.emilym.sinatra.ui.navigation

import android.content.Context
import android.graphics.Point
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.maps.MarkerIcon
import cl.emilym.sinatra.ui.maps.toMaps
import cl.emilym.sinatra.ui.toMaps
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.pow

data class GoogleMapsClusterItem<T>(
    val position: Location,
    val data: T
): ClusterItem {

    override fun getPosition(): LatLng = position.toMaps()
    override fun getTitle(): String? = null
    override fun getSnippet(): String? = null
    override fun getZIndex(): Float? = null

    fun toMultiplatform(): MapClusterItem<T> {
        return MapClusterItem(position, data)
    }

    companion object {

        fun <T> fromMultiplatform(item: MapClusterItem<T>): GoogleMapsClusterItem<T> {
            return GoogleMapsClusterItem(item.location, item.data)
        }

    }

}

class MapIconRenderer<T>(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<GoogleMapsClusterItem<T>>,
    private val singleMarkerIcon: (item: MapClusterItem<T>) -> MarkerIcon,
    private val clusterMarkerIcon: (items: List<MapClusterItem<T>>) -> MarkerIcon,
): DefaultClusterRenderer<GoogleMapsClusterItem<T>>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(
        item: GoogleMapsClusterItem<T>,
        markerOptions: MarkerOptions
    ) {
        super.onBeforeClusterItemRendered(item, markerOptions)
    }

    override fun onBeforeClusterRendered(
        cluster: Cluster<GoogleMapsClusterItem<T>>,
        markerOptions: MarkerOptions
    ) {
        super.onBeforeClusterRendered(cluster, markerOptions)
    }
}

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
        get() = ((screenSize.height / 2) - (viewportSize.height / 2)) * 2

    @Composable
    @GoogleMapComposable
    actual fun Marker(location: Location, icon: MarkerIcon?) {
        com.google.maps.android.compose.Marker(
            rememberMarkerState(position = location.toMaps()),
            icon = icon?.bitmapDescriptor,
            anchor = icon?.anchor?.toMaps() ?: Offset(0.5f, 1.0f)
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

    @OptIn(MapsComposeExperimentalApi::class)
    @Composable
    @GoogleMapComposable
    actual fun <T> Cluster(
        items: List<MapClusterItem<T>>,
        singleMarkerIcon: (item: MapClusterItem<T>) -> MarkerIcon,
        clusterMarkerIcon: (items: List<MapClusterItem<T>>) -> MarkerIcon
    ) {
        Clustering(
            items.map { GoogleMapsClusterItem.fromMultiplatform(it) },
            onClusterItemClick = {
                false
            },
            onClusterManager = { clusterManager ->
                (clusterManager.renderer as DefaultClusterRenderer<ClusterItem<T>>).minClusterSize = 2
            },
        )
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