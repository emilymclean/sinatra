package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import cafe.adriel.voyager.core.screen.Screen
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.canberraZoom
import cl.emilym.sinatra.ui.maps.createRegion
import cl.emilym.sinatra.ui.maps.toMaps
import cl.emilym.sinatra.ui.navigation.MapControl
import cl.emilym.sinatra.ui.navigation.MapScope
import kotlinx.cinterop.ExperimentalForeignApi
import platform.MapKit.MKMapView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun Map(content: @Composable MapControl.(@Composable () -> Unit) -> Unit) {

    val control = object : MapControl {
        override fun zoomToArea(bounds: Bounds, padding: Int) {}

        override fun zoomToArea(topLeft: Location, bottomRight: Location, padding: Int) {}

        override fun zoomToPoint(location: Location, zoom: Float) {}
    }
    control.content {
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MKMapView().apply {
                    setZoomEnabled(true)
                    setScrollEnabled(true)
                }
            },
            update = { mapView ->
                mapView.setRegion(createRegion(canberra, canberraZoom), animated = true)
            }
        )
    }
}