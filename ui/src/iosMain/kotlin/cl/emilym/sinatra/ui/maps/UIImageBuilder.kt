package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import cl.emilym.sinatra.ui.sinatraAllocArrayOf
import cl.emilym.sinatra.ui.toNativeCGColor
import cl.emilym.sinatra.ui.widgets.toFloatPx
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.pointed
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFURLRef
import platform.CoreGraphics.CGContextFillEllipseInRect
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextSetFillColorWithColor
import platform.CoreGraphics.CGPDFDocumentCreateWithURL
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSBundle
import platform.UIKit.UIGraphicsBeginImageContext
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import cnames.structs.CGPDFDocument
import io.github.aakira.napier.Napier
import kotlinx.cinterop.interpretPointed
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.useContents
import platform.CoreFoundation.CFDataRef
import platform.CoreGraphics.CGBlendMode
import platform.CoreGraphics.CGContextDrawPDFPage
import platform.CoreGraphics.CGContextFillRect
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextSetBlendMode
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGDataProviderCreateWithCFData
import platform.CoreGraphics.CGPDFDocumentCreateWithProvider
import platform.CoreGraphics.CGPDFDocumentGetPage
import platform.CoreGraphics.CGPDFPageGetBoxRect
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.kCGPDFArtBox
import platform.CoreGraphics.kCGPDFMediaBox
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Foundation.dataWithBytes

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
        with(density) { height.roundToPx().toDouble() },
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

@OptIn(ExperimentalForeignApi::class)
fun UIImageScope.pdf(pdf: ByteArray, tint: Color, x: Number, y: Number, width: Number, height: Number) {
    uiImageBuilder(
        width.toDouble(),
        height.toDouble()
    ) {
        memScoped {
            CGContextSaveGState(context)
            val dataPtr = pdf.toCValues().getPointer(this)

            val data = NSData.dataWithBytes(dataPtr, pdf.size.toULong())
            val cfData = CFBridgingRetain(data) as CFDataRef

            val provider = CGDataProviderCreateWithCFData(cfData)!!
            val pdf = CGPDFDocumentCreateWithProvider(provider)!!
            val page = CGPDFDocumentGetPage(pdf, 1u)!!

            val box = CGPDFPageGetBoxRect(page, kCGPDFArtBox)
            CGContextTranslateCTM(context, 0.0, height.toDouble())
            CGContextScaleCTM(context, 1.0, -1.0)
            box.useContents {
                CGContextScaleCTM(context,  width.toDouble() / size.width, height.toDouble() / size.height)
            }

            CGContextDrawPDFPage(context, page)

            CGContextSetFillColorWithColor(context, tint.toNativeCGColor())
            CGContextSetBlendMode(context, CGBlendMode.kCGBlendModeSourceAtop)
            CGContextFillRect(context, box)

            CFRelease(cfData)
            CGContextRestoreGState(context)
        }
    }?.let {
        uiImage(
            it,
            x.toDouble(),
            y.toDouble(),
            width.toDouble(),
            height.toDouble()
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
fun UIImageScope.uiImage(image: UIImage, x: Number, y: Number, width: Number, height: Number) {
    val rect = CGRectMake(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    image.drawInRect(rect)
}