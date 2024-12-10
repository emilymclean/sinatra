package cl.emilym.sinatra.ui.maps

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.ui.minimumTouchTarget

val markerCircleSize = 8.dp
val markerCirclePadding = (minimumTouchTarget - markerCircleSize) / 2
val totalMarkerCircleSize = minimumTouchTarget

data class MarkerIconOffset(
    val x: Float,
    val y: Float
)

expect interface MarkerIcon {
    val anchor: MarkerIconOffset
}

@Composable
expect fun stopMarkerIcon(color: Color = MaterialTheme.colorScheme.primary): MarkerIcon

@Composable
expect fun routeStopMarkerIcon(route: Route): MarkerIcon