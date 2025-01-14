package cl.emilym.sinatra.ui.maps

import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import kotlin.test.Test
import kotlin.test.assertEquals

class MapHelpersTest {

    @Test
    fun `wider map with native map zoom of 10 should calculate to 10`() {
        val ne = MapLocation(-35.06972707051231, 149.47432275390625)
        val sw = MapLocation(-35.4901273207884, 148.78767724609375)
        val mapWidth = 500f
        val mapHeight = 375f
        val mapRegion = MapRegion(
            MapLocation(ne.lat, sw.lng),
            MapLocation(sw.lat, ne.lng),
        )

        val result = mapRegion.toZoom(mapWidth, mapHeight)
        assertEquals(10f, result)
    }

    @Test
    fun `taller map with native map zoom of 10 should calculate to 10`() {
        val ne = MapLocation(-34.99944842708678, 149.3884920654297)
        val sw = MapLocation(-35.55998160569954, 148.8735079345703)
        val mapWidth = 375f
        val mapHeight = 500f
        val mapRegion = MapRegion(
            MapLocation(ne.lat, sw.lng),
            MapLocation(sw.lat, ne.lng),
        )

        val result = mapRegion.toZoom(mapWidth, mapHeight)
        assertEquals(10f, result)
    }

}