package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.ui.maps.MapControl
import cl.emilym.sinatra.ui.maps.createRegion
import cl.emilym.sinatra.ui.maps.rememberMapKitState
import cl.emilym.sinatra.ui.navigation.MapScope
import cl.emilym.sinatra.ui.widgets.currentLocation
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import platform.MapKit.MKMapView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun Map(content: @Composable MapControl.(@Composable () -> Unit) -> Unit) {

    val state = rememberMapKitState {}

    val control = object : MapControl {
        override fun zoomToArea(bounds: MapRegion, padding: Int) {}

        override fun zoomToArea(topLeft: MapLocation, bottomRight: MapLocation, padding: Int) {}

        override fun zoomToPoint(location: MapLocation, zoom: Float) {
            state.coordinate.value = location
            state.zoom.value = zoom
        }
    }

    control.content {
        val coordinate by state.coordinate
        val zoom by state.zoom

        LaunchedEffect(coordinate) {
            Napier.d("Map coordinate = $coordinate")
        }

        val scope = MapScope(state, control)
        val currentLocation = currentLocation()

        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MKMapView().apply {
                    setZoomEnabled(true)
                    setScrollEnabled(true)
                }
            },
            update = { mapView ->
                mapView.setRegion(createRegion(coordinate, zoom), animated = true)
            }
        )
    }
}