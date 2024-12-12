package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.canberraZoom
import cl.emilym.sinatra.ui.toCoordinateSpan
import cl.emilym.sinatra.ui.toNative
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKMapView
import kotlin.math.pow



@OptIn(ExperimentalForeignApi::class)
fun MapLocation.toNative(): CValue<CLLocationCoordinate2D> {
    return CLLocationCoordinate2DMake(
        latitude = lat,
        longitude = lng
    )
}

data class CameraDescription(
    val center: MapLocation,
    val coordinateSpan: CoordinateSpan
) {

    @OptIn(ExperimentalForeignApi::class)
    val region: CValue<MKCoordinateRegion> get() = createRegion(center, coordinateSpan)

}

class MapKitState(
    cameraDescription: CameraDescription
) {

    private var _map by mutableStateOf<MKMapView?>(null)
    val map = _map

    private val _cameraDescription = mutableStateOf(cameraDescription)
    var cameraDescription by _cameraDescription

    @OptIn(ExperimentalForeignApi::class)
    fun animate(cameraDescription: CameraDescription) {
        _cameraDescription.value = cameraDescription
        map?.setRegion(cameraDescription.region, true)
    }

    internal fun setMap(map: MKMapView?) {
        if (this._map == null && map == null) return
        if (this._map != null && map != null) {
            error("MapKitState may only be associated with one MKMapView at a time")
        }
        this._map = map
    }

    companion object {
        val Saver: Saver<MapKitState, MapKitSavedState> = Saver(
            save = { MapKitSavedState(it.cameraDescription) },
            restore = { MapKitState(it.cameraDescription) }
        )
    }

}

class MapKitSavedState(
    val cameraDescription: CameraDescription
)

@Composable
inline fun rememberMapKitState(
    key: String? = null,
    crossinline init: MapKitState.() -> Unit = {}
): MapKitState = rememberSaveable(key = key, saver = MapKitState.Saver) {
    MapKitState(
        CameraDescription(canberra, canberraZoom.toCoordinateSpan())
    ).apply(init)
}


@OptIn(ExperimentalForeignApi::class)
fun createRegion(location: MapLocation, span: CoordinateSpan): CValue<MKCoordinateRegion> {
    return MKCoordinateRegionMake(
        centerCoordinate = location.toNative(),
        span = span.toNative()
    )
}