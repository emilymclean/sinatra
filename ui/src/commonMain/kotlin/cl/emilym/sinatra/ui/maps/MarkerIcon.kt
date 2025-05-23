package cl.emilym.sinatra.ui.maps

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.isIos
import cl.emilym.sinatra.ui.color
import cl.emilym.sinatra.ui.presentation.theme.walkingColor

expect fun platformSizeAdjustment(): Float

data class MarkerIconOffset(
    val x: Float,
    val y: Float
)

expect interface MarkerIcon

@Composable
expect fun spotMarkerIcon(
    tint: Color,
    borderColor: Color = Color.White,
    size: Dp = 30.dp
): MarkerIcon?

@Composable
expect fun circularIcon(
    color: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = Color.White,
    size: Dp = 20.dp,
    borderWidth: Dp = 4.dp
): MarkerIcon

@Composable
fun stopMarkerIcon(stop: Stop? = null): MarkerIcon? {
    return spotMarkerIcon(MaterialTheme.colorScheme.primary)
}

@Composable
fun placeMarkerIcon(place: Place? = null): MarkerIcon? {
    return spotMarkerIcon(MaterialTheme.colorScheme.primary)
}

@Composable
fun highlightedRouteStopMarkerIcon(route: Route, stop: Stop? = null): MarkerIcon? {
    return spotMarkerIcon(route.color())
}

@Composable
fun routeStopMarkerIcon(route: Route): MarkerIcon {
    return circularIcon(route.color(), size = 8.dp * if(isIos) 1.5f else 1f, borderWidth = 2.dp * if(isIos) 1.5f else 1f)
}

@Composable
fun walkingMarkerIcon(): MarkerIcon {
    return circularIcon(walkingColor, size = 8.dp * if(isIos) 1.5f else 1f, borderWidth = 2.dp * if(isIos) 1.5f else 1f)
}

@Composable
fun currentLocationIcon(): MarkerIcon {
    return circularIcon(
        MaterialTheme.colorScheme.secondary,
    )
}