package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.navigation.CurrentMapContent
import cl.emilym.sinatra.ui.navigation.MapScope
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.toMaps
import cl.emilym.sinatra.ui.widgets.screenSize
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
actual fun Map() {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(canberra.toMaps(), 10f)
    }
    val windowPadding = ScaffoldDefaults.contentWindowInsets.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current

    Box {
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
            MapScope(
                cameraPositionState,
                screenSize(),
                bottomSheetHalfHeight()
            ).CurrentMapContent()
        }
    }
}