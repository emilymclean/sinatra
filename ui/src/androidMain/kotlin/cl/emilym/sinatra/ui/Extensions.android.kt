package cl.emilym.sinatra.ui

import android.graphics.Point
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.data.models.ScreenRegionSizePx
import cl.emilym.sinatra.data.models.Zoom
import cl.emilym.sinatra.ui.maps.MapProjectionProvider
import cl.emilym.sinatra.ui.maps.calculateZoom
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import org.jetbrains.compose.resources.StringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.open_maps_android

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
    return ScreenLocation(x.toFloat(), y.toFloat())
}

fun ScreenLocation.toNative(): Point {
    return Point(x.toInt(), y.toInt())
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

fun MapProjectionProvider.calculateZoom(
    cameraPositionState: CameraPositionState,
    visibleMapSize: ScreenRegionSizePx,
    density: Density
): Zoom {
    return calculateZoom(
        cameraPositionState.position.zoom,
        visibleMapSize,
        density
    )
}

internal actual val Res.string.open_maps: StringResource
    get() = Res.string.open_maps_android