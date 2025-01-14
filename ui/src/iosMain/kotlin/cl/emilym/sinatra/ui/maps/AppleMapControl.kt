package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.data.models.ScreenRegionSizePx
import cl.emilym.sinatra.ui.toNative
import cl.emilym.sinatra.ui.toShared
import kotlinx.cinterop.ExperimentalForeignApi

@Composable
actual fun rememberMapControl(): MapControl {
    return remember { SafeMapControl() }
}

const val ZOOM_OFFSET = 1f

class AppleMapControl(
    private val state: MapKitState,
    override val contentViewportSize: ScreenRegionSizePx,
    override val contentViewportPadding: PrecomputedPaddingValues,
    override val bottomSheetHalfHeight: Float,
    override val density: Density
): AbstractMapControl() {

    override val zoom: Float
        get() = calculateZoom(nativeZoom, visibleMapSize, density) + ZOOM_OFFSET

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

    override val nativeZoom: Float get() = state.cameraDescription.zoom(contentViewportSize.dp(density.density))

    override fun showBounds(bounds: MapRegion) {
        state.animate(CameraDescription(
            bounds
        ))
    }

}