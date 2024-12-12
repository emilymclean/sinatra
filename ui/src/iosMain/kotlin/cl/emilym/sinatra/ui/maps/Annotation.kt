package cl.emilym.sinatra.ui.maps

import cl.emilym.sinatra.ui.toNative
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2D
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKMapRect
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.UIKit.UIColor
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
fun updatePointAnnotation(
    annotation: MKPointAnnotation,
    item: MarkerItem
) {
    annotation.setCoordinate(item.location.toNative())
}

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