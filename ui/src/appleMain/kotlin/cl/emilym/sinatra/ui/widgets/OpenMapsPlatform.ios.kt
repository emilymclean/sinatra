package cl.emilym.sinatra.ui.widgets

import androidx.compose.ui.platform.UriHandler
import cl.emilym.gtfs.Location
import cl.emilym.sinatra.data.models.MapLocation

actual fun openMaps(
    uriHandler: UriHandler,
    to: MapLocation,
    from: MapLocation?
) {
    uriHandler.openUri("maps://daddr=${to.lat},${to.lng}")
}