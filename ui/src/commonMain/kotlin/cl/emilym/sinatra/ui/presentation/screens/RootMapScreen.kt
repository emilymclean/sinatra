package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.navigation.MapScope

expect class RootMapScreen(): Screen

@Composable
fun MapScope.Test() {
    Marker(canberra)
    Marker(canberra.copy(lat = canberra.lat + 0.1))
    Line(listOf(
        canberra.copy(lat = canberra.lat - 0.5, lng = canberra.lng - 0.5),
        canberra.copy(lat = canberra.lat + 0.5, lng = canberra.lng + 0.5)
    ))
}