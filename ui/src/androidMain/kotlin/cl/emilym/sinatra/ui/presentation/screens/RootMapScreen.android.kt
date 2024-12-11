package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.canberraZoom
import cl.emilym.sinatra.ui.maps.CameraState
import cl.emilym.sinatra.ui.maps.LineItem
import cl.emilym.sinatra.ui.maps.LocalCameraState
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.currentLocationIcon
import cl.emilym.sinatra.ui.maps.defaultMarkerOffset
import cl.emilym.sinatra.ui.maps.toNative
import cl.emilym.sinatra.ui.navigation.AndroidMapControl
import cl.emilym.sinatra.ui.navigation.MapControl
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.navigation.currentMapItems
import cl.emilym.sinatra.ui.presentation.theme.defaultLineColor
import cl.emilym.sinatra.ui.toNative
import cl.emilym.sinatra.ui.toShared
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.viewportSize
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Composable
actual fun Map(content: @Composable MapControl.(@Composable () -> Unit) -> Unit) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(canberra.toNative(), canberraZoom)
    }
    val windowPadding = ScaffoldDefaults.contentWindowInsets.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current
    val currentLocation = currentLocation()
    val currentLocationIcon = currentLocationIcon()

    val scope = AndroidMapControl(
        cameraPositionState,
        rememberCoroutineScope(),
        viewportSize(),
        bottomSheetHalfHeight()
    )

    scope.content {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            googleMapOptionsFactory = {
                GoogleMapOptions()
            },
            uiSettings = MapUiSettings(
                compassEnabled = false,
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            ),
            contentPadding = PaddingValues(
                windowPadding.calculateStartPadding(layoutDirection),
                windowPadding.calculateTopPadding(),
                windowPadding.calculateEndPadding(layoutDirection),
                windowPadding.calculateBottomPadding() + 56.dp
            ),
            mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM
        ) {
            CompositionLocalProvider(LocalCameraState provides CameraState(
                cameraPositionState.position.target.toShared(),
                cameraPositionState.position.zoom
            )) {
                val items = currentMapItems()
                
                currentLocation?.let { MarkerItem(it, currentLocationIcon) }

                for (item in items) {
                    when (item) {
                        is MarkerItem -> DrawMarker(item)
                        is LineItem -> DrawLine(item)
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
@GoogleMapComposable
fun DrawMarker(item: MarkerItem) {
    Marker(
        rememberMarkerState(
            item.id,
            item.location.toNative()
        ),
        icon = item.icon?.bitmapDescriptor,
        anchor = (item.icon?.anchor ?: defaultMarkerOffset).toNative(),
        onClick = {
            item.onClick?.let { it() }
            false
        }
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

