package cl.emilym.sinatra.ui.maps

import cl.emilym.sinatra.data.models.Zoom
import com.google.maps.android.compose.CameraPositionState

actual class NativeMapScope(
    val cameraPositionState: CameraPositionState,
    private val androidMapControl: AndroidMapControl,
) {

    // Even though we don't use cameraPositionState.position, we use it to ensure that the zoom property
    // is kept up to date!
    val zoom: Zoom get() = cameraPositionState.position.run {
        androidMapControl.zoom
    }

}