package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.ui.R
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.canberraZoom
import cl.emilym.sinatra.ui.maps.LineItem
import cl.emilym.sinatra.ui.maps.MapControl
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.NativeMapScope
import cl.emilym.sinatra.ui.maps.currentLocationIcon
import cl.emilym.sinatra.ui.maps.defaultMarkerOffset
import cl.emilym.sinatra.ui.maps.precompute
import cl.emilym.sinatra.ui.maps.toNative
import cl.emilym.sinatra.ui.navigation.AndroidMapControl
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.navigation.currentDrawNativeMap
import cl.emilym.sinatra.ui.navigation.currentMapItems
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
actual fun Map(content: @Composable MapControl.(@Composable () -> Unit) -> Unit) {
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(canberra.toNative(), canberraZoom)
    }
    val windowPadding = ScaffoldDefaults.contentWindowInsets.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current
    val currentLocation = currentLocation()
    val currentLocationIcon = currentLocationIcon()

    val insets = WindowInsets.systemBars.only(WindowInsetsSides.Top)
    val coroutineScope = rememberCoroutineScope()
    val viewportSize = viewportSize(insets)
    val paddingValues = insets.asPaddingValues().precompute()
    val bottomSheetHalfHeight = bottomSheetHalfHeight()


    val scope = remember(cameraPositionState, coroutineScope, viewportSize, paddingValues, bottomSheetHalfHeight) {
        AndroidMapControl(
            cameraPositionState,
            coroutineScope,
            viewportSize,
            paddingValues,
            bottomSheetHalfHeight
        )
    }
    val nativeMapScope = NativeMapScope(cameraPositionState)

    scope.content {
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
            contentPadding = PaddingValues(
                windowPadding.calculateStartPadding(layoutDirection),
                windowPadding.calculateTopPadding(),
                windowPadding.calculateEndPadding(layoutDirection),
                windowPadding.calculateBottomPadding()
            ),
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

