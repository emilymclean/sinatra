package cl.emilym.sinatra.data.domain

import cl.emilym.sinatra.data.models.*
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.domain.CurrentTripForRouteUseCase
import cl.emilym.sinatra.domain.LiveTripInformationUseCase
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CurrentTripForRouteUseCaseTest {

    private lateinit var routeRepository: RouteRepository
    private lateinit var serviceRepository: ServiceRepository
    private lateinit var transportMetadataRepository: TransportMetadataRepository
    private lateinit var liveTripInformationUseCase: LiveTripInformationUseCase
    private lateinit var clock: Clock
    private lateinit var useCase: CurrentTripForRouteUseCase

    val route = Route(
        "route1",
        "r1",
        "R1",
        null,
        "Route 1",
        null,
        RouteType.LIGHT_RAIL,
        null
    )
    val tripInformation = RouteTripInformation(
        Time.parseIsoString("PT12H"),
        Time.parseIsoString("PT12H"),
        RouteServiceAccessibility(ServiceBikesAllowed.ALLOWED, ServiceWheelchairAccessible.ACCESSIBLE),
        "North",
        listOf()
    )

    @BeforeTest
    fun setup() {
        routeRepository = mockk()
        serviceRepository = mockk()
        transportMetadataRepository = mockk()
        liveTripInformationUseCase = mockk()
        clock = mockk()
        useCase = CurrentTripForRouteUseCase(
            routeRepository,
            serviceRepository,
            transportMetadataRepository,
            clock,
            liveTripInformationUseCase
        )
        mockkObject(Napier) // Mock Napier logging
    }

    @Test
    fun `invoke should return null if route is not found`() = runTest {
        val instant = Instant.parse("2024-01-05T12:00:00Z")
        every { clock.now() } returns instant

        coEvery { routeRepository.route(any()) } returns Cachable(null, CacheState.LIVE)

        val result = useCase.invoke("route1").first()

        assertEquals(null, result.item)
        coVerify { routeRepository.route("route1") }
        confirmVerified(routeRepository, serviceRepository, liveTripInformationUseCase)
    }

    @Test
    fun `invoke should return null if no active services are found`() = runTest {
        val instant = Instant.parse("2024-01-05T12:00:00Z")
        every { clock.now() } returns instant

        coEvery { routeRepository.route(any()) } returns Cachable(route, CacheState.LIVE)
        coEvery { routeRepository.servicesForRoute(any()) } returns Cachable(emptyList(), CacheState.LIVE)
        coEvery { serviceRepository.services(any()) } returns Cachable(listOf(), CacheState.LIVE)

        val result = useCase.invoke("route1").first()

        assertEquals(null, result.item)
        coVerify {
            routeRepository.route("route1")
            routeRepository.servicesForRoute("route1")
            serviceRepository.services(listOf())
        }
        confirmVerified(routeRepository, serviceRepository)
    }

    @Test
    fun `invoke should fallback to timetable if tripId is provided but no real-time URL`() = runTest {
        val instant = Instant.parse("2024-01-05T12:00:00Z")
        every { clock.now() } returns instant

        val serviceId = "service1"
        val tripId = "trip1"
        val service = Service(serviceId, emptyList(), emptyList())
        val tripTimetable = mockk<RouteTripTimetable>()

        every { tripTimetable.trip } returns tripInformation
        coEvery { routeRepository.route(any()) } returns Cachable(route, CacheState.LIVE)
        coEvery { serviceRepository.services(any()) } returns Cachable(listOf(service), CacheState.LIVE)
        coEvery {
            routeRepository.tripTimetable(any(), any(), any())
        } returns Cachable(tripTimetable, CacheState.LIVE)

        val result = useCase.invoke("route1", serviceId, tripId).first()

        assertEquals(route, result.item?.route)
        assertEquals(tripInformation, result.item?.tripInformation)
        coVerify {
            routeRepository.route("route1")
            serviceRepository.services(listOf(serviceId))
            routeRepository.tripTimetable("route1", serviceId, tripId)
        }
    }

    @Test
    fun `invoke should return real-time information if available`() = runTest {
        val instant = Instant.parse("2024-01-05T12:00:00Z")
        every { clock.now() } returns instant

        val route = route.copy(
            realTimeUrl = "http://realtime.url"
        )
        val serviceId = "service1"
        val tripId = "trip1"
        val service = Service(serviceId, emptyList(), emptyList())
        val tripTimetable = mockk<RouteTripTimetable>()

        every { tripTimetable.trip } returns tripInformation
        coEvery { routeRepository.route(any()) } returns Cachable(route, CacheState.LIVE)
        coEvery { serviceRepository.services(any()) } returns Cachable(listOf(service), CacheState.LIVE)
        coEvery {
            liveTripInformationUseCase.invoke(any(), any(), any(), any())
        } returns flowOf(Cachable(tripInformation, CacheState.LIVE))

        val result = useCase.invoke("route1", serviceId, tripId).first()

        assertEquals(route, result.item?.route)
        assertEquals(tripInformation, result.item?.tripInformation)
        coVerify {
            routeRepository.route("route1")
            serviceRepository.services(listOf(serviceId))
            liveTripInformationUseCase.invoke(
                "http://realtime.url",
                "route1",
                serviceId,
                tripId
            )
        }
    }

    @Test
    fun `invoke should handle exception and fallback to timetable`() = runBlocking {
        val instant = Instant.parse("2024-01-05T12:00:00Z")
        every { clock.now() } returns instant

        val route = route.copy(
            realTimeUrl = "http://realtime.url"
        )
        val serviceId = "service1"
        val tripId = "trip1"
        val service = Service(serviceId, emptyList(), emptyList())
        val tripTimetable = mockk<RouteTripTimetable>()

        every { tripTimetable.trip } returns tripInformation
        coEvery { routeRepository.route(any()) } returns Cachable(route, CacheState.LIVE)
        coEvery { serviceRepository.services(any()) } returns Cachable(listOf(service), CacheState.LIVE)
        coEvery {
            liveTripInformationUseCase.invoke(any(), any(), any(), any())
        } throws RuntimeException("Real-time service error")
        coEvery {
            routeRepository.tripTimetable(any(), any(), any())
        } returns Cachable(tripTimetable, CacheState.LIVE)

        val result = useCase.invoke("route1", serviceId, tripId).first()

        assertEquals(route, result.item?.route)
        assertEquals(tripInformation, result.item?.tripInformation)
        coVerify {
            routeRepository.route("route1")
            serviceRepository.services(listOf(serviceId))
            liveTripInformationUseCase.invoke(
                "http://realtime.url",
                "route1",
                serviceId,
                tripId
            )
            routeRepository.tripTimetable("route1", serviceId, tripId)
        }
        verify { Napier.e(any<String>(), any(), any()) }
    }
}

