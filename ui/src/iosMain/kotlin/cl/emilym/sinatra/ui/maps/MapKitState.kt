package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cl.emilym.sinatra.data.models.CoordinateSpan
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.canberraZoom
import cl.emilym.sinatra.ui.matches
import cl.emilym.sinatra.ui.sinatraAllocArrayOf
import cl.emilym.sinatra.ui.toCoordinateSpan
import cl.emilym.sinatra.ui.toNative
import cl.emilym.sinatra.ui.toNativeUIColor
import cl.emilym.sinatra.ui.toShared
import io.github.aakira.napier.Napier
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.CoreLocation.CLLocationCoordinate2D
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKMapView
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolyline
import platform.MapKit.addOverlays
import platform.MapKit.removeOverlays
import platform.UIKit.UIColor


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
    @OptIn(ExperimentalForeignApi::class)
    private var managedArenas = mutableMapOf<String, Arena>()

    private val delegate = SinatraMapKitDelegate(::onAnnotationClick, ::onMapUpdate)

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

    private fun onAnnotationClick(annotation: MKAnnotationProtocol) {
        val id = when (annotation) {
            is MarkerAnnotation -> annotation.id
            else -> return
        }
        (items.firstOrNull { it.id == id } as? ClickableMapItem)?.onClick?.invoke()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun onMapUpdate() {
        val map = map ?: return
        _cameraDescription = CameraDescription(
            map.camera.centerCoordinate.toShared(),
            map.region.useContents { span.toShared() }
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    suspend fun updateItems(items: List<MapItem>) {
        lock.withLock {
            this._items = items
            val visitedIds = mutableMapOf<String, Unit>()
            val toAdd = mutableListOf<MKAnnotationProtocol>()
            val toRemove = mutableListOf<MKAnnotationProtocol>()
            val toRemoveArena = mutableListOf<Arena>()

            fun removeItem(id: String) {
                managedAnnotations.remove(id)?.let { toRemove.add(it) }
                managedArenas.remove(id)?.let { toRemoveArena.add(it) }
            }

            for (item in items) {
                visitedIds[item.id] = Unit
                val existing = managedAnnotations[item.id]

                when (item) {
                    is MarkerItem -> {
                        if (existing is MarkerAnnotation && existing.matches(item)) continue
                        removeItem(item.id)

                        MarkerAnnotation(
                            item.id,
                            item.location,
                            item.icon
                        ).also {
                            toAdd.add(it)
                            managedAnnotations[item.id] = it
                        }
                    }
                    is LineItem -> {
                        removeItem(item.id)

                        val arena = Arena()
                        val coordinates = arena.sinatraAllocArrayOf<CLLocationCoordinate2D>(
                            item.points.map { it.toNative() }
                        )

                        LineAnnotation(
                            MKPolyline.polylineWithCoordinates(
                                coordinates,
                                item.points.size.toULong()
                            ),
                            color = item.color?.toNativeUIColor() ?: UIColor.magentaColor
                        ).also {
                            toAdd.add(it)
                            managedAnnotations[item.id] = it
                            managedArenas[item.id] = arena
                        }
                    }
                }
            }

            val discardedIds = managedAnnotations.keys.filterNot { visitedIds.containsKey(it) }
            for (id in discardedIds) {
                toRemove.add(managedAnnotations.remove(id)!!)
                managedArenas.remove(id)?.let { toRemoveArena.add(it) }
            }

            map?.removeOverlays(toRemove.filterIsInstance<MKOverlayProtocol>())
            map?.removeAnnotations(toRemove.filter { it !is MKOverlayProtocol })
            toRemoveArena.forEach { it.clear() }
            map?.addOverlays(toAdd.filterIsInstance<MKOverlayProtocol>())
            map?.addAnnotations(toAdd.filter { it !is MKOverlayProtocol })

            Napier.d("Map updates: removed ${toRemove.size} items, added ${toAdd.size} items, updated ${items.size - toAdd.size} items; removed ${toRemoveArena.size} arenas")
            Napier.d("Map stats: ${managedAnnotations.size} items, ${managedArenas.size} arenas")
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

            if (map != null) {
                map.delegate = delegate
                map.setRegion(cameraDescription.region)
            } else {
                managedAnnotations.clear()
                managedArenas.values.forEach { it.clear() }
                managedArenas.clear()
            }

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