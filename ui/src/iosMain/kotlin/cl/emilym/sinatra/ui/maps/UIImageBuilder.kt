package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import cl.emilym.sinatra.ui.toNativeCGColor
import cl.emilym.sinatra.ui.widgets.toFloatPx
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGContextFillEllipseInRect
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextSetFillColorWithColor
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.UIKit.UIGraphicsBeginImageContext
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage

class UIImageScope @OptIn(ExperimentalForeignApi::class) private constructor(
    val context: CGContextRef?
) {
    companion object {
        @OptIn(ExperimentalForeignApi::class)
        fun builder(width: Double, height: Double, init: UIImageScope.() -> Unit): UIImage? {
            val size = CGSizeMake(width, height)
            UIGraphicsBeginImageContext(size)

            val context = UIGraphicsGetCurrentContext()
            UIImageScope(context).init()

            val image = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            return image
        }
    }
}

fun uiImageBuilder(
    width: Dp,
    height: Dp,
    density: Density,
    init: UIImageScope.() -> Unit
): UIImage? {
    return UIImageScope.builder(
        with(density) { width.roundToPx().toDouble() },
        with(density) { width.roundToPx().toDouble() },
        init
    )
}

fun uiImageBuilder(
    width: Double,
    height: Double,
    init: UIImageScope.() -> Unit
): UIImage? {
    return UIImageScope.builder(width, height, init)
}

@OptIn(ExperimentalForeignApi::class)
fun UIImageScope.circle(color: Color, x: Number, y: Number, radius: Number) {
    CGContextSaveGState(context)
    CGContextSetFillColorWithColor(context, color.toNativeCGColor())

    val diameter = radius.toDouble() * 2.0
    val rect = CGRectMake(x.toDouble(), y.toDouble(), diameter, diameter)
    CGContextFillEllipseInRect(context, rect)

    CGContextRestoreGState(context)
}