package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import cl.emilym.sinatra.ui.maps.AppleMapControl
import cl.emilym.sinatra.ui.maps.CameraState
import cl.emilym.sinatra.ui.maps.MapControl
import cl.emilym.sinatra.ui.maps.MapScope
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.currentLocationIcon
import cl.emilym.sinatra.ui.maps.rememberMapKitState
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.navigation.currentMapItems
import cl.emilym.sinatra.ui.toShared
import cl.emilym.sinatra.ui.toZoom
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.viewportSize
import kotlinx.coroutines.launch
import platform.MapKit.MKMapView
import platform.MapKit.MKPointOfInterestCategoryPublicTransport
import platform.MapKit.MKPointOfInterestFilter

val globalPointOfInterestFilter = MKPointOfInterestFilter(excludingCategories = listOf(
    MKPointOfInterestCategoryPublicTransport
))

@Composable
actual fun Map(content: @Composable MapControl.(@Composable () -> Unit) -> Unit) {

    val state = rememberMapKitState {}

    val control = AppleMapControl(
        state,
        viewportSize(),
        bottomSheetHalfHeight()
    )

    val scope = remember(state.cameraDescription) {
        MapScope(
            CameraState(
                state.cameraDescription.center,
                state.cameraDescription.coordinateSpan.toZoom()
            )
        )
    }

    val currentLocation = currentLocation()
    val currentLocationIcon = currentLocationIcon()

    val items = scope.currentMapItems() + listOfNotNull(
        currentLocation?.let {
            MarkerItem(
                it,
                currentLocationIcon,
                id = "currentLocation"
            )
        }
    )
    LaunchedEffect(items) {
        state.updateItems(items)
    }

    val coroutineScope = rememberCoroutineScope()

    control.content {
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MKMapView().apply {
                    setZoomEnabled(true)
                    setScrollEnabled(true)
                    setPointOfInterestFilter(globalPointOfInterestFilter)
                }.also {
                    coroutineScope.launch { state.setMap(it) }
                }
            },
            onRelease = {
                coroutineScope.launch { state.setMap(null) }
            }
        )
    }
}