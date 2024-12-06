package cl.emilym.sinatra.ui.presentation

import cl.emilym.sinatra.data.models.Location
import java.util.UUID

actual fun generateMapObjectKey() = UUID.randomUUID().toString()

actual class MapsManager : MapsManagerHandle {
    override fun add(obj: MapObject) {}

    override fun delete(key: MapObjectKey) {}

    override fun show(pos: Location) {}

    override fun show(tl: Location, br: Location) {}
}