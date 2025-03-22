package cl.emilym.sinatra.ui.widgets

import androidx.compose.ui.platform.UriHandler
import cl.emilym.sinatra.data.models.MapLocation

actual fun openMaps(
    uriHandler: UriHandler,
    to: MapLocation,
    from: MapLocation?
) {
    uriHandler.openUri("google.navigation:q=${to.lat},${to.lng}")
}