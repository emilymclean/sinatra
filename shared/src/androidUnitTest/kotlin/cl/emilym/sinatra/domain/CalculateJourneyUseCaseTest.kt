package cl.emilym.sinatra.domain

import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.*
import cl.emilym.sinatra.data.repository.*
import cl.emilym.sinatra.domain.navigation.CalculateJourneyUseCase
import cl.emilym.sinatra.domain.navigation.JourneyCalculationTime
import cl.emilym.sinatra.domain.navigation.JourneyLocation
import cl.emilym.sinatra.domain.navigation.ReconstructJourneyUseCase
import cl.emilym.sinatra.domain.navigation.RouterFactory
import cl.emilym.sinatra.router.*
import cl.emilym.sinatra.router.data.NetworkGraph
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import kotlin.test.*
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

    private val useCase = CalculateJourneyUseCase(
        networkGraphRepository,
        activeServicesUseCase,
        stopRepository,
        clock,
        transportMetadataRepository,
        routingPreferencesRepository,
        routerFactory,
        reconstructJourneyUseCase
    )

    private val now = Instant.parse("2025-04-28T10:00:00Z")
    private val location = MapLocation(0.0, 0.0)

    @BeforeTest
    fun setup() {
        every { config.maximumComputationTime } returns 12.hours
        every { config.options } returns listOf(JourneySearchOption(1.seconds, 1.seconds, 1, 1.seconds, 1))
        coEvery { graph.metadata.assumedWalkingSecondsPerKilometer } returns 25U * 60U
        every { clock.now() } returns Instant.fromEpochMilliseconds(1745809143)
    }

    @Test
    fun `invoke returns journeys when successful`() = runTest {
        val stop = Stop("stop1", null, "Stop 1", "Stop 1", location, StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null))
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
        coEvery { reconstructJourneyUseCase(any(), any(), any(), any(), any()) } returns Journey(listOf())

        val result = useCase(
            JourneyLocation(location, exact = true),
            JourneyLocation(location, exact = true),
            JourneyCalculationTime.DepartureTime(now)
        )

        assertEquals(1, result.size)
    }

    @Test
    fun `invoke throws RouterException when no journeys found`() = runTest {
        val stop = Stop("stop1", null, "Stop 1", "Stop 1", location, StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null))
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
        val stop1 = Stop("stop1", null, "Stop 1", "Stop 1", location, StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null))
        val stop2 = Stop("stop2", null, "Stop 2", "Stop 2", MapLocation(1.0, 1.0), StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null))
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
