package cl.emilym.sinatra.router

import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.router.data.NetworkGraph
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
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
        const val STOP_ID_GUNGAHLIN_GGN = "8100"
        const val STOP_ID_ALINGA_GGN = "8129"
        const val STOP_ID_AIKMAN_DR_BEFORE_TOWNSEND_PL = "4144"
        const val STOP_ID_HOLY_SPIRIT_PS_PLT_1 = "4830"
    }

    lateinit var graph: NetworkGraph
    lateinit var raptor: Router
    lateinit var graphReverse: NetworkGraph
    lateinit var raptorReverse: Router
    lateinit var config: RaptorConfig
    lateinit var altConfig: RaptorConfig

    @BeforeTest
    fun setup() {
        config = RaptorConfig(
            maximumWalkingTime = 10 * 60L,
            transferTime = 0,
            transferPenalty = 0,
            changeOverTime = 0,
            changeOverPenalty = 0
        )
        altConfig = RaptorConfig(
            maximumWalkingTime = 10 * 60L,
            transferTime = 5 * 60L,
            transferPenalty = 50 * 60 * 100,
            changeOverTime = 5 * 60L,
            changeOverPenalty = 50 * 60 * 100
        )
        graph = NetworkGraph.byteFormatForByteArray(
            this::class.java.classLoader.getResource("network_graph.eng").readBytes()
        )
        raptor = Raptor(graph, List(3) { graph.mappings.serviceIds }, config)
        graphReverse = NetworkGraph.byteFormatForByteArray(
            this::class.java.classLoader.getResource("network-graph-reverse.eng").readBytes()
        )
        raptorReverse = ArrivalBasedRouter(graphReverse, List(3) { graphReverse.mappings.serviceIds }, config)
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
    fun testValidJourneyOnSingleRoute() = runTest {
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
                bikesAllowed = true,
                wheelchairAccessible = true
            )
        )), result)
    }

    @Test
    fun testValidJourneyOnSingleRouteEndToEnd() = runTest {
        val result = raptor.calculate(
            Duration.parseIsoString("PT09H").inWholeSeconds,
            STOP_ID_GUNGAHLIN_GGN,
            STOP_ID_ALINGA_GGN
        )
        assertEquals(RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                listOf("8100", "8104", "8106", "8108", "8110", "8112", "8114", "8116", "8118", "8120", "8122", "8124", "8126", "8129"),
                "ACTO001",
                "Alinga St",
                startTime=32400,
                endTime=33875,
                travelTime=1475,
                dayIndex = 0,
                bikesAllowed = true,
                wheelchairAccessible = true
            )
        )), result)
    }

    @Test
    fun testValidJourneyOnSingleRouteReverse() = runTest {
        val result = raptorReverse
            .calculate(
                Duration.parseIsoString("PT09H").inWholeSeconds,
                STOP_ID_SWINDEN_STREET_GGN,
                STOP_ID_MANNING_CLARK_GGN
            )
        assertEquals(RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                listOf("8119", "8117", "8115", "8113", "8111", "8109", "8107", "8105"),
                "ACTO001",
                "Gungahlin Pl",
                startTime=31556,
                endTime=32427,
                travelTime=871,
                dayIndex = 0,
                bikesAllowed = true,
                wheelchairAccessible = true
            )
        )), result)
    }

    @Test
    fun testValidJourneyOnSingleRouteEndToEndReverse() = runTest {
        val result = raptorReverse.calculate(
            33875,
            STOP_ID_GUNGAHLIN_GGN,
            STOP_ID_ALINGA_GGN
        )
        assertEquals(RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                listOf("8100", "8104", "8106", "8108", "8110", "8112", "8114", "8116", "8118", "8120", "8122", "8124", "8126", "8129"),
                "ACTO001",
                "Alinga St",
                startTime=32400,
                endTime=33875,
                travelTime=1475,
                dayIndex = 0,
                bikesAllowed = true,
                wheelchairAccessible = true
            )
        )), result)
    }

    @Test
    fun testValidJourneyOnSingleRouteWithTransfer() = runTest {
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
                bikesAllowed = true,
                wheelchairAccessible = true
            ),
            RaptorJourneyConnection.Transfer(
                listOf("8105", "MCK"),
                travelTime = 9
            ),
        )), result)
    }

    @Test
    fun testValidJourneyOnSingleRouteWithTransferReverse() = runTest {
        val result = try {
            raptorReverse.calculate(
                Duration.parseIsoString("PT10H").inWholeSeconds,
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
                startTime=35156,
                endTime=36027,
                travelTime=871,
                dayIndex = 0,
                bikesAllowed = true,
                wheelchairAccessible = true
            ),
            RaptorJourneyConnection.Transfer(
                listOf("8105", "MCK"),
                travelTime = 9
            ),
        )), result)
    }

    @Test
    fun testValidJourneyOnSingleRouteBus() = runTest {
        val result = DepartureBasedRouter(
            graph,
            List(3) { graph.mappings.serviceIds },
            altConfig
        ).calculate(
            Duration.parseIsoString("PT14H").inWholeSeconds,
            STOP_ID_AIKMAN_DR_BEFORE_TOWNSEND_PL,
            STOP_ID_HOLY_SPIRIT_PS_PLT_1
        )
        assertEquals(RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                listOf("4144", "4938", "4182", "4180", "4128", "4126", "4124", "4122", "4120", "4106", "4793", "600", "4674", "4676", "4678", "4680", "5137", "5062", "5064", "5066", "5068", "5070", "5175", "5078", "5074", "5073", "2553", "4830"),
                "24-10647",
                "Gungahlin",
                startTime=50760,
                endTime=52320,
                travelTime=1560,
                dayIndex = 0,
                bikesAllowed = true,
                wheelchairAccessible = true
            )
        )), result)
    }

    @Test
    fun testValidJourneyOnSingleRouteBusReverse() = runTest {
        val result = ArrivalBasedRouter(
            graphReverse,
            List(3) { graph.mappings.serviceIds },
            altConfig
        ).calculate(
            Duration.parseIsoString("PT14H").inWholeSeconds,
            STOP_ID_AIKMAN_DR_BEFORE_TOWNSEND_PL,
            STOP_ID_HOLY_SPIRIT_PS_PLT_1
        )
        assertEquals(RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                listOf("4144", "4938", "4182", "4180", "4128", "4126", "4124", "4122", "4120", "4106", "4793", "600", "4674", "4676", "4678", "4680", "5137", "5062", "5064", "5066", "5068", "5070", "5175", "5078", "5074", "5073", "2553", "4830"),
                "24-10647",
                "Gungahlin",
                startTime=48180,
                endTime=49860,
                travelTime=1680,
                dayIndex = 0,
            )
        )), result)
    }

    @Test
    fun testValidJourneyAcrossMultipleRoutes() = runTest {
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
                bikesAllowed = false,
                wheelchairAccessible = true
            ),
            RaptorJourneyConnection.Travel(
                listOf("8889", "3356", "3406"),
                routeId = "5-10647",
                heading = "City ANU",
                startTime = 33360,
                endTime = 33540,
                travelTime = 180,
                dayIndex = 0,
                bikesAllowed = true,
                wheelchairAccessible = true
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
                bikesAllowed = true,
                wheelchairAccessible = true
            ),
            RaptorJourneyConnection.Transfer(
                listOf("8105", "MCK"),
                travelTime = 9
            )
        )), result)
    }

    @Test
    fun testValidJourneyAcrossMultipleRoutesReverse() = runTest {
        val result = raptorReverse.calculate(
            Duration.parseIsoString("PT10H").inWholeSeconds,
            STOP_ID_CANBERRA_RAILWAY_STATION,
            STOP_ID_MANNING_CLARK
        )
        assertEquals(RaptorJourney(listOf(
            RaptorJourneyConnection.Transfer(
                listOf("3320", "3321"),
                travelTime = 342
            ),
            RaptorJourneyConnection.Travel(
                listOf("3321", "2235", "2373"),
                routeId = "2-10647",
                heading = "Fraser",
                startTime = 33180,
                endTime = 33300,
                travelTime = 120,
                dayIndex = 0,
                bikesAllowed = true,
                wheelchairAccessible = true
            ),
            RaptorJourneyConnection.Travel(
                listOf("2373", "2376", "2258", "3261", "3259", "8889"),
                routeId = "6-10647",
                heading = "City ANU",
                startTime = 33600,
                endTime = 34140,
                travelTime = 540,
                dayIndex = 0,
                bikesAllowed = false,
                wheelchairAccessible = false
            ),
            RaptorJourneyConnection.Travel(
                listOf("8889", "3356", "3406"),
                routeId = "7-10647",
                heading = "City ANU",
                startTime = 34200,
                endTime = 34380,
                travelTime = 180,
                dayIndex = 0,
                bikesAllowed = true,
                wheelchairAccessible = true
            ),
            RaptorJourneyConnection.Transfer(
                listOf("3406", "8129"),
                travelTime = 109
            ),
            RaptorJourneyConnection.Travel(
                listOf("8129", "8127", "8125", "8123", "8121", "8119", "8117", "8115", "8113", "8111", "8109", "8107", "8105"),
                routeId = "ACTO001",
                heading = "Gungahlin Pl",
                startTime=34650,
                endTime=36027,
                travelTime=1377,
                dayIndex = 0,
                bikesAllowed = true,
                wheelchairAccessible = true
            ),
            RaptorJourneyConnection.Transfer(
                listOf("8105", "MCK"),
                travelTime = 9
            )
        )), result)
    }

    @Test
    fun testValidJourneyWithFewerServices() = runTest {
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
                bikesAllowed = true,
                wheelchairAccessible = true
            ),
            RaptorJourneyConnection.Transfer(
                listOf("8105", "MCK"),
                travelTime = 9
            ),
        )), result)
    }

    @Test
    fun testInvalidJourney() = runTest {
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
    fun testNegativeValueOutcomeJourney() = runTest {
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
                dayIndex=0,
                bikesAllowed = true,
                wheelchairAccessible = true
            ),
        )), result)
    }

    @Test
    fun testNegativeValueOutcomeJourney2() = runTest {
        val result = Raptor(
            graph,
            listOf(
                listOf("WD", "2023-COMBNXT-Weekday-10"),
                listOf("SA", "2023-COMBNXT-Saturday-05"),
                listOf("SU", "2023-COMBNXT-Sunday-04"),
            ),
            RaptorConfig(
                maximumWalkingTime = 25 * 60L,
                transferTime = 5 * 60L,
                transferPenalty = 5 * 60 * 100,
                changeOverTime = 5 * 60L,
                changeOverPenalty = 5 * 60 * 100
            )
        ).calculate(
            Duration.parseIsoString("PT21H").inWholeSeconds,
            "8104",
            "1825"
        )

        println(result)

        assertIs<RaptorJourneyConnection.Travel>(result.connections.first())
        assertIs<RaptorJourneyConnection.Travel>(result.connections.last())

        val start = (result.connections.first() as RaptorJourneyConnection.Travel)
        val end = (result.connections.last() as RaptorJourneyConnection.Travel)
        assert(start.startTime + (start.dayIndex * 86400) < end.endTime + (end.dayIndex * 86400))
    }

    @Test
    fun `when multiple valid nearby stops, the soonest should be picked`() = runTest {
        val raptor = Raptor(graph, List(3) { listOf("2023-COMBVAC-Weekday-05", "WD") }, altConfig)
        // This route would have previously followed the whole loop, since R2 visits the belconnen
        // interchange twice in one journey, and the stop it visits the 2nd time just happens to
        // appear first in the stop list
        val result = raptor.calculate(
            Duration.parseIsoString("PT9H").inWholeSeconds,
            listOf(
                RaptorStop(id="4528", addedTime=0)
            ),
            listOf(
                RaptorStop(id="5511", addedTime=260),
                RaptorStop(id="5512", addedTime=202),
                RaptorStop(id="5513", addedTime=162),
                RaptorStop(id="5514", addedTime=138),
                RaptorStop(id="5515", addedTime=146),
                RaptorStop(id="5516", addedTime=187),
                RaptorStop(id="5517", addedTime=164),
                RaptorStop(id="5518", addedTime=188)
            )
        )
        assertEquals(RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                listOf("4528", "4803", "4910", "4006", "4972", "3442", "5520", "5514"),
                "2-10647",
                "Fraser",
                startTime=32400,
                endTime=33240,
                travelTime=840,
                dayIndex = 0,
                bikesAllowed = false,
                wheelchairAccessible = true
            ),
        )), result)
    }

}