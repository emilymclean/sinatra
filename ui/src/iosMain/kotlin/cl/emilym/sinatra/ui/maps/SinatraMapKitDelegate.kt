package cl.emilym.sinatra.ui.maps

import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.pointed
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKOverlayRenderer
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.UIKit.UIColor
import platform.darwin.NSObject

class SinatraMapKitDelegate(
    val callback: (MKAnnotationProtocol) -> Unit
): NSObject(), MKMapViewDelegateProtocol {

    @OptIn(ExperimentalForeignApi::class)
    override fun mapView(
        mapView: MKMapView,
        rendererForOverlay: MKOverlayProtocol
    ): MKOverlayRenderer {
        return when (rendererForOverlay) {
            is LineAnnotation -> {
                MKPolylineRenderer(rendererForOverlay.delegate).also {
                    it.strokeColor = rendererForOverlay.color
                }
            }
            is MKPolyline -> {
                MKPolylineRenderer(rendererForOverlay)
            }
            else -> MKOverlayRenderer()
        }
    }

    override fun mapView(
        mapView: MKMapView,
        viewForAnnotation: MKAnnotationProtocol
    ): MKAnnotationView? {
        return when (viewForAnnotation) {
            is MarkerAnnotation -> {
                val icon = viewForAnnotation.icon ?: return MKAnnotationView()
                mapView.dequeueReusableAnnotationViewWithIdentifier(icon.reuseIdentifier)
                    ?: icon.annotationView(viewForAnnotation)
            }
            else -> MKAnnotationView()
        }
    }

    override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
        callback(didSelectAnnotationView.annotation ?: return)
    }
}