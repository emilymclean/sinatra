package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.presentation.theme.defaultLineColor

expect class MapScope {

    @Composable
    fun Marker(location: Location)

    @Composable
    fun Line(
        points: List<Location>,
        color: Color = defaultLineColor()
    )

}