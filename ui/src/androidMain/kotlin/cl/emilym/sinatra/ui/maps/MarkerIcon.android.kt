package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import cl.emilym.sinatra.ui.R
import cl.emilym.sinatra.ui.minimumTouchTarget
import cl.emilym.sinatra.ui.widgets.toIntPx
import com.google.android.gms.maps.model.BitmapDescriptor

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
actual fun circularIcon(color: Color, borderColor: Color, size: Dp): MarkerIcon {
    val markerCirclePadding = (minimumTouchTarget - size) / 2
    val totalMarkerCircleSize = minimumTouchTarget

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
                    borderColor,
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
actual fun spotMarkerIcon(
    tint: Color,
    size: Dp
): MarkerIcon {
    val sizePx = size.toIntPx()
    val drawable = DrawableCompat.wrap(
        ContextCompat.getDrawable(LocalContext.current, R.drawable.spot_marker)!!
    ).apply {
        setTint(tint.toArgb())
    }

    return MarkerIconBuilder(
        factory = {
            bitmapDescriptorBuilder(sizePx, sizePx) {
                drawable(drawable, 0, 0, sizePx, sizePx)
            }
        },
        anchor = MarkerIconOffset(0.5f, 1f)
    )
}