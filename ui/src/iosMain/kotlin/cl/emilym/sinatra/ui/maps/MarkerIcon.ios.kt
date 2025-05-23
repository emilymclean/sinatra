package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.ui.minimumTouchTarget
import cl.emilym.sinatra.ui.widgets.toFloatPx
import cl.emilym.sinatra.ui.widgets.toIntPx
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import platform.CoreGraphics.CGPointMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.UIKit.UIImage
import platform.UIKit.accessibilityLabel
import sinatra.ui.generated.resources.Res

actual interface MarkerIcon {
    val reuseIdentifier: String
    val anchor: MarkerIconOffset

    fun annotationView(annotation: MKAnnotationProtocol, contentDescription: String? = null): MKAnnotationView
}

class UIImageMarkerIcon(
    override val reuseIdentifier: String,
    override val anchor: MarkerIconOffset,
    val image: UIImage?
): MarkerIcon {

    override fun annotationView(annotation: MKAnnotationProtocol, contentDescription: String?): MKAnnotationView {
        return UIImageAnnotationView(
            image, anchor, annotation, reuseIdentifier, contentDescription
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as UIImageMarkerIcon

        return reuseIdentifier == other.reuseIdentifier
    }

    override fun hashCode(): Int {
        return reuseIdentifier.hashCode()
    }


}

@OptIn(ExperimentalForeignApi::class)
class UIImageAnnotationView(
    image: UIImage?,
    anchor: MarkerIconOffset,
    annotation: MKAnnotationProtocol,
    reuseIdentifier: String,
    contentDescription: String?
): MKAnnotationView(annotation, reuseIdentifier) {
    init {
        image?.let {
            this.image = image
            this.centerOffset = image.size.useContents {
                CGPointMake(
                    -(anchor.x - 0.5f) * width,
                    -(anchor.y - 0.5f) * height,
                )
            }
        }
        accessibilityLabel = contentDescription
    }
}

@Composable
actual fun circularIcon(color: Color, borderColor: Color, size: Dp, borderWidth: Dp): MarkerIcon {
    val markerCirclePadding = (minimumTouchTarget - size) / 2
    val totalMarkerCircleSize = minimumTouchTarget

    val halfBorderSize = (borderWidth * platformSizeAdjustment()).toIntPx() / 2
    val canvasSize = (totalMarkerCircleSize * platformSizeAdjustment()).toIntPx()
    val markerPadding = (markerCirclePadding * platformSizeAdjustment()).toIntPx()
    val markerRadius = ((size * platformSizeAdjustment()) / 2).toIntPx()

    return remember(color, borderColor, size, borderWidth) {
        UIImageMarkerIcon(
            image = uiImageBuilder(canvasSize.toDouble(), canvasSize.toDouble()) {
                circle(
                    borderColor,
                    markerPadding - halfBorderSize,
                    markerPadding - halfBorderSize,
                    markerRadius + halfBorderSize
                )
                circle(color, markerPadding, markerPadding, markerRadius)
            },
            anchor = MarkerIconOffset(0.5f, 0.5f),
            reuseIdentifier = "circleIcon-${color.toArgb()}-${borderColor.toArgb()}-${size.value}-${borderWidth.value}"
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun spotMarkerIcon(
    tint: Color,
    borderColor: Color,
    size: Dp
): MarkerIcon? {
    val sizePx = (size * platformSizeAdjustment()).toFloatPx()
    val borderSize = (4.dp * platformSizeAdjustment()).toFloatPx()

    val locationPdf = remember { runBlocking { Res.readBytes("files/location.pdf") } }

    return remember(tint, borderColor, size, locationPdf) {
        UIImageMarkerIcon(
            image = uiImageBuilder(sizePx.toDouble(), sizePx.toDouble()) {
                pdf(locationPdf, borderColor, 0, 0, sizePx, sizePx)
                pdf(
                    locationPdf,
                    tint,
                    borderSize, borderSize,
                    sizePx - (borderSize * 2),
                    sizePx - (borderSize * 2),
                )
            },
            anchor = MarkerIconOffset(0.5f, 1f),
            reuseIdentifier = "spotMarkerIcon-${tint.toArgb()}-${borderColor.toArgb()}-${size.value}"
        )
    }
}

actual fun platformSizeAdjustment(): Float = 0.4f