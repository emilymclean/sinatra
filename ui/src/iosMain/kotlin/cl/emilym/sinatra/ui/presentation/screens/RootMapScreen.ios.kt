package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cl.emilym.sinatra.ui.navigation.MapControl
import cl.emilym.sinatra.ui.navigation.MapScope

@Composable
actual fun Map(content: @Composable MapControl.(@Composable () -> Unit) -> Unit) {
}