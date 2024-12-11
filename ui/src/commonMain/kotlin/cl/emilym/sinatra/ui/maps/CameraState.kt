package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.compositionLocalOf
import cl.emilym.sinatra.data.models.MapLocation

data class CameraState(
    val position: MapLocation,
    val zoom: Float
)

val LocalCameraState = compositionLocalOf<CameraState> { error("No camera state available") }