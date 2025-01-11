package cl.emilym.sinatra.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

val minimumTouchTarget = 48.dp

val canberra = MapLocation(
    -35.2802, 149.1310
)

val canberraRegion = MapRegion(
    MapLocation(-35.105698, 148.717365),
    MapLocation(-36.063920, 149.546833)
)

val canberraZoom = 10f

const val NEAREST_STOP_RADIUS = 1.0