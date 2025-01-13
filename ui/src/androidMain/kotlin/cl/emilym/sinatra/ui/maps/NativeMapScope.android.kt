package cl.emilym.sinatra.ui.maps

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import cl.emilym.sinatra.data.models.Zoom
import cl.emilym.sinatra.ui.calculateZoom
import cl.emilym.sinatra.ui.navigation.AndroidMapControl
import com.google.maps.android.compose.CameraPositionState

actual class NativeMapScope(
    val cameraPositionState: CameraPositionState,
    private val androidMapControl: AndroidMapControl,
) {

    val zoom: Zoom get() = cameraPositionState.position.run {
        androidMapControl.zoom
    }

}