package cl.emilym.sinatra.ui.maps

import cl.emilym.sinatra.data.models.MapLocation

data class CameraState(
    val position: MapLocation,
    val zoom: Float
)