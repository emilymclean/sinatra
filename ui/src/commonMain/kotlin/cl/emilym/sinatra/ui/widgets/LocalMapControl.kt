package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.staticCompositionLocalOf
import cl.emilym.sinatra.ui.maps.MapControl

val LocalMapControl = staticCompositionLocalOf<MapControl> { error("No map control provided") }