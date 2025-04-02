package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.ui.maps.AppleMapControl
import cl.emilym.sinatra.ui.maps.MapControl
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.SafeMapControl
import cl.emilym.sinatra.ui.maps.calculateVisibleMapSize
import cl.emilym.sinatra.ui.maps.currentLocationIcon
import cl.emilym.sinatra.ui.maps.iosCurrentMapItems
import cl.emilym.sinatra.ui.maps.precompute
import cl.emilym.sinatra.ui.maps.precomputeDp
import cl.emilym.sinatra.ui.maps.rememberMapKitState
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.plus
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.viewportSize
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.launch
import platform.MapKit.MKMapView
import platform.MapKit.MKPointOfInterestCategoryPublicTransport
import platform.MapKit.MKPointOfInterestFilter

val globalPointOfInterestFilter = MKPointOfInterestFilter(excludingCategories = listOf(
    MKPointOfInterestCategoryPublicTransport
))

@OptIn(ExperimentalComposeUiApi::class, ExperimentalForeignApi::class)
@Composable
actual fun Map(
    mapControl: MapControl,
    modifier: Modifier
) {

    val state = rememberMapKitState {}

    val insets = when {
        FeatureFlags.IOS_APPLE_MAP_LOGO_FOLLOW_BOTTOM_SHEET -> mapInsets + PaddingValues(bottom = bottomSheetContentPadding)
        else -> mapInsets
    }
    val bottomSheetContentPadding = when {
        FeatureFlags.IOS_APPLE_MAP_LOGO_FOLLOW_BOTTOM_SHEET -> PaddingValues(bottom = bottomSheetContentPadding)
        else -> PaddingValues(0.dp)
    }.precomputeDp()
    val viewportSize = viewportSize()
    val paddingValues = insets.precompute()
    val bottomSheetHalfHeight = bottomSheetHalfHeight()
    val density = LocalDensity.current

    val control = remember(state, viewportSize, paddingValues, bottomSheetHalfHeight, density) {
        AppleMapControl(
            state,
            viewportSize,
            paddingValues,
            bottomSheetHalfHeight,
            density
        )
    }

    LaunchedEffect(control) {
        (mapControl as? SafeMapControl)?.wrapped = control
    }

    LaunchedEffect(viewportSize, bottomSheetHalfHeight, paddingValues) {
        state.contentViewportSize = viewportSize.dp(density.density)
        state.visibleMapSize = calculateVisibleMapSize(
            bottomSheetHalfHeight, viewportSize, paddingValues
        ).dp(density.density)
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

    LaunchedEffect(bottomSheetContentPadding) {
        state.contentPadding = bottomSheetContentPadding
    }

    val coroutineScope = rememberCoroutineScope()

    Box(Modifier.then(modifier)) {
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
}