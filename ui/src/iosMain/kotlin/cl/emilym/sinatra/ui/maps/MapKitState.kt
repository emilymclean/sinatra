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
import io.github.aakira.napier.Napier
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineView


data class CameraDescription(
    val center: MapLocation,
    val coordinateSpan: CoordinateSpan
) {

    @OptIn(ExperimentalForeignApi::class)
    val region: CValue<MKCoordinateRegion> get() = createRegion(center, coordinateSpan)

}

class MapKitState(
    cameraDescription: CameraDescription,
    items: List<MapItem>
) {

    private val lock = Mutex()

    private var _map by mutableStateOf<MKMapView?>(null)
    val map: MKMapView?
        get() = _map

    private var _items: List<MapItem> = items
    val items get() = _items
    private var managedAnnotations = mutableMapOf<String, MKAnnotationProtocol>()

    private var _cameraDescription by mutableStateOf(cameraDescription)
    @OptIn(ExperimentalForeignApi::class)
    var cameraDescription: CameraDescription
        get() = _cameraDescription
        set(value) {
            _cameraDescription = value
            map?.setRegion(value.region)
        }

    @OptIn(ExperimentalForeignApi::class)
    fun animate(description: CameraDescription) {
        _cameraDescription = description
        Napier.d("Map update called, map is null = ${map == null}, (description = $description)")
        map?.setRegion(description.region, true)
    }

    @OptIn(ExperimentalForeignApi::class)
    fun animate(location: MapLocation) {
        val description = cameraDescription.copy(
            center = location
        )
        _cameraDescription = description
        map?.setRegion(description.region, true)
    }

    suspend fun updateItems(items: List<MapItem>) {
        lock.withLock {
            this._items = items
            val visitedIds = mutableMapOf<String, Unit>()
            val toAdd = mutableListOf<MKAnnotationProtocol>()
            val toRemove = mutableListOf<MKAnnotationProtocol>()
            for (item in items) {
                visitedIds[item.id] = Unit
                val existing = managedAnnotations[item.id] ?: run {
                    when (item) {
                        is MarkerItem -> MKPointAnnotation()
                        else -> null
                    }.also {
                        it?.let { toAdd.add(it) }
                    }
                }

                when (item) {
                    is MarkerItem -> {
                        updatePointAnnotation(existing as? MKPointAnnotation ?: continue, item)
                    }
                }
            }

            for (annotationKey in managedAnnotations.keys) {
                if (visitedIds.containsKey(annotationKey)) continue
                toRemove.add(managedAnnotations.remove(annotationKey)!!)
            }

            map?.removeAnnotations(toRemove)
            map?.addAnnotations(toAdd)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    suspend fun setMap(map: MKMapView?) {
        lock.withLock {
            if (this._map == null && map == null) return
            if (this._map != null && map != null) {
                error("MapKitState may only be associated with one MKMapView at a time")
            }
            this._map = map
            map?.setRegion(cameraDescription.region)
            Napier.d("Map in MapKitState is null = ${map == null}")
        }
    }

    companion object {
        val Saver: Saver<MapKitState, MapKitSavedState> = Saver(
            save = { MapKitSavedState(it.cameraDescription, it.items) },
            restore = { MapKitState(it.cameraDescription, it.items) }
        )
    }

}

class MapKitSavedState(
    val cameraDescription: CameraDescription,
    val items: List<MapItem>
)

@Composable
inline fun rememberMapKitState(
    key: String? = null,
    crossinline init: MapKitState.() -> Unit = {}
): MapKitState = rememberSaveable(key = key, saver = MapKitState.Saver) {
    MapKitState(
        CameraDescription(canberra, canberraZoom.toCoordinateSpan()),
        listOf()
    ).apply(init)
}


@OptIn(ExperimentalForeignApi::class)
fun createRegion(location: MapLocation, span: CoordinateSpan): CValue<MKCoordinateRegion> {
    return MKCoordinateRegionMake(
        centerCoordinate = location.toNative(),
        span = span.toNative()
    )
}