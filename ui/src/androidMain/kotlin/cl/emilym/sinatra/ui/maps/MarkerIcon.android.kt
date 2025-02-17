package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    val anchor: MarkerIconOffset
}

fun MarkerIconOffset.toNative(): Offset {
    return Offset(x, y)
}

class MarkerIconBuilder(
    val factory: () -> BitmapDescriptor,
    override val anchor: MarkerIconOffset = MarkerIconOffset(0.5f, 0.5f)
): MarkerIcon {
    override val bitmapDescriptor by lazy { factory() }
}

val defaultMarkerOffset = MarkerIconOffset(0.5f, 1f)

@Composable
actual fun circularIcon(color: Color, borderColor: Color, size: Dp, borderWidth: Dp): MarkerIcon {
    val markerCirclePadding = (minimumTouchTarget - size) / 2
    val totalMarkerCircleSize = minimumTouchTarget

    val halfBorderSize = (borderWidth * platformSizeAdjustment()).toIntPx() / 2
    val canvasSize = (totalMarkerCircleSize * platformSizeAdjustment()).toIntPx()
    val markerPadding = (markerCirclePadding * platformSizeAdjustment()).toIntPx()
    val markerRadius = ((size * platformSizeAdjustment()) / 2).toIntPx()
    return remember(color, borderColor, size, borderWidth) {
        MarkerIconBuilder(
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
}

@Composable
actual fun spotMarkerIcon(
    tint: Color,
    borderColor: Color,
    size: Dp
): MarkerIcon? {
    val sizePx = (size * platformSizeAdjustment()).toIntPx()
    val borderSize = (4.dp * platformSizeAdjustment()).toIntPx()
    val overDrawable = DrawableCompat.wrap(
        ContextCompat.getDrawable(LocalContext.current, R.drawable.spot_marker)!!
    ).apply {
        setTint(tint.toArgb())
    }
    val borderDrawable = DrawableCompat.wrap(
        ContextCompat.getDrawable(LocalContext.current, R.drawable.spot_marker)!!
    ).apply {
        setTint(borderColor.toArgb())
    }

    return remember(tint, borderColor, size) {
        MarkerIconBuilder(
            factory = {
                bitmapDescriptorBuilder(sizePx, sizePx) {
                    drawable(borderDrawable, 0, 0, sizePx, sizePx)
                    drawable(
                        overDrawable,
                        borderSize, borderSize,
                        sizePx - (borderSize * 2),
                        sizePx - (borderSize * 2),
                    )
                }
            },
            anchor = MarkerIconOffset(0.5f, 1f)
        )
    }
}

actual fun platformSizeAdjustment(): Float = 1f