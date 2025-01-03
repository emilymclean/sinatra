package cl.emilym.sinatra.ui.navigation

import androidx.compose.ui.geometry.Size
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.ui.maps.AbstractMapControl
import cl.emilym.sinatra.ui.maps.PrecomputedPaddingValues
import cl.emilym.sinatra.ui.toNative
import cl.emilym.sinatra.ui.toShared
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AndroidMapControl(
    private val cameraPositionState: CameraPositionState,
    private val mainScope: CoroutineScope,
    override val contentViewportSize: Size,
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

    override fun showPoint(center: MapLocation, zoom: Float) {
        mainScope.launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    center.toNative(),
                    zoom
                )
            )
        }
    }

}