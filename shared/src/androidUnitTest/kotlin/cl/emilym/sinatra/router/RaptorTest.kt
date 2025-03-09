package cl.emilym.sinatra.router

import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.router.data.NetworkGraph
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
        const val STOP_ID_MANNING_CLARK_ALG = "8104"
        const val STOP_ID_MANNING_CLARK = "MCK"
        const val STOP_ID_SANDFORD_STREET_ALG = "8112"
        const val STOP_ID_CANBERRA_RAILWAY_STATION = "3320"
    }

    lateinit var graph: NetworkGraph
    lateinit var raptor: Raptor
    lateinit var config: RaptorConfig

    @BeforeTest
    fun setup() {
        graph = NetworkGraph.byteFormatForByteArray(this::class.java.classLoader.getResource("network_graph.eng").readBytes())
        config = RaptorConfig(
            maximumWalkingTime = 10 * 60L,
            transferTime = 0,
            transferPenalty = 0,
            changeOverTime = 0,
            changeOverPenalty = 0
        )
        raptor = Raptor(graph, List(3) { graph.mappings.serviceIds }, config)
    }

    @Test
    fun byteGraph() {
        println(graph.mappings.stopIds.take(5))
        println(graph.mappings.routeIds.take(5))
        println(graph.mappings.headings.take(5))
        println(graph.mappings.serviceIds)
        println(graph.metadata)
//        println(graph.node(0))
//        println(graph.node(1))
//        println(graph.node(graph.mappings.stopIds.size))
    }

    @Test
    fun testValidJourneyOnSingleRoute() {
        val result = raptor.calculate(
            Duration.parseIsoString("PT09H").inWholeSeconds,
            STOP_ID_SWINDEN_STREET_GGN,
            STOP_ID_MANNING_CLARK_GGN
        )
        assertEquals(RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                listOf("8119", "8117", "8115", "8113", "8111", "8109", "8107", "8105"),
                "ACTO001",
                "Gungahlin Pl",
                startTime=32476,
                endTime=33307,
                travelTime=831,
                dayIndex = 0,
            )
        )), result)
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
        assertEquals(RaptorJourney(listOf(
            RaptorJourneyConnection.Transfer(
                listOf("SWN", "8119"),
                travelTime = 16
            ),
            RaptorJourneyConnection.Travel(
                listOf("8119", "8117", "8115", "8113", "8111", "8109", "8107", "8105"),
                "ACTO001",
                "Gungahlin Pl",
                startTime=32476,
                endTime=33307,
                travelTime=831,
                dayIndex = 0,
            ),
            RaptorJourneyConnection.Transfer(
                listOf("8105", "MCK"),
                travelTime = 9
            ),
        )), result)
    }

    @Test
    fun testValidJourneyAcrossMultipleRoutes() {
        val result = raptor.calculate(
            Duration.parseIsoString("PT09H").inWholeSeconds,
            STOP_ID_CANBERRA_RAILWAY_STATION,
            STOP_ID_MANNING_CLARK
        )
        assertEquals(RaptorJourney(listOf(
            RaptorJourneyConnection.Transfer(
                listOf("3320", "3321"),
                travelTime = 342
            ),
            RaptorJourneyConnection.Travel(
                listOf("3321", "2235", "2373", "2376", "2258", "3261", "3259", "8889"),
                routeId = "2-10647",
                heading = "Fraser",
                startTime = 32700,
                endTime = 33420,
                travelTime = 720,
                dayIndex = 0,
            ),
            RaptorJourneyConnection.Travel(
                listOf("8889", "3356", "3406"),
                routeId = "5-10647",
                heading = "City ANU",
                startTime = 33360,
                endTime = 33540,
                travelTime = 180,
                dayIndex = 0,
            ),
            RaptorJourneyConnection.Transfer(
                listOf("3406", "8129"),
                travelTime = 109
            ),
            RaptorJourneyConnection.Travel(
                listOf("8129", "8127", "8125", "8123", "8121", "8119", "8117", "8115", "8113", "8111", "8109", "8107", "8105"),
                routeId = "ACTO001",
                heading = "Gungahlin Pl",
                startTime=33600,
                endTime=34957,
                travelTime=1357,
                dayIndex = 0,
            ),
            RaptorJourneyConnection.Transfer(
                listOf("8105", "MCK"),
                travelTime = 9
            )
        )), result)
    }

    @Test
    fun testValidJourneyWithFewerServices() {
        val raptor = Raptor(graph, List(3) { listOf("2023-COMBVAC-Weekday-05", "WD") }, config)
        val result = raptor.calculate(
            Duration.parseIsoString("PT09H").inWholeSeconds,
            STOP_ID_SWINDEN_STREET,
            STOP_ID_MANNING_CLARK
        )
        assertEquals(RaptorJourney(listOf(
            RaptorJourneyConnection.Transfer(
                listOf("SWN", "8119"),
                travelTime = 16
            ),
            RaptorJourneyConnection.Travel(
                listOf("8119", "8117", "8115", "8113", "8111", "8109", "8107", "8105"),
                "ACTO001",
                "Gungahlin Pl",
                startTime=32626,
                endTime=33457,
                travelTime=831,
                dayIndex = 0,
            ),
            RaptorJourneyConnection.Transfer(
                listOf("8105", "MCK"),
                travelTime = 9
            ),
        )), result)
    }

    @Test
    fun testInvalidJourney() {
        assertFails {
            val raptor = Raptor(graph, List(3) { graph.mappings.serviceIds }, config = RaptorConfig(
                maximumWalkingTime = 0 * 60L,
                transferTime = 0,
                transferPenalty = 0,
                changeOverTime = 0,
                changeOverPenalty = 0
            ))
            raptor.calculate(
                Duration.parseIsoString("PT25H").inWholeSeconds,
                STOP_ID_CANBERRA_RAILWAY_STATION,
                STOP_ID_MANNING_CLARK
            )
        }
    }

    @Test
    fun testNegativeValueOutcomeJourney() {
        val raptor = Raptor(
            graph,
            listOf(
                listOf("SA"),
                listOf("SU"),
                listOf("WD")
            ),
            RaptorConfig(
                maximumWalkingTime = 25 * 60L,
                transferTime = 10 * 60L,
                transferPenalty = 10 * 60 * 1000,
                changeOverTime = 15 * 60L,
                changeOverPenalty = 15 * 60 * 1000,
            )
        )
        val result = raptor.calculate(
            Duration.parseIsoString("PT23H55M").inWholeSeconds,
            STOP_ID_MANNING_CLARK_ALG,
            STOP_ID_SANDFORD_STREET_ALG,
        )
        assertEquals(RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                listOf("8104", "8106", "8108", "8110", "8112"),
                "X1",
                "Sandford St",
                startTime=86524,
                endTime=86947,
                travelTime=423,
                dayIndex = 0,
            ),
        )), result)
    }

}