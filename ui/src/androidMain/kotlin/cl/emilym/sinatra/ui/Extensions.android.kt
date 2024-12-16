package cl.emilym.sinatra.ui

import android.graphics.Point
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.ScreenLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

fun MapLocation.toNative(): LatLng {
    return LatLng(
        lat,
        lng
    )
}

fun LatLng.toShared(): MapLocation {
    return MapLocation(
        latitude, longitude
    )
}

fun Point.toShared(): ScreenLocation {
    return ScreenLocation(x, y)
}

fun ScreenLocation.toNative(): Point {
    return Point(x,y)
}

fun MapRegion.toNative(): LatLngBounds {
    return LatLngBounds(
        LatLng(bottomRight.lat, topLeft.lng), // southwest
        LatLng(topLeft.lat, bottomRight.lng) // northeast
    )
}

fun LatLngBounds.toShared(): MapRegion {
    return MapRegion(
        MapLocation(northeast.latitude, southwest.longitude),
        MapLocation(southwest.latitude, northeast.longitude),
    )
}