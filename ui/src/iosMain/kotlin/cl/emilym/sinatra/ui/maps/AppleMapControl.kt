package cl.emilym.sinatra.ui.maps

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.geometry.Size
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.ui.toCoordinateSpan
import cl.emilym.sinatra.ui.toNative
import cl.emilym.sinatra.ui.toShared
import kotlinx.cinterop.ExperimentalForeignApi

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

    override fun showBounds(bounds: MapRegion) {
        val center = bounds.center
        val span = bounds.toCoordinateSpan()
        state.animate(CameraDescription(
            center, span
        ))
    }

    override fun showPoint(center: MapLocation, zoom: Float) {
        state.animate(CameraDescription(
            center, zoom.toCoordinateSpan()
        ))
    }
}