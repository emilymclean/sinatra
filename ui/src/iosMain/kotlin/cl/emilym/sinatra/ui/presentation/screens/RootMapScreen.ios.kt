package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import cl.emilym.sinatra.ui.maps.AppleMapControl
import cl.emilym.sinatra.ui.maps.MapControl
import cl.emilym.sinatra.ui.maps.rememberMapKitState
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.viewportSize
import kotlinx.cinterop.ExperimentalForeignApi
import platform.MapKit.MKMapView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun Map(content: @Composable MapControl.(@Composable () -> Unit) -> Unit) {

    val state = rememberMapKitState {}

    val control = AppleMapControl(
        state,
        viewportSize(),
        bottomSheetHalfHeight()
    )


    control.content {
        val currentLocation = currentLocation()

        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MKMapView().apply {
                    setZoomEnabled(true)
                    setScrollEnabled(true)
                }.also {
                    state.setMap(it)
                }
            },
            onRelease = {
                state.setMap(null)
            }
        )
    }
}