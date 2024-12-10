package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import cl.emilym.sinatra.data.models.Route

actual interface MarkerIcon {
    actual val anchor: MarkerIconOffset
}

private val default = object : MarkerIcon {
    override val anchor: MarkerIconOffset
        get() = MarkerIconOffset(0.5f, 0.5f)
}

@Composable
actual fun stopMarkerIcon(color: Color, size: Dp): MarkerIcon {
    return default
}

@Composable
actual fun routeStopMarkerIcon(route: Route): MarkerIcon {
    return default
}