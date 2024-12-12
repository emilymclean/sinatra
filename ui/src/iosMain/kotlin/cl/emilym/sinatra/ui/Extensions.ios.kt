package cl.emilym.sinatra.ui

import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.ui.maps.CoordinateSpan
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.CValues
import kotlinx.cinterop.CVariable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.NativePlacement
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGPointMake
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateSpan
import platform.MapKit.MKCoordinateSpanMake
import platform.UIKit.UIColor
import kotlin.math.pow
import cnames.structs.CGColor
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.toCValues
import platform.CoreGraphics.CGColorRef
import platform.posix.malloc

@OptIn(ExperimentalForeignApi::class)
fun MapLocation.toNative(): CValue<CLLocationCoordinate2D> {
    return CLLocationCoordinate2DMake(
        latitude = lat,
        longitude = lng
    )
}

@OptIn(ExperimentalForeignApi::class)
fun CValue<CLLocationCoordinate2D>.toShared(): MapLocation {
    return useContents {
        MapLocation(
            latitude,
            longitude
        )
    }
}

fun Float.toCoordinateSpan(): CoordinateSpan {
    val span = 360 / 2.0.pow(this.toDouble())
    return CoordinateSpan(
        deltaLatitude = span,
        deltaLongitude = span
    )
}

@OptIn(ExperimentalForeignApi::class)
fun CValue<MKCoordinateSpan>.toShared(): CoordinateSpan {
    return useContents {
        CoordinateSpan(
            deltaLatitude = latitudeDelta,
            deltaLongitude = longitudeDelta
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
fun CoordinateSpan.toNative(): CValue<MKCoordinateSpan> {
    return MKCoordinateSpanMake(
        deltaLatitude,
        deltaLongitude
    )
}

@OptIn(ExperimentalForeignApi::class)
fun CValue<CGPoint>.toShared(): ScreenLocation {
    return useContents {
        ScreenLocation(
            x.toInt(),
            y.toInt()
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
fun ScreenLocation.toNative(): CValue<CGPoint> {
    return CGPointMake(x.toDouble(), y.toDouble())
}

fun MapRegion.toCoordinateSpan(): CoordinateSpan {
    return CoordinateSpan(
        deltaLatitude = width,
        deltaLongitude = height
    )
}

fun Color.toNativeUIColor(): UIColor {
    return UIColor.colorWithRed(
        red.toDouble(),
        green.toDouble(),
        blue.toDouble(),
        alpha.toDouble()
    )
}

@OptIn(ExperimentalForeignApi::class)
fun Color.toNativeCGColor(): CGColorRef? {
    return toNativeUIColor().CGColor
}


@OptIn(ExperimentalForeignApi::class)
inline operator fun <reified T : CVariable> CPointer<T>.set(index: Int, item: CValues<T>) {
    val offset = index * sizeOf<T>()
    item.place(interpretCPointer(rawValue + offset)!!)
}

@OptIn(ExperimentalForeignApi::class)
inline fun <reified T: CVariable> NativePlacement.sinatraAllocArrayOf(vararg items: CValue<T>): CArrayPointer<T> {
    val array = allocArray<T>(items.size)
    items.forEachIndexed { index, item ->
        array[index] = item
    }
    return array
}

@OptIn(ExperimentalForeignApi::class)
inline fun <reified T: CVariable> NativePlacement.sinatraAllocArrayOf(items: List<CValue<T>>): CArrayPointer<T> {
    val array = allocArray<T>(items.size)
    items.forEachIndexed { index, item ->
        array[index] = item
    }
    return array
}