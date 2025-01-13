package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import cl.emilym.sinatra.ui.R
import cl.emilym.sinatra.ui.asPaddingValues
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.canberraZoom
import cl.emilym.sinatra.ui.maps.AndroidMapControl
import cl.emilym.sinatra.ui.maps.LineItem
import cl.emilym.sinatra.ui.maps.MapControl
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.NativeMapScope
import cl.emilym.sinatra.ui.maps.SafeMapControl
import cl.emilym.sinatra.ui.maps.currentLocationIcon
import cl.emilym.sinatra.ui.maps.defaultMarkerOffset
import cl.emilym.sinatra.ui.maps.precompute
import cl.emilym.sinatra.ui.maps.toNative
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.navigation.currentDrawNativeMap
import cl.emilym.sinatra.ui.navigation.currentMapItems
import cl.emilym.sinatra.ui.plus
import cl.emilym.sinatra.ui.presentation.theme.defaultLineColor
import cl.emilym.sinatra.ui.toNative
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.viewportSize
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
actual fun Map(mapControl: MapControl) {
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(canberra.toNative(), canberraZoom)
    }
    val currentLocation = currentLocation()
    val currentLocationIcon = currentLocationIcon()

    val insets = mapInsets + PaddingValues(bottom = bottomSheetContentPadding)
    val coroutineScope = rememberCoroutineScope()
    val viewportSize = viewportSize()
    val paddingValues = insets.precompute()
    val bottomSheetHalfHeight = bottomSheetHalfHeight()
    val density = LocalDensity.current

    val realMapControl = remember(cameraPositionState, coroutineScope, viewportSize, paddingValues, bottomSheetHalfHeight, density) {
        AndroidMapControl(
            cameraPositionState,
            coroutineScope,
            viewportSize,
            density,
            paddingValues,
            bottomSheetHalfHeight
        )
    }
    val nativeMapScope = remember(cameraPositionState, realMapControl) { NativeMapScope(cameraPositionState, realMapControl) }

    LaunchedEffect(realMapControl) {
        (mapControl as? SafeMapControl)?.wrapped = realMapControl
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        googleMapOptionsFactory = {
            GoogleMapOptions()
        },
        properties = MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.maps_style)
        ),
        uiSettings = MapUiSettings(
            compassEnabled = false,
            rotationGesturesEnabled = false,
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false
        ),
        contentPadding = insets,
        mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM
    ) {
        currentLocation?.let { DrawMarker(MarkerItem(it, currentLocationIcon)) }

        currentMapItems { items ->
            for (item in items) {
                when (item) {
                    is MarkerItem -> DrawMarker(item)
                    is LineItem -> DrawLine(item)
                    else -> {}
                }
            }
        }

        nativeMapScope.currentDrawNativeMap()
    }
}

@Composable
@GoogleMapComposable
fun DrawMarker(item: MarkerItem) {
    val state = rememberMarkerState(
        item.id,
        item.location.toNative()
    )

    LaunchedEffect(item) {
        state.position = item.location.toNative()
    }

    Marker(
        state,
        icon = item.icon?.bitmapDescriptor,
        anchor = (item.icon?.anchor ?: defaultMarkerOffset).toNative(),
        onClick = {
            item.onClick?.let { it() }
            false
        },
        title = item.contentDescription
    )
}

@Composable
@GoogleMapComposable
fun DrawLine(item: LineItem) {
    Polyline(
        item.points.map { it.toNative() },
        color = item.color ?: defaultLineColor()
    )
}

