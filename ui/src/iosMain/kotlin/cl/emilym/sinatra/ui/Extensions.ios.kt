package cl.emilym.sinatra.ui

import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.ui.maps.CoordinateSpan
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGPointMake
import platform.CoreLocation.CLLocationCoordinate2D
import platform.MapKit.MKCoordinateSpan
import platform.MapKit.MKCoordinateSpanMake
import kotlin.math.pow

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