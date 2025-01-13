package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import cl.emilym.sinatra.ui.maps.AppleMapControl
import cl.emilym.sinatra.ui.maps.MapControl
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.SafeMapControl
import cl.emilym.sinatra.ui.maps.currentLocationIcon
import cl.emilym.sinatra.ui.maps.iosCurrentMapItems
import cl.emilym.sinatra.ui.maps.precompute
import cl.emilym.sinatra.ui.maps.rememberMapKitState
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.viewportSize
import kotlinx.coroutines.launch
import platform.MapKit.MKMapView
import platform.MapKit.MKPointOfInterestCategoryPublicTransport
import platform.MapKit.MKPointOfInterestFilter

val globalPointOfInterestFilter = MKPointOfInterestFilter(excludingCategories = listOf(
    MKPointOfInterestCategoryPublicTransport
))

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun Map(mapControl: MapControl) {

    val state = rememberMapKitState {}

    val insets = WindowInsets.systemBars.only(WindowInsetsSides.Top)
    val viewportSize = viewportSize(insets)
    val paddingValues = insets.asPaddingValues().precompute()
    val bottomSheetHalfHeight = bottomSheetHalfHeight()

    val control = remember(state, viewportSize, paddingValues, bottomSheetHalfHeight) {
        AppleMapControl(
            state,
            viewportSize,
            paddingValues,
            bottomSheetHalfHeight
        )
    }

    LaunchedEffect(control) {
        (mapControl as? SafeMapControl)?.wrapped = control
    }

    LaunchedEffect(viewportSize) {
        state.contentViewportSize = viewportSize
    }

    val currentLocation = currentLocation()
    val currentLocationIcon = currentLocationIcon()

    val items = iosCurrentMapItems() + listOfNotNull(
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

    UIKitView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            MKMapView().apply {
                setZoomEnabled(true)
                setScrollEnabled(true)
                setRotateEnabled(false)
                setPointOfInterestFilter(globalPointOfInterestFilter)
            }.also {
                coroutineScope.launch { state.setMap(it) }
            }
        },
        onRelease = {
            coroutineScope.launch { state.setMap(null) }
        },
        properties = UIKitInteropProperties(
            interactionMode = UIKitInteropInteractionMode.NonCooperative
        )
    )
}