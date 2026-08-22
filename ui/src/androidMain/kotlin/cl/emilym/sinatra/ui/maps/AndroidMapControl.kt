package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Density
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.data.models.ScreenRegionSizePx
import cl.emilym.sinatra.ui.toNative
import cl.emilym.sinatra.ui.toShared
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
actual fun rememberMapControl(): MapControl {
    return remember { SafeMapControl() }
}

class AndroidMapControl(
    private val cameraPositionState: CameraPositionState,
    private val mainScope: CoroutineScope,
    override val contentViewportSize: ScreenRegionSizePx,
    override val density: Density,
    override val contentViewportPadding: PrecomputedPaddingValues,
    override val bottomSheetHalfHeight: Float,
): AbstractMapControl() {

    override fun toScreenSpace(location: MapLocation): ScreenLocation? {
        val projection = cameraPositionState.projection ?: return null
        return projection.toScreenLocation(location.toNative()).toShared()
    }

    override fun toMapSpace(coordinate: ScreenLocation): MapLocation? {
        val projection = cameraPositionState.projection ?: return null
        return projection.fromScreenLocation(coordinate.toNative()).toShared()
    }

    override val nativeZoom: Float get() = cameraPositionState.position.zoom

    override val cameraRegion: MapRegion? by derivedStateOf {
        val projection = cameraPositionState.projection ?: return@derivedStateOf null
        MapRegion(
            projection.fromScreenLocation(ScreenLocation(
                contentViewportPadding.top,
                contentViewportPadding.left,
            ).toNative()).toShared(),
            projection.fromScreenLocation(ScreenLocation(
                contentViewportSize.height + contentViewportPadding.top,
                contentViewportSize.width + contentViewportPadding.left
            ).toNative()).toShared()
        )
    }

    override fun showBounds(bounds: MapRegion) {
        mainScope.launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.toNative(),
                    0
                )
            )
        }
    }

}