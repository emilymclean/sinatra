package cl.emilym.sinatra.ui

import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion

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

object LanguageConsts {
    const val MAINLAND_CHINESE_BCP = "zh-Hans-CN"
    const val US_BCP = "en-US"
}