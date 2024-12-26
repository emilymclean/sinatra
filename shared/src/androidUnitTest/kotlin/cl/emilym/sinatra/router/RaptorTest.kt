package cl.emilym.sinatra.router

import cl.emilym.gtfs.networkgraph.Graph
import cl.emilym.sinatra.RouterException
import io.github.aakira.napier.Napier
import pbandk.decodeFromByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.time.Duration

class RaptorTest {

    companion object {
        const val STOP_ID_SWINDEN_STREET_GGN = "8119"
        const val STOP_ID_SWINDEN_STREET = "SWN"
        const val STOP_ID_MANNING_CLARK_GGN = "8105"
        const val STOP_ID_MANNING_CLARK = "MCK"
        const val STOP_ID_CANBERRA_RAILWAY_STATION = "3320"
    }

    lateinit var graph: Graph
    lateinit var raptor: Raptor

    @BeforeTest
    fun setup() {
        graph = Graph.decodeFromByteArray(this::class.java.classLoader.getResource("network_graph.pb").readBytes())
        raptor = Raptor(graph, graph.mappings.serviceIds)
    }

    @Test
    fun testValidJourneyOnSingleRoute() {
        val result = raptor.calculate(
            Duration.parseIsoString("PT09H").inWholeSeconds,
            STOP_ID_SWINDEN_STREET_GGN,
            STOP_ID_MANNING_CLARK_GGN
        )
        assertEquals(RaptorJourney(listOf()), result)
    }

    @Test
    fun testValidJourneyOnSingleRouteWithTransfer() {
        val result = try {
            raptor.calculate(
                Duration.parseIsoString("PT09H").inWholeSeconds,
                STOP_ID_SWINDEN_STREET,
                STOP_ID_MANNING_CLARK
            )
        } catch(e: RouterException) { null }
        assertEquals(RaptorJourney(listOf()), result)
    }

    @Test
    fun testValidJourneyAcrossMultipleRoutes() {
        val result = raptor.calculate(
            Duration.parseIsoString("PT09H").inWholeSeconds,
            STOP_ID_CANBERRA_RAILWAY_STATION,
            STOP_ID_MANNING_CLARK
        )
        println(result)
        assertEquals(RaptorJourney(listOf()), result)
    }

    @Test
    fun testValidJourneyWithFewerServices() {
        val raptor = Raptor(graph, listOf("2023-COMBVAC-Weekday-05", "WD"))
        val result = raptor.calculate(
            Duration.parseIsoString("PT09H").inWholeSeconds,
            STOP_ID_SWINDEN_STREET,
            STOP_ID_MANNING_CLARK
        )
        assertEquals(RaptorJourney(listOf()), result)
    }

    @Test
    fun testInvalidJourney() {
        assertFails {
            raptor.calculate(
                Duration.parseIsoString("PT23H").inWholeSeconds,
                STOP_ID_CANBERRA_RAILWAY_STATION,
                STOP_ID_MANNING_CLARK
            )
        }
    }

}