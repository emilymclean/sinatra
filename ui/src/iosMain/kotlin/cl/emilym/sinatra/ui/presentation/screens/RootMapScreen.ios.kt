package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.navigation.MapControl
import cl.emilym.sinatra.ui.navigation.MapScope

@Composable
actual fun Map(content: @Composable MapControl.(@Composable () -> Unit) -> Unit) {
    val control = object : MapControl {
        override fun zoomToArea(bounds: Bounds, padding: Int) {}

        override fun zoomToArea(topLeft: Location, bottomRight: Location, padding: Int) {}

        override fun zoomToPoint(location: Location, zoom: Float) {}
    }
    control.content {}
}