package cl.emilym.sinatra.ui.widgets

import androidx.compose.ui.platform.UriHandler
import cl.emilym.sinatra.data.models.MapLocation

expect fun openMaps(uriHandler: UriHandler, to: MapLocation, from: MapLocation? = null)