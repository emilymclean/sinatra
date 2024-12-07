package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import cl.emilym.sinatra.data.models.Location

expect class MapScope {

    @Composable
    fun Marker(location: Location)

    @Composable
    fun Line(points: List<Location>)

}