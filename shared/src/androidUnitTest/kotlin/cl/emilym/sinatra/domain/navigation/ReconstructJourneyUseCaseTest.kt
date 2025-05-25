package cl.emilym.sinatra.domain.navigation

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteType
import cl.emilym.sinatra.data.models.RouteVisibility
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopAccessibility
import cl.emilym.sinatra.data.models.StopVisibility
import cl.emilym.sinatra.data.models.StopWheelchairAccessibility
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.router.RaptorJourney
import cl.emilym.sinatra.router.RaptorJourneyConnection
import cl.emilym.sinatra.router.data.NetworkGraph
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReconstructJourneyUseCaseTest {

    private val stopRepository = mockk<StopRepository>()
    private val routeRepository = mockk<RouteRepository>()
    private val transportMetadataRepository = mockk<TransportMetadataRepository>()
    private val graph = mockk<NetworkGraph>()

    private val useCase = ReconstructJourneyUseCase(
        routeRepository, transportMetadataRepository, stopRepository
    )

    private val now = Instant.parse("2025-04-28T10:00:00Z")
    private val location = MapLocation(0.0, 0.0)

    @BeforeTest
    fun setup() {
        coEvery { transportMetadataRepository.timeZone() } returns TimeZone.UTC
    }

    @Test
    fun `toJourney handles exact journey starting with a transfer and dayIndex increase`() = runTest {
        val startStop = Stop("stop1", null, "Stop 1", "Stop 1", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val endStop = Stop("stop2", null, "Stop 2", "Stop 2", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val stopsList = listOf(startStop, endStop)

        val raptorJourney = RaptorJourney(listOf(
            RaptorJourneyConnection.Transfer(
                stops = listOf(startStop.id),
                travelTime = 120,
            ),
            RaptorJourneyConnection.Travel(
                stops = listOf(startStop.id, endStop.id),
                routeId = "route1",
                heading = "North",
                startTime = 3600 * 3,
                endTime = 7200 * 4,
                travelTime = 3600,
                dayIndex = 1
            )
        ))

        coEvery { stopRepository.stops() } returns Cachable.live(stopsList)
        coEvery { routeRepository.routes(any()) } returns Cachable.live(listOf(
            Route("route1", "R1", "R1", null, "R1", null, false, false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)
        ))
        coEvery { graph.metadata.assumedWalkingSecondsPerKilometer } returns 25U * 60U

        val journey = useCase(
            raptorJourney,
            JourneyLocation(location, exact = true),
            JourneyLocation(location, exact = true),
            JourneyCalculationTime.DepartureTime(now),
            graph
        )

        assertEquals(2, journey.legs.size)
        assertIs<JourneyLeg.Transfer>(journey.legs.first())
        assertIs<JourneyLeg.Travel>(journey.legs.last())
        assertEquals(
            journey.legs.last().departureTime.instant.toLocalDateTime(TimeZone.UTC).dayOfMonth,
            journey.legs.first().departureTime.instant.toLocalDateTime(TimeZone.UTC).dayOfMonth,
        )
        assertEquals(
            journey.legs.last().arrivalTime.instant.toLocalDateTime(TimeZone.UTC).dayOfMonth,
            journey.legs.first().arrivalTime.instant.toLocalDateTime(TimeZone.UTC).dayOfMonth,
        )
    }

    @Test
    fun `toJourney handles exact journey ending with a transfer`() = runTest {
        val startStop = Stop("stop1", null, "Stop 1", "Stop 1", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val endStop = Stop("stop2", null, "Stop 2", "Stop 2", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val stopsList = listOf(startStop, endStop)

        val raptorJourney = RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                stops = listOf(startStop.id, endStop.id),
                routeId = "route1",
                heading = "North",
                startTime = 0,
                endTime = 3600,
                travelTime = 3600,
                dayIndex = 1
            ),
            RaptorJourneyConnection.Transfer(
                stops = listOf(endStop.id),
                travelTime = 300,
            )
        ))

        coEvery { stopRepository.stops() } returns Cachable.live(stopsList)
        coEvery { routeRepository.routes(any()) } returns Cachable.live(listOf(
            Route("route1", "R1", "R1", null, "R1", null, false, false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)
        ))
        coEvery { graph.metadata.assumedWalkingSecondsPerKilometer } returns 25U * 60U

        val journey = useCase(
            raptorJourney,
            JourneyLocation(location, exact = true),
            JourneyLocation(location, exact = true),
            JourneyCalculationTime.DepartureTime(now),
            graph
        )

        assertEquals(2, journey.legs.size)
        assertIs<JourneyLeg.Travel>(journey.legs.first())
        assertIs<JourneyLeg.Transfer>(journey.legs.last())
        assertEquals(
            journey.legs.first().departureTime.instant.toLocalDateTime(TimeZone.UTC).dayOfMonth,
            journey.legs.last().departureTime.instant.toLocalDateTime(TimeZone.UTC).dayOfMonth,
        )
        assertEquals(
            journey.legs.first().arrivalTime.instant.toLocalDateTime(TimeZone.UTC).dayOfMonth,
            journey.legs.last().arrivalTime.instant.toLocalDateTime(TimeZone.UTC).dayOfMonth,
        )
    }

    @Test
    fun `toJourney handles mid-journey dayIndex increase`() = runTest {
        val stop1 = Stop("stop1", null, "Stop 1", "Stop 1", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val stop2 = Stop("stop2", null, "Stop 2", "Stop 2", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val stop3 = Stop("stop3", null, "Stop 3", "Stop 3", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val stop4 = Stop("stop4", null, "Stop 4", "Stop 4", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val stop5 = Stop("stop5", null, "Stop 5", "Stop 5", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val stopsList = listOf(stop1, stop2, stop3, stop4, stop5)

        val raptorJourney = RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                stops = listOf(stop1.id, stop2.id),
                routeId = "route1",
                heading = "East",
                startTime = 100,
                endTime = 200,
                travelTime = 100,
                dayIndex = 0
            ),
            RaptorJourneyConnection.Travel(
                stops = listOf(stop2.id, stop3.id),
                routeId = "route2",
                heading = "East",
                startTime = 100,
                endTime = 200,
                travelTime = 100,
                dayIndex = 1
            ),
            RaptorJourneyConnection.Transfer(
                stops = listOf(stop3.id, stop4.id),
                travelTime = 300,
            ),
            RaptorJourneyConnection.Travel(
                stops = listOf(stop4.id, stop5.id),
                routeId = "route2",
                heading = "East",
                startTime = 500,
                endTime = 600,
                travelTime = 100,
                dayIndex = 1
            ),
        ))

        coEvery { stopRepository.stops() } returns Cachable.live(stopsList)
        coEvery { routeRepository.routes(any()) } returns Cachable.live(listOf(
            Route("route1", "R1", "R1", null, "R1", null, false, false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null),
            Route("route2", "R2", "R2", null, "R2", null, false, false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)
        ))
        coEvery { graph.metadata.assumedWalkingSecondsPerKilometer } returns 25U * 60U

        val journey = useCase(
            raptorJourney,
            JourneyLocation(location, exact = true),
            JourneyLocation(location, exact = true),
            JourneyCalculationTime.DepartureTime(now),
            graph
        )

        assertEquals(4, journey.legs.size)
        assertEquals(
            28,
            journey.legs[0].departureTime.instant.toLocalDateTime(TimeZone.UTC).dayOfMonth,
        )
        assertEquals(
            29,
            journey.legs[1].departureTime.instant.toLocalDateTime(TimeZone.UTC).dayOfMonth,
        )
        assertEquals(
            29,
            journey.legs[2].departureTime.instant.toLocalDateTime(TimeZone.UTC).dayOfMonth,
        )
        assertEquals(
            29,
            journey.legs[3].departureTime.instant.toLocalDateTime(TimeZone.UTC).dayOfMonth,
        )
    }

    @Test
    fun `toJourney adds transfer points when non-exact departure and arrival`() = runTest {
        val startStop = Stop("stop1", null, "Stop 1", "Stop 1", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val endStop = Stop("stop2", null, "Stop 2", "Stop 2", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val stopsList = listOf(startStop, endStop)

        val raptorJourney = RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                stops = listOf(startStop.id, endStop.id),
                routeId = "route1",
                heading = "East",
                startTime = 100,
                endTime = 200,
                travelTime = 100,
                dayIndex = 0
            )
        ))

        coEvery { stopRepository.stops() } returns Cachable.live(stopsList)
        coEvery { routeRepository.routes(any()) } returns Cachable.live(listOf(
            Route("route1", "R1", "R1", null, "R1", null, false, false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)
        ))
        coEvery { graph.metadata.assumedWalkingSecondsPerKilometer } returns 25U * 60U

        val journey = useCase(
            raptorJourney,
            JourneyLocation(location, exact = false),
            JourneyLocation(location, exact = false),
            JourneyCalculationTime.DepartureTime(now),
            graph
        )

        assertTrue(journey.legs.first() is JourneyLeg.TransferPoint)
        assertTrue(journey.legs.last() is JourneyLeg.TransferPoint)
        assertTrue(journey.legs[1] is JourneyLeg.Travel)
    }

    @Test
    fun `toJourney handles simple case`() = runTest {
        val startStop = Stop("stop1", null, "Stop 1", "Stop 1", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val endStop = Stop("stop2", null, "Stop 2", "Stop 2", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val stopsList = listOf(startStop, endStop)

        val raptorJourney = RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                stops = listOf(startStop.id, endStop.id),
                routeId = "route1",
                heading = "East",
                startTime = 100,
                endTime = 200,
                travelTime = 100,
                dayIndex = 0
            )
        ))

        coEvery { stopRepository.stops() } returns Cachable.live(stopsList)
        coEvery { routeRepository.routes(any()) } returns Cachable.live(listOf(
            Route("route1", "R1", "R1", null, "R1", null, false, false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)
        ))
        coEvery { graph.metadata.assumedWalkingSecondsPerKilometer } returns 25U * 60U

        val journey = useCase(
            raptorJourney,
            JourneyLocation(location, exact = true),
            JourneyLocation(location, exact = true),
            JourneyCalculationTime.DepartureTime(now),
            graph
        )

        assertEquals(1, journey.legs.size)
        assertTrue(journey.legs.first() is JourneyLeg.Travel)
    }

    @Test
    fun `toJourney correctly translates accessibility information`() = runTest {
        val startStop = Stop("stop1", null, "Stop 1", "Stop 1", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val endStop = Stop("stop2", null, "Stop 2", "Stop 2", location, StopAccessibility(
            StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        )
        val stopsList = listOf(startStop, endStop)

        val raptorJourney = RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                stops = listOf(startStop.id, endStop.id),
                routeId = "route1",
                heading = "East",
                startTime = 100,
                endTime = 200,
                travelTime = 100,
                dayIndex = 0,
                bikesAllowed = true,
                wheelchairAccessible = true
            ),
            RaptorJourneyConnection.Travel(
                stops = listOf(startStop.id, endStop.id),
                routeId = "route1",
                heading = "East",
                startTime = 100,
                endTime = 200,
                travelTime = 100,
                dayIndex = 0,
                bikesAllowed = false,
                wheelchairAccessible = true
            ),
            RaptorJourneyConnection.Travel(
                stops = listOf(startStop.id, endStop.id),
                routeId = "route1",
                heading = "East",
                startTime = 100,
                endTime = 200,
                travelTime = 100,
                dayIndex = 0,
                bikesAllowed = true,
                wheelchairAccessible = false
            )
        ))

        coEvery { stopRepository.stops() } returns Cachable.live(stopsList)
        coEvery { routeRepository.routes(any()) } returns Cachable.live(listOf(
            Route("route1", "R1", "R1", null, "R1", null, false, false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)
        ))
        coEvery { graph.metadata.assumedWalkingSecondsPerKilometer } returns 25U * 60U

        val journey = useCase(
            raptorJourney,
            JourneyLocation(location, exact = true),
            JourneyLocation(location, exact = true),
            JourneyCalculationTime.DepartureTime(now),
            graph
        )

        assertEquals(ServiceWheelchairAccessible.ACCESSIBLE, (journey.legs[0] as JourneyLeg.Travel).routeAccessibility?.wheelchairAccessible)
        assertEquals(ServiceBikesAllowed.ALLOWED, (journey.legs[0] as JourneyLeg.Travel).routeAccessibility?.bikesAllowed)
        assertEquals(ServiceWheelchairAccessible.ACCESSIBLE, (journey.legs[1] as JourneyLeg.Travel).routeAccessibility?.wheelchairAccessible)
        assertEquals(ServiceBikesAllowed.DISALLOWED, (journey.legs[1] as JourneyLeg.Travel).routeAccessibility?.bikesAllowed)
        assertEquals(ServiceWheelchairAccessible.INACCESSIBLE, (journey.legs[2] as JourneyLeg.Travel).routeAccessibility?.wheelchairAccessible)
        assertEquals(ServiceBikesAllowed.ALLOWED, (journey.legs[2] as JourneyLeg.Travel).routeAccessibility?.bikesAllowed)
    }
}