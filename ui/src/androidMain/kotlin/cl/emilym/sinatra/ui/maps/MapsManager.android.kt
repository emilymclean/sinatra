package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.ui.presentation.screens.MapStackKey
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import java.io.Serializable
import java.lang.ref.WeakReference
import java.util.Map.entry

class MapStackEntry(
    val stackKey: MapStackKey,
    val objects: Map<MapObjectKey, MapObject>
): Serializable

actual class MapsManager private constructor(
    stack: List<MapStackEntry>
): MapsManagerHandle {

    val stack = MutableStateFlow(stack)
    val objects: Flow<Collection<MapObject>> = this.stack.map { it.lastOrNull()?.objects?.values ?: listOf() }

    private fun update(stackKey: MapStackKey, operation: (Map<MapObjectKey, MapObject>) -> Map<MapObjectKey, MapObject>) {
        stack.getAndUpdate {
            it.map {
                when {
                    it.stackKey == stackKey -> MapStackEntry(
                        stackKey,
                        operation(it.objects)
                    )
                    else -> it
                }
            }
        }
    }

    override fun add(obj: MapObject, stackKey: MapStackKey) {
        update(stackKey) {
            it + mapOf(
                obj.key to obj
            )
        }
    }

    override fun delete(key: MapObjectKey, stackKey: MapStackKey) {
        update(stackKey) {
            it.filter { it.key == stackKey }
        }
    }

    override fun show(pos: Location, stackKey: MapStackKey) {}
    override fun show(tl: Location, br: Location, stackKey: MapStackKey) {}

    actual fun push(key: MapStackKey) {
        stack.getAndUpdate {
            if (it.any { it.stackKey == key }) return@getAndUpdate it
            it + MapStackEntry(key, mapOf())
        }
    }

    actual fun pop(key: MapStackKey?) {
        stack.getAndUpdate {
            when {
                key != null -> it.filter { it.stackKey == key }
                else -> it.dropLast(1)
            }
        }
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
        return value.stack.value
    }
}

@Composable
actual fun mapsHandle(): MapsHandle? {
    val key = LocalMapStackKey.current ?: return null
    val manager = LocalMapsManager.current
    return manager.handleFor(key)
}