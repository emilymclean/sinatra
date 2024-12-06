package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.presentation.screens.MapStackKey

interface MapsManagerHandle {

    fun add(obj: MapObject, stackKey: MapStackKey)
    fun delete(key: MapObjectKey, stackKey: MapStackKey)
    fun show(pos: Location, stackKey: MapStackKey)
    fun show(tl: Location, br: Location, stackKey: MapStackKey)

}

expect class MapsManager: MapsManagerHandle {

    fun push(key: MapStackKey)
    fun pop(key: MapStackKey?)
    fun handleFor(key: MapStackKey): MapsHandle

}

val LocalMapsManager = staticCompositionLocalOf<MapsManager> { error("No map available") }
val LocalMapStackKey = staticCompositionLocalOf<MapStackKey?> { null }

@Composable
expect fun mapsHandle(): MapsHandle?