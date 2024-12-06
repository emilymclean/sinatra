package cl.emilym.sinatra.ui.maps

import cl.emilym.sinatra.data.models.Location
import java.util.UUID

actual fun generateMapObjectKey() = UUID.randomUUID().toString()