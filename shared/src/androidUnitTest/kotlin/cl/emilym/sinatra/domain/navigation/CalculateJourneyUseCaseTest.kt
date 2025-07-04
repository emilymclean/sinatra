package cl.emilym.sinatra.domain.navigation

import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.JourneySearchConfig
import cl.emilym.sinatra.data.models.JourneySearchOption
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopAccessibility
import cl.emilym.sinatra.data.models.StopVisibility
import cl.emilym.sinatra.data.models.StopWheelchairAccessibility
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.repository.NetworkGraphRepository
import cl.emilym.sinatra.data.repository.RoutingPreferencesRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.domain.ActiveServicesUseCase
import cl.emilym.sinatra.router.DepartureBasedRouter
import cl.emilym.sinatra.router.RaptorJourney
import cl.emilym.sinatra.router.RaptorJourneyConnection
import cl.emilym.sinatra.router.RaptorStop
import cl.emilym.sinatra.router.data.NetworkGraph
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class CalculateJourneyUseCaseTest {

    private val networkGraphRepository = mockk<NetworkGraphRepository>()
    private val activeServicesUseCase = mockk<ActiveServicesUseCase>()
    private val routerFactory = mockk<RouterFactory>()
    private val stopRepository = mockk<StopRepository>()
    private val clock = mockk<Clock>()
    private val transportMetadataRepository = mockk<TransportMetadataRepository>()
    private val routingPreferencesRepository = mockk<RoutingPreferencesRepository>()
    private val config = mockk<JourneySearchConfig>()
    private val graph = mockk<NetworkGraph>()
    private val reconstructJourneyUseCase = mockk<ReconstructJourneyUseCase>()
    private val directWalkingJourneyUseCase = mockk<DirectWalkingJourneyUseCase>()

    private val useCase = CalculateJourneyUseCase(
        networkGraphRepository,
        activeServicesUseCase,
        stopRepository,
        clock,
        transportMetadataRepository,
        routingPreferencesRepository,
        routerFactory,
        reconstructJourneyUseCase,
        directWalkingJourneyUseCase
    )

    private val now = Instant.parse("2025-04-28T10:00:00Z")
    private val location = MapLocation(0.0, 0.0)

    @BeforeTest
    fun setup() {
        every { config.maximumComputationTime } returns 12.hours
        every { config.options } returns listOf(JourneySearchOption(1.seconds, 1.seconds, 1, 1.seconds, 1))
        coEvery { graph.metadata.assumedWalkingSecondsPerKilometer } returns 25U * 60U
        every { clock.now() } returns Instant.fromEpochMilliseconds(1745809143)
        coEvery { directWalkingJourneyUseCase.invoke(any(), any(), any(), any()) } returns null
    }

    @Test
    fun `invoke returns journeys when successful`() = runTest {
        val stop = Stop("stop1", null, "Stop 1", "Stop 1", location, StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false)
        val stops = listOf(stop)

        coEvery { stopRepository.stops() } returns Cachable.live(stops)
        coEvery { activeServicesUseCase(any()) } returns Cachable.live(listOf(Service("service1", emptyList(), emptyList())))
        coEvery { routingPreferencesRepository.maximumWalkingTime() } returns 10.minutes
        coEvery { transportMetadataRepository.timeZone() } returns TimeZone.UTC

        coEvery { networkGraphRepository.config() } returns Cachable.live(config)
        coEvery { networkGraphRepository.networkGraph(any()) } returns Cachable.live(graph)

        val router = mockk<DepartureBasedRouter>()
        coEvery { router.calculate(any(), any<List<RaptorStop>>(), any<List<RaptorStop>>()) } returns RaptorJourney(listOf(
            RaptorJourneyConnection.Travel(
                listOf("8119", "8117", "8115", "8113", "8111", "8109", "8107", "8105"),
                "ACTO001",
                "Gungahlin Pl",
                startTime=32476,
                endTime=33307,
                travelTime=831,
                dayIndex = 0,
            )
        ))
        every { routerFactory(any(), any(), any(), any(), any()) } returns router
        coEvery { reconstructJourneyUseCase(any(), any(), any(), any(), any()) } returns Journey(listOf(
            JourneyLeg.TransferPoint(1.seconds, Time.create(0.seconds, now), Time.create(1.seconds, now)
        )))

        val result = useCase(
            JourneyLocation(location, exact = true),
            JourneyLocation(location, exact = true),
            JourneyCalculationTime.DepartureTime(now)
        )

        assertEquals(1, result.size)
    }

    @Test
    fun `invoke throws RouterException when no journeys found`() = runTest {
        val stop = Stop("stop1", null, "Stop 1", "Stop 1", location, StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false)
        val stops = listOf(stop)

        coEvery { stopRepository.stops() } returns Cachable.live(stops)
        coEvery { activeServicesUseCase(any()) } returns Cachable.live(listOf(Service("service1", emptyList(), emptyList())))
        coEvery { routingPreferencesRepository.maximumWalkingTime() } returns 10.minutes
        coEvery { transportMetadataRepository.timeZone() } returns TimeZone.UTC

        coEvery { networkGraphRepository.config() } returns Cachable.live(config)

        coEvery { networkGraphRepository.networkGraph(any()) } returns Cachable.live(graph)

        val router = mockk<DepartureBasedRouter>()
        coEvery { router.calculate(any(), any<List<RaptorStop>>(), any<List<RaptorStop>>()) } throws RouterException.noJourneyFound()
        every { routerFactory(any(), any(), any(), any(), any()) } returns router
        coEvery { reconstructJourneyUseCase(any(), any(), any(), any(), any()) } returns Journey(listOf())

        assertFailsWith<RouterException> {
            useCase(
                JourneyLocation(location, exact = true),
                JourneyLocation(location, exact = true),
                JourneyCalculationTime.DepartureTime(now)
            )
        }
    }

    @Test
    fun `nearbyStops returns correct stop when exact`() = runTest {
        val stop1 = Stop("stop1", null, "Stop 1", "Stop 1", location, StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false)
        val stop2 = Stop("stop2", null, "Stop 2", "Stop 2", MapLocation(1.0, 1.0), StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false)
        val stops = listOf(stop1, stop2)

        coEvery { networkGraphRepository.networkGraph(any()) } returns Cachable.live(graph)

        val journeyLocation = JourneyLocation(MapLocation(0.0, 0.0), exact = true)
        val result = with(useCase) {
            withContext(kotlinx.coroutines.Dispatchers.Default) {
                stops.nearbyStops(journeyLocation, 10.minutes)
            }
        }

        assertEquals(1, result.size)
        assertEquals(stop1.id, result.first().id)
    }

}
