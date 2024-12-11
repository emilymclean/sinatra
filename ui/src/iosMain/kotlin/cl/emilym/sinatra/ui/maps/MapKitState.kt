package cl.emilym.sinatra.ui.maps

import cl.emilym.sinatra.data.models.Location
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreLocation.CLLocationDegrees
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpan
import platform.MapKit.MKCoordinateSpanMake
import kotlin.math.pow

@OptIn(ExperimentalForeignApi::class)
fun Location.toMaps(): CValue<CLLocationCoordinate2D> {
    return CLLocationCoordinate2DMake(
        latitude = lat,
        longitude = lng
    )
}

@OptIn(ExperimentalForeignApi::class)
fun Float.toCoordinateSpan(): CValue<MKCoordinateSpan> {
    val span = 360 / 2f.pow(this)
    return MKCoordinateSpanMake(
        latitudeDelta = span.toDouble(),
        longitudeDelta = span.toDouble()
    )
}

class MapKitState @OptIn(ExperimentalForeignApi::class) constructor(
    val coordinate: CValue<CLLocationCoordinate2D>
)

@OptIn(ExperimentalForeignApi::class)
fun createRegion(location: Location, zoom: Float): CValue<MKCoordinateRegion> {
    return MKCoordinateRegionMake(
        centerCoordinate = location.toMaps(),
        span = zoom.toCoordinateSpan()
    )
}