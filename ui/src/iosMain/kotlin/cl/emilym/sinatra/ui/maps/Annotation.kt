package cl.emilym.sinatra.ui.maps

import cl.emilym.sinatra.ui.toNative
import kotlinx.cinterop.ExperimentalForeignApi
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolyline

@OptIn(ExperimentalForeignApi::class)
fun updatePointAnnotation(
    annotation: MKPointAnnotation,
    item: MarkerItem
) {
    annotation.setCoordinate(item.location.toNative())
}