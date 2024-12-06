package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.presentation.screens.MapStackKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.Serializable
import java.lang.ref.WeakReference

class MapStackEntry(
    val stackKey: MapStackKey,
    val objects: MutableMap<MapObjectKey, MapObject>
): Serializable

actual class MapsManager private constructor(
    val stack: MutableList<MapStackEntry>
): MapsManagerHandle {

    private val current: MutableMap<MapObjectKey, MapObject>? get() = stack.lastOrNull()?.objects

    private val _objects = MutableStateFlow<Collection<MapObject>>(listOf())
    val objects: Flow<Collection<MapObject>> = _objects

    private fun update() {
        _objects.value = current?.values ?: listOf()
    }

    private fun entry(stackKey: MapStackKey): MutableMap<MapObjectKey, MapObject>? = stack.firstOrNull {
        it.stackKey == stackKey
    }?.objects

    override fun add(obj: MapObject, stackKey: MapStackKey) {
        entry(stackKey)?.set(obj.key, obj)
        update()
    }

    override fun delete(key: MapObjectKey, stackKey: MapStackKey) {
        entry(stackKey)?.remove(key)
        update()
    }

    override fun show(pos: Location, stackKey: MapStackKey) {}
    override fun show(tl: Location, br: Location, stackKey: MapStackKey) {}

    actual fun push(key: MapStackKey) {
        if (stack.any { it.stackKey == key }) return
        stack.add(MapStackEntry(key, mutableMapOf()))
        update()
    }

    actual fun pop(key: MapStackKey?) {
        stack.removeAt(stack.lastIndex)
    }

    actual fun handleFor(key: MapStackKey): MapsHandle {
        return DefaultMapsHandle(WeakReference(this), key)
    }

    companion object {

        internal fun restore(value: List<MapStackEntry>): MapsManager {
            return MapsManager(value.toMutableList())
        }

        fun create(): MapsManager {
            return MapsManager(mutableListOf())
        }

    }

}

class MapsManagerSaver: Saver<MapsManager, List<MapStackEntry>> {
    override fun restore(value: List<MapStackEntry>): MapsManager {
        return MapsManager.restore(value)
    }

    override fun SaverScope.save(value: MapsManager): List<MapStackEntry> {
        return value.stack
    }
}

@Composable
actual fun mapsHandle(): MapsHandle? {
    val key = LocalMapStackKey.current ?: return null
    val manager = LocalMapsManager.current
    return manager.handleFor(key)
}