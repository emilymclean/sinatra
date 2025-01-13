package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.ui.toNative
import cl.emilym.sinatra.ui.toShared
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi

@Composable
actual fun rememberMapControl(): MapControl {
    return remember { SafeMapControl() }
}

class AppleMapControl(
    private val state: MapKitState,
    override val contentViewportSize: Size,
    override val contentViewportPadding: PrecomputedPaddingValues,
    override val bottomSheetHalfHeight: Float
): AbstractMapControl() {

    @OptIn(ExperimentalForeignApi::class)
    override fun toScreenSpace(location: MapLocation): ScreenLocation? {
        val map = state.map ?: return null
        return map.convertCoordinate(location.toNative(), map).toShared()
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun toMapSpace(coordinate: ScreenLocation): MapLocation? {
        val map = state.map ?: return null
        return map.convertPoint(coordinate.toNative(), toCoordinateFromView = map).toShared()
    }

    override val nativeZoom: Float get() = state.cameraDescription.zoom(contentViewportSize)

    override fun showBounds(bounds: MapRegion) {
        val center = bounds.center
        val span = bounds.toCoordinateSpan()
        state.animate(CameraDescription(
            center, span
        ))
    }

    override fun showPoint(center: MapLocation, zoom: Float) {
        state.animate(CameraDescription(
            center,
            zoom.toCoordinateSpan(
                contentViewportSize
            ).adjustForLatitude(center.lat)
        ))
    }

    override fun zoomToPoint(location: MapLocation, zoom: Float) {
        Napier.d("Zooming to point = $location")
        super.zoomToPoint(location, zoom)
    }
}