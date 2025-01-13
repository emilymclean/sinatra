package cl.emilym.sinatra.ui.maps

import cl.emilym.sinatra.data.models.ScreenRegionSizeDp
import cl.emilym.sinatra.ui.toShared
import cl.emilym.sinatra.ui.toSize
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKOverlayRenderer
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.darwin.NSObject

class SinatraMapKitDelegate(
    val clickCallback: (MKAnnotationProtocol) -> Unit,
    val mapUpdateCallback: () -> Unit,
): NSObject(), MKMapViewDelegateProtocol {

    var visibleMapSize: ScreenRegionSizeDp? = null

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

    @OptIn(ExperimentalForeignApi::class)
    override fun mapView(
        mapView: MKMapView,
        viewForAnnotation: MKAnnotationProtocol
    ): MKAnnotationView? {
        val visibleRegion = mapView.visibleMapRect.useContents { toShared() }
        val zoom = visibleMapSize?.let { visibleRegion.toZoom(it) + 1 } ?:
            visibleRegion.toZoom(mapView.bounds.toSize().width, mapView.bounds.toSize().height)
        return when (viewForAnnotation) {
            is MarkerAnnotation -> {
                val icon = viewForAnnotation.icon ?: return MKAnnotationView()
                (mapView.dequeueReusableAnnotationViewWithIdentifier(icon.reuseIdentifier)
                    ?: icon.annotationView(viewForAnnotation, viewForAnnotation.contentDescription)
                ).apply {
                    viewForAnnotation.visibleZoomRange?.let {
                        hidden = zoom !in it
                    }
                }
            }
            else -> MKAnnotationView()
        }
    }

    override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
        clickCallback(didSelectAnnotationView.annotation ?: return)
    }

    override fun mapViewDidChangeVisibleRegion(mapView: MKMapView) {
        mapUpdateCallback()
    }
}