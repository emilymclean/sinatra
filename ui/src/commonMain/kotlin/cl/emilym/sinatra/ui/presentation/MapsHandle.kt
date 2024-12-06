package cl.emilym.sinatra.ui.presentation

import androidx.compose.ui.node.WeakReference
import cafe.adriel.voyager.core.concurrent.ThreadSafeMap
import cl.emilym.kmp.serializable.Serializable
import cl.emilym.sinatra.data.models.Location

typealias MapObjectKey = String

interface MapsHandle {

    fun showPoint(position: Location)
    fun showArea(topLeft: Location, bottomRight: Location)

    fun addPolygon(shape: List<Location>): MapObjectKey
    fun addLine(shape: List<Location>): MapObjectKey
    fun addMarker(position: Location): MapObjectKey

    fun delete(key: MapObjectKey)

}

interface MapObject: Serializable {
    val key: MapObjectKey
}

class PolygonMapObject(
    override val key: MapObjectKey,
    val shape: List<Location>
): MapObject

class LineMapObject(
    override val key: MapObjectKey,
    val shape: List<Location>
): MapObject

class MarkerMapObject(
    override val key: MapObjectKey,
    val position: Location
): MapObject

expect fun generateMapObjectKey(): MapObjectKey

private class DefaultMapsHandle(
    private val mapsManagerHandle: WeakReference<MapsManagerHandle>
): MapsHandle {

    val items = ThreadSafeMap<MapObjectKey, MapObject>()

    override fun showPoint(position: Location) {
        mapsManagerHandle.get()?.show(position)
    }

    override fun showArea(topLeft: Location, bottomRight: Location) {
        mapsManagerHandle.get()?.show(topLeft, bottomRight)
    }

    override fun addPolygon(shape: List<Location>): MapObjectKey {
        val poly = PolygonMapObject(
            generateMapObjectKey(),
            shape
        )

        items[poly.key] = poly
        mapsManagerHandle.get()?.add(poly)
        return poly.key
    }

    override fun addLine(shape: List<Location>): MapObjectKey {
        val line = LineMapObject(
            generateMapObjectKey(),
            shape
        )

        items[line.key] = line
        mapsManagerHandle.get()?.add(line)
        return line.key
    }

    override fun addMarker(position: Location): MapObjectKey {
        val marker = MarkerMapObject(
            generateMapObjectKey(),
            position
        )

        items[marker.key] = marker
        mapsManagerHandle.get()?.add(marker)
        return marker.key
    }

    override fun delete(key: MapObjectKey) {
        items.remove(key)
        mapsManagerHandle.get()?.delete(key)
    }

}

interface MapsManagerHandle {

    fun add(obj: MapObject)
    fun delete(key: MapObjectKey)
    fun show(pos: Location)
    fun show(tl: Location, br: Location)

}

expect class MapsManager: MapsManagerHandle