package cl.emilym.sinatra.ui.maps

import android.graphics.Bitmap
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.ui.color
import cl.emilym.sinatra.ui.minimumTouchTarget
import cl.emilym.sinatra.ui.widgets.toFloatPx
import cl.emilym.sinatra.ui.widgets.toIntPx
import com.google.android.gms.maps.model.BitmapDescriptor
import io.github.aakira.napier.Napier

actual interface MarkerIcon {
    val bitmapDescriptor: BitmapDescriptor
    actual val anchor: MarkerIconOffset
}

fun MarkerIconOffset.toMaps(): Offset {
    return Offset(x, y)
}

class MarkerIconBuilder(
    val factory: () -> BitmapDescriptor,
    override val anchor: MarkerIconOffset = MarkerIconOffset(0.5f, 0.5f)
): MarkerIcon {
    override val bitmapDescriptor by lazy { factory() }
}

@Composable
actual fun stopMarkerIcon(color: Color, size: Dp): MarkerIcon {
    val markerCirclePadding = (minimumTouchTarget - size) / 2
    val totalMarkerCircleSize = minimumTouchTarget

    val surfaceColor = MaterialTheme.colorScheme.surface
    val halfBorderSize = 2.dp.toIntPx() / 2
    val canvasSize = totalMarkerCircleSize.toIntPx()
    val markerPadding = markerCirclePadding.toIntPx()
    val markerRadius = (size / 2).toIntPx()
    return MarkerIconBuilder(
        factory = {
            bitmapDescriptorBuilder(
                canvasSize, canvasSize
            ) {
                circle(
                    surfaceColor,
                    markerPadding - halfBorderSize,
                    markerPadding - halfBorderSize,
                    markerRadius + halfBorderSize
                )
                circle(color, markerPadding, markerPadding, markerRadius)
            }
        }
    )
}

@Composable
actual fun routeStopMarkerIcon(route: Route): MarkerIcon {
    return stopMarkerIcon(route.color(), 8.dp)
}