package cl.emilym.sinatra.ui.maps

import cl.emilym.kmp.serializable.Serializable
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.lib.NativeWeakReference
import cl.emilym.sinatra.ui.presentation.screens.MapStackKey

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

internal class DefaultMapsHandle(
    private val mapsManagerHandle: NativeWeakReference<MapsManagerHandle>,
    private val stackKey: MapStackKey
): MapsHandle {

    override fun showPoint(position: Location) {
        mapsManagerHandle.get()?.show(position, stackKey)
    }

    override fun showArea(topLeft: Location, bottomRight: Location) {
        mapsManagerHandle.get()?.show(topLeft, bottomRight, stackKey)
    }

    override fun addPolygon(shape: List<Location>): MapObjectKey {
        val poly = PolygonMapObject(
            generateMapObjectKey(),
            shape
        )
        mapsManagerHandle.get()?.add(poly, stackKey)
        return poly.key
    }

    override fun addLine(shape: List<Location>): MapObjectKey {
        val line = LineMapObject(
            generateMapObjectKey(),
            shape
        )
        mapsManagerHandle.get()?.add(line, stackKey)
        return line.key
    }

    override fun addMarker(position: Location): MapObjectKey {
        val marker = MarkerMapObject(
            generateMapObjectKey(),
            position
        )
        mapsManagerHandle.get()?.add(marker, stackKey)
        return marker.key
    }

    override fun delete(key: MapObjectKey) {
        mapsManagerHandle.get()?.delete(key, stackKey)
    }

}