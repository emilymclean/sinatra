package cl.emilym.sinatra.ui.maps

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun bitmapDescriptorBuilder(
    width: Dp,
    height: Dp,
    density: Density,
    init: Canvas.() -> Unit
): BitmapDescriptor {
    return BitmapDescriptorFactory
        .fromBitmap(bitmapBuilder(width, height, density, init = init))
}

fun bitmapDescriptorBuilder(
    width: Int,
    height: Int,
    init: Canvas.() -> Unit
): BitmapDescriptor {
    return BitmapDescriptorFactory
        .fromBitmap(bitmapBuilder(width, height, init = init))
}

fun bitmapBuilder(
    width: Dp,
    height: Dp,
    density: Density,
    config: Config = Config.ARGB_8888,
    init: Canvas.() -> Unit
): Bitmap {
    val pxWidth = with(density) { width.roundToPx() }
    val pxHeight = with(density) { height.roundToPx() }
    return bitmapBuilder(width, height, density, config, init)
}

fun bitmapBuilder(
    width: Int,
    height: Int,
    config: Config = Config.ARGB_8888,
    init: Canvas.() -> Unit
): Bitmap {
    return createBitmap(width, height, config).builder(false, init)
}

fun Bitmap.builder(copy: Boolean = true, init: Canvas.() -> Unit): Bitmap {
    val bitmap = when(copy) {
        true -> createBitmap(this)
        false -> this
    }
    Canvas(bitmap).init()
    return bitmap
}

fun Canvas.circle(color: Color, x: Number, y: Number, radius: Number) {
    circle(paint(color), x, y, radius)
}

fun Canvas.circle(paint: Paint, x: Number, y: Number, radius: Number) {
    val r = radius.toFloat()
    drawCircle(x.toFloat() + r, y.toFloat() + r, radius.toFloat(), paint)
}

fun Canvas.rectangle(color: Color, x: Number, y: Number, width: Number, height: Number) {
    rectangle(paint(color), x, y, width, height)
}

fun Canvas.rectangle(paint: Paint, x: Number, y: Number, width: Number, height: Number) {
    drawRect(x.toFloat(), y.toFloat(), (x.toFloat()) + (width.toFloat()), (y.toFloat()) + (height.toFloat()), paint)
}

fun paint(color: Color, init: Paint.() -> Unit = {}): Paint {
    return paint {
        setColor(color.toArgb())
        init()
    }
}

fun paint(init: Paint.() -> Unit): Paint {
    return Paint().apply(init)
}

