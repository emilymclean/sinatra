package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.canberraZoom
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpan
import platform.MapKit.MKCoordinateSpanMake
import kotlin.math.pow

@OptIn(ExperimentalForeignApi::class)
fun MapLocation.toMaps(): CValue<CLLocationCoordinate2D> {
    return CLLocationCoordinate2DMake(
        latitude = lat,
        longitude = lng
    )
}

@OptIn(ExperimentalForeignApi::class)
fun Float.toCoordinateSpan(): CValue<MKCoordinateSpan> {
    val span = 360 / 2f.pow(this)
    return MKCoordinateSpanMake(
        latitudeDelta = span.toDouble(),
        longitudeDelta = span.toDouble()
    )
}

class MapKitState @OptIn(ExperimentalForeignApi::class) constructor(
    val coordinate: MutableState<MapLocation> = mutableStateOf(canberra),
    val zoom: MutableState<Float> = mutableStateOf(canberraZoom)
) {

    companion object {
        public val Saver: Saver<MapKitState, MapKitSavedState> = Saver(
            save = { MapKitSavedState(it.coordinate.value, it.zoom.value) },
            restore = { MapKitState(mutableStateOf(it.coordinate), mutableStateOf(it.zoom)) }
        )
    }

}

class MapKitSavedState(
    val coordinate: MapLocation,
    val zoom: Float
)

@Composable
inline fun rememberMapKitState(
    key: String? = null,
    crossinline init: MapKitState.() -> Unit = {}
): MapKitState = rememberSaveable(key = key, saver = MapKitState.Saver) {
    MapKitState().apply(init)
}


@OptIn(ExperimentalForeignApi::class)
fun createRegion(location: MapLocation, zoom: Float): CValue<MKCoordinateRegion> {
    return MKCoordinateRegionMake(
        centerCoordinate = location.toMaps(),
        span = zoom.toCoordinateSpan()
    )
}