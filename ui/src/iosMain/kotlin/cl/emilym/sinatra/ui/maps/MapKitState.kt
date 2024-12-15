package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cl.emilym.sinatra.data.models.CoordinateSpan
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.ui.adjustForLatitude
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.canberraZoom
import cl.emilym.sinatra.ui.sinatraAllocArrayOf
import cl.emilym.sinatra.ui.toCoordinateSpan
import cl.emilym.sinatra.ui.toNative
import cl.emilym.sinatra.ui.toNativeUIColor
import cl.emilym.sinatra.ui.toShared
import cl.emilym.sinatra.ui.widgets.screenSize
import io.github.aakira.napier.Napier
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.CoreLocation.CLLocationCoordinate2D
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKMapView
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKPolyline
import platform.MapKit.addOverlay
import platform.MapKit.removeOverlay
import platform.UIKit.UIColor


data class CameraDescription(
    val center: MapLocation,
    val coordinateSpan: CoordinateSpan
) {

    @OptIn(ExperimentalForeignApi::class)
    val region: CValue<MKCoordinateRegion> get() = createRegion(center, coordinateSpan)

}

class ManagedMapItem<T: MapItem> @OptIn(ExperimentalForeignApi::class) constructor(
    val item: T,
    val annotation: MKAnnotationProtocol,
    val arena: Arena?
) {

    override fun equals(other: Any?): Boolean {
        return item == other
    }

    override fun hashCode(): Int {
        return item.hashCode()
    }

    override fun toString(): String {
        return item.toString()
    }
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

    private var managedItems = mutableMapOf<String, ManagedMapItem<*>>()

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
            _items = items
            val visitedIds = mutableMapOf<String, Unit>()
            val toAdd = mutableListOf<ManagedMapItem<*>>()
            val toRemove = mutableListOf<ManagedMapItem<*>>()

            fun removeItem(id: String) {
                managedItems.remove(id)?.let { toRemove.add(it) }
            }

            fun addItem(item: MapItem, annotation: MKAnnotationProtocol, arena: Arena? = null) {
                val managed = ManagedMapItem(
                    item, annotation, arena
                )
                toAdd.add(managed)
                managedItems[item.id] = managed
            }

            for (item in items) {
                visitedIds[item.id] = Unit
                val existing = managedItems[item.id]
                if (existing == item) continue

                when (item) {
                    is MarkerItem -> {
                        removeItem(item.id)

                        MarkerAnnotation(
                            item.id,
                            item.location,
                            item.icon,
                            item.visibleZoomRange
                        ).also {
                            addItem(item, it)
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
                            addItem(item, it, arena)
                        }
                    }
                }
            }

            toRemove.addAll(
                managedItems
                    .filterNot { visitedIds.containsKey(it.key) }
                    .map { managedItems.remove(it.key)!! }
            )

            for (remove in toRemove) {
                when (val annotation = remove.annotation) {
                    is MKOverlayProtocol -> map?.removeOverlay(annotation)
                    else -> map?.removeAnnotation(annotation)
                }
                remove.arena?.clear()
            }

            for (add in toAdd) {
                when (val annotation = add.annotation) {
                    is MKOverlayProtocol -> map?.addOverlay(annotation)
                    else -> map?.addAnnotation(annotation)
                }
            }

            Napier.d("Map updates: removed ${toRemove.size} items, added ${toAdd.size} items, updated ${items.size - toAdd.size} items")
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
                managedItems.forEach { it.value.arena?.clear() }
                managedItems.clear()
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
): MapKitState {
    val screenSize = screenSize()
    return rememberSaveable(key = key, saver = MapKitState.Saver) {
        MapKitState(
            CameraDescription(
                canberra,
                canberraZoom.toCoordinateSpan(
                    screenSize,
                ).adjustForLatitude(canberra.lat)
            ),
            listOf()
        ).apply(init)
    }
}


@OptIn(ExperimentalForeignApi::class)
fun createRegion(location: MapLocation, span: CoordinateSpan): CValue<MKCoordinateRegion> {
    return MKCoordinateRegionMake(
        centerCoordinate = location.toNative(),
        span = span.toNative()
    )
}