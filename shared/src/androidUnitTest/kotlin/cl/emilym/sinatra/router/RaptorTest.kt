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
import kotlin.time.Duration

class RaptorTest {

    companion object {
        const val STOP_ID_SWINDEN_STREET_ALG = "8118"
        const val STOP_ID_SWINDEN_STREET_GGN = "8119"
        const val STOP_ID_SWINDEN_STREET = "SWN"
        const val STOP_ID_MANNING_CLARK_ALG = "8104"
        const val STOP_ID_MANNING_CLARK_GGN = "8105"
        const val STOP_ID_MANNING_CLARK = "MCK"
        const val START_OF_DAY = 1734786000 // 2024-12-22 00:00:00 AEDT
    }

    lateinit var graph: Graph
    lateinit var raptor: Raptor

    @BeforeTest
    fun setup() {
        graph = Graph.decodeFromByteArray(this::class.java.classLoader.getResource("network_graph.pb").readBytes())
        raptor = Raptor(graph)
    }

    @Test
    fun testValidJourneyOnSingleRoute() {
        val result = raptor.calculate(
            Duration.parseIsoString("PT09H").inWholeSeconds,
            STOP_ID_SWINDEN_STREET_GGN,
            STOP_ID_MANNING_CLARK_GGN
        )
        assertEquals(result, Journey(
            stops = listOf("8119", "8105"),
            connections = listOf(
                JourneyConnection.Travel(
                    routeId = "ACTO001",
                    heading = "Gungahlin Pl",
                    startTime = 32400L,
                    endTime = 33307L
                )
            )
        ))
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
        assertEquals(result, Journey(
            stops = listOf("SWN", "8119", "8105", "MCK"),
            connections = listOf(
                JourneyConnection.Transfer(
                    travelTime = 16
                ),
                JourneyConnection.Travel(
                    routeId = "ACTO001",
                    heading = "Gungahlin Pl",
                    startTime = 32416L,
                    endTime = 33307L
                ),
                JourneyConnection.Transfer(travelTime = 9)
            )
        ))
    }

}