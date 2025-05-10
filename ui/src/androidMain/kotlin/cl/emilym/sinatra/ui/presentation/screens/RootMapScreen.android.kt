package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Zoom
import cl.emilym.sinatra.ui.R
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.canberraZoom
import cl.emilym.sinatra.ui.maps.AndroidMapControl
import cl.emilym.sinatra.ui.maps.LineItem
import cl.emilym.sinatra.ui.maps.MapCallbackItem
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
import cl.emilym.sinatra.ui.toShared
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
actual fun Map(
    mapControl: MapControl,
    modifier: Modifier
) {
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

    var clickCallback by remember { mutableStateOf<((MapLocation, Zoom) -> Unit)?>(null) }
    var longClickCallback by remember { mutableStateOf<((MapLocation, Zoom) -> Unit)?>(null) }

    Box(Modifier.then(modifier)) {
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
                tiltGesturesEnabled = false,
                indoorLevelPickerEnabled = false,
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                mapToolbarEnabled = false
            ),
            onMapClick = { clickCallback?.invoke(it.toShared(), cameraPositionState.position.zoom) },
            onMapLongClick = { longClickCallback?.invoke(it.toShared(), cameraPositionState.position.zoom) },
            contentPadding = insets,
            mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM
        ) {
            currentLocation?.let { DrawMarker(MarkerItem(it, currentLocationIcon)) }

            currentMapItems { items ->
                clickCallback = null
                longClickCallback = null
                for (item in items) {
                    when (item) {
                        is MarkerItem -> DrawMarker(item)
                        is LineItem -> DrawLine(item)
                        is MapCallbackItem -> {
                            clickCallback = item.onClick
                            longClickCallback = item.onLongClick
                        }
                        else -> {}
                    }
                }
            }

            nativeMapScope.currentDrawNativeMap()
        }
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

