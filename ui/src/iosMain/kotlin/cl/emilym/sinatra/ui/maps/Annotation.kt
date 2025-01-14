package cl.emilym.sinatra.ui.maps

import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Zoom
import cl.emilym.sinatra.lib.FloatRange
import cl.emilym.sinatra.ui.toNative
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2D
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKMapRect
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKPolyline
import platform.UIKit.UIColor
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class LineAnnotation(
    val delegate: MKPolyline,
    val color: UIColor
): NSObject(), MKAnnotationProtocol, MKOverlayProtocol {
    override fun coordinate(): CValue<CLLocationCoordinate2D> {
        return delegate.coordinate()
    }

    override fun boundingMapRect(): CValue<MKMapRect> {
        return delegate.boundingMapRect()
    }
}

class MarkerAnnotation(
    id: String,
    location: MapLocation,
    icon: MarkerIcon?,
    visibleZoomRange: FloatRange?,
    visible: Boolean,
    contentDescription: String?
): NSObject(), MKAnnotationProtocol {

    var id: String = id
        private set
    var location: MapLocation = location
        private set
    var icon: MarkerIcon? = icon
        private set
    var visibleZoomRange: FloatRange? = visibleZoomRange
        private set
    var visible: Boolean = visible
        private set
    var contentDescription: String? = contentDescription
        private set

    @OptIn(ExperimentalForeignApi::class)
    private val position = location.toNative()

    @ExperimentalForeignApi
    override fun coordinate(): CValue<CLLocationCoordinate2D> {
        return position
    }

    fun update(item: MarkerItem) {
        id = item.id
        location = item.location
        icon = item.icon
        visibleZoomRange = item.visibleZoomRange
        visible = item.visible
        contentDescription = item.contentDescription
    }

    fun isHidden(zoom: Zoom?): Boolean {
        return !visible || visibleZoomRange?.let { visibleZoomRange ->
            zoom?.let { zoom -> zoom !in visibleZoomRange }
        } ?: false
    }

    companion object {
        fun fromItem(item: MarkerItem): MarkerAnnotation {
            return MarkerAnnotation(
                id = item.id,
                location = item.location,
                icon = item.icon,
                visibleZoomRange = item.visibleZoomRange,
                visible = item.visible,
                contentDescription = item.contentDescription
            )
        }
    }

}