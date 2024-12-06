package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import cl.emilym.sinatra.data.models.Location

class MapObjectScope(
    private val addMarker: (Location) -> Unit
) {
    fun marker(position: Location) {
        addMarker(position)
    }
}

@Composable
fun MapObjects(items: MapObjectScope.() -> Unit) {
    val currentMarkers = remember { mutableListOf<MapObjectKey>() }
    val handle = mapsHandle() ?: return
    val scope = remember {
        MapObjectScope(
            addMarker = {
                currentMarkers.add(handle.addMarker(it))
            }
        )
    }
    scope.items()

    DisposableEffect(items) {
        onDispose {
            currentMarkers.forEach { handle.delete(it) }
            currentMarkers.clear()
        }
    }
}
