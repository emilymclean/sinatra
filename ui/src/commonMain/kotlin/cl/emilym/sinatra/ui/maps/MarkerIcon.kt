package cl.emilym.sinatra.ui.maps

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.ui.minimumTouchTarget


data class MarkerIconOffset(
    val x: Float,
    val y: Float
)

expect interface MarkerIcon {
    val anchor: MarkerIconOffset
}

@Composable
expect fun stopMarkerIcon(
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 20.dp
): MarkerIcon

@Composable
expect fun routeStopMarkerIcon(route: Route): MarkerIcon