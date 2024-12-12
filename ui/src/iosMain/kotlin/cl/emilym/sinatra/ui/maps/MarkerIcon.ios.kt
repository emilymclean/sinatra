package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.ui.minimumTouchTarget
import cl.emilym.sinatra.ui.widgets.toIntPx
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.UIKit.UIImage
import platform.darwin.NSObject

actual interface MarkerIcon {
    val reuseIdentifier: String
    val anchor: MarkerIconOffset

    fun annotationView(annotation: MKAnnotationProtocol): MKAnnotationView
}

class UIImageMarkerIcon(
    override val reuseIdentifier: String,
    override val anchor: MarkerIconOffset,
    val image: UIImage?
): MarkerIcon {

    override fun annotationView(annotation: MKAnnotationProtocol): MKAnnotationView {
        return UIImageAnnotationView(
            image, anchor, annotation, reuseIdentifier
        )
    }

}

@OptIn(ExperimentalForeignApi::class)
class UIImageAnnotationView(
    image: UIImage?,
    anchor: MarkerIconOffset,
    annotation: MKAnnotationProtocol,
    reuseIdentifier: String
): MKAnnotationView(annotation, reuseIdentifier) {
    init {
        image?.let {
            this.image = image
            this.centerOffset = image.size.useContents {
                CGPointMake(
                    anchor.x.toDouble(),
                    anchor.y.toDouble(),
                )
            }
        }
    }
}

@Composable
actual fun circularIcon(color: Color, borderColor: Color, size: Dp, borderWidth: Dp): MarkerIcon {
    val markerCirclePadding = (minimumTouchTarget - size) / 2
    val totalMarkerCircleSize = minimumTouchTarget

    val halfBorderSize = borderWidth.toIntPx() / 2
    val canvasSize = totalMarkerCircleSize.toIntPx()
    val markerPadding = markerCirclePadding.toIntPx()
    val markerRadius = (size / 2).toIntPx()

    return UIImageMarkerIcon(
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
        reuseIdentifier = "circleIcon-${color.toArgb()}-${borderColor.toArgb()}-${size.toIntPx()}-${borderWidth.toIntPx()}"
    )
}

@Composable
actual fun spotMarkerIcon(
    tint: Color,
    borderColor: Color,
    size: Dp
): MarkerIcon {
    return circularIcon(tint, borderColor, size)
}