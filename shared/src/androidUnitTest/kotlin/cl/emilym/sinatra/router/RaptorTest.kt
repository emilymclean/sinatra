package cl.emilym.sinatra.router

import cl.emilym.gtfs.networkgraph.EdgeType
import cl.emilym.gtfs.networkgraph.Graph
import cl.emilym.gtfs.networkgraph.NodeType
import cl.emilym.sinatra.RouterException
import io.github.aakira.napier.DebugAntilog
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
        assertEquals(Journey(
            stops = listOf("8119", "8105"),
            connections = listOf(
                JourneyConnection.Travel(
                    routeId = "ACTO001",
                    heading = "Gungahlin Pl",
                    startTime = 32476L,
                    endTime = 33307L
                )
            )
        ), result)
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
        assertEquals(Journey(
            stops = listOf("SWN", "8119", "8105", "MCK"),
            connections = listOf(
                JourneyConnection.Transfer(
                    travelTime = 16
                ),
                JourneyConnection.Travel(
                    routeId = "ACTO001",
                    heading = "Gungahlin Pl",
                    startTime = 32476L,
                    endTime = 33307L
                ),
                JourneyConnection.Transfer(travelTime = 9)
            )
        ), result)
    }

    @Test
    fun testValidJourneyAcrossMultipleRoutes() {
        val result = raptor.calculate(
            Duration.parseIsoString("PT09H").inWholeSeconds,
            STOP_ID_CANBERRA_RAILWAY_STATION,
            STOP_ID_MANNING_CLARK
        )
        assertEquals(result, Journey(
            stops = listOf("3320", "2232", "3406", "8129", "8105", "MCK"),
            connections = listOf(
                JourneyConnection.Transfer(
                    travelTime = 1040
                ),
                JourneyConnection.Travel(
                    routeId = "6-10647",
                    heading = "City ANU",
                    startTime = 33480L,
                    endTime = 37080L
                ),
                JourneyConnection.Transfer(
                    travelTime = 109
                ),
                JourneyConnection.Travel(
                    routeId = "ACTO001",
                    heading = "Gungahlin Pl",
                    startTime = 37200L,
                    endTime = 38557
                ),
                JourneyConnection.Transfer(
                    travelTime = 9
                )
            )
        ))
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