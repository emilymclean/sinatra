package cl.emilym.sinatra.ui

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.CoordinateSpan
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.ui.maps.MarkerAnnotation
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.PrecomputedPaddingValuesDp
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
import org.jetbrains.compose.resources.StringResource
import platform.CoreGraphics.CGColorRef
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRect
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateForMapPoint
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionForMapRect
import platform.MapKit.MKCoordinateSpan
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapPointForCoordinate
import platform.MapKit.MKMapPointMake
import platform.MapKit.MKMapRect
import platform.MapKit.MKMapRectMake
import platform.MapKit.MKMapView
import platform.UIKit.UIColor
import platform.UIKit.UIEdgeInsetsMake
import platform.UIKit.UIScreen
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.open_maps_ios
import kotlin.math.abs

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

@OptIn(ExperimentalForeignApi::class)
fun CValue<CGRect>.toSize(): Size {
    val scale = UIScreen.mainScreen.scale.toFloat()
    return useContents {
        Size(
            size.width.toFloat() * scale,
            size.height.toFloat() * scale,
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
fun MKCoordinateSpan.toShared(): CoordinateSpan {
    return CoordinateSpan(
        deltaLatitude = latitudeDelta,
        deltaLongitude = longitudeDelta
    )
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
            x.toFloat(),
            y.toFloat()
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
fun ScreenLocation.toNative(): CValue<CGPoint> {
    return CGPointMake(x.toDouble(), y.toDouble())
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
fun MapRegion.toNative(): CValue<MKCoordinateRegion> {
    val topLeft = MKMapPointForCoordinate(topLeft.toNative())
    val bottomRight = MKMapPointForCoordinate(bottomRight.toNative())
    val width = abs(topLeft.useContents { x } - bottomRight.useContents { x })
    val height = abs(topLeft.useContents { y } - bottomRight.useContents { y })
    return MKCoordinateRegionForMapRect(
        MKMapRectMake(
            topLeft.useContents { x },
            topLeft.useContents { y },
            width,
            height
        )
    )
}

@OptIn(ExperimentalForeignApi::class)
fun MKMapRect.toShared(): MapRegion {
    val topLeft = MKCoordinateForMapPoint(
        MKMapPointMake(origin.x, origin.y)
    ).toShared()
    val bottomRight = MKCoordinateForMapPoint(
        MKMapPointMake(origin.x + size.width, origin.y + size.height)
    ).toShared()

    return MapRegion(
        topLeft,
        bottomRight
    )
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

@OptIn(ExperimentalForeignApi::class)
fun MarkerAnnotation.matches(markerItem: MarkerItem): Boolean {
    return id == markerItem.id &&
            markerItem.icon?.reuseIdentifier == icon?.reuseIdentifier &&
            location == markerItem.location
}

internal actual val Res.string.open_maps: StringResource
    get() = Res.string.open_maps_ios


@OptIn(ExperimentalForeignApi::class)
fun MKMapView.applyPadding(padding: PrecomputedPaddingValuesDp) {
    layoutMargins = UIEdgeInsetsMake(
        padding.top.toDouble(),
        padding.left.toDouble(),
        padding.bottom.toDouble(),
        padding.right.toDouble(),
    )
}