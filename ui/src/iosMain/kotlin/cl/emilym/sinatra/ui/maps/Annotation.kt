package cl.emilym.sinatra.ui.maps

import cl.emilym.gtfs.Location
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.lib.FloatRange
import cl.emilym.sinatra.ui.toNative
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
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
    val id: String,
    val location: MapLocation,
    val icon: MarkerIcon?,
    val visibleZoomRange: FloatRange?,
): NSObject(), MKAnnotationProtocol {

    @OptIn(ExperimentalForeignApi::class)
    private val position = location.toNative()

    @ExperimentalForeignApi
    override fun coordinate(): CValue<CLLocationCoordinate2D> {
        return position
    }

}