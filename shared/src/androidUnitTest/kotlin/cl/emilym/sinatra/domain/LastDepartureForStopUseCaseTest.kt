package cl.emilym.sinatra.domain

import cl.emilym.sinatra.DefaultService
import cl.emilym.sinatra.DefaultStopTimetableTime
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class LastDepartureForStopUseCaseTest {

    private val servicesAndTimesForStopUseCase = mockk<ServicesAndTimesForStopUseCase>()
    private val metadataRepository = mockk<TransportMetadataRepository>()
    private val clock = mockk<Clock>()

    private val useCase = LastDepartureForStopUseCase(
        servicesAndTimesForStopUseCase,
        metadataRepository,
        clock
    )

    private val testTimeZone = TimeZone.of("Australia/Sydney")
    private val testStopId: StopId = "test-stop-123"

    private val baseTime = Instant.fromEpochSeconds(1710516600)
    private val yesterday = baseTime - 1.days
    private val today = baseTime

    @BeforeTest
    fun setup() {
        every { clock.now() } returns baseTime
        coEvery { metadataRepository.timeZone() } returns testTimeZone
    }

    @AfterTest
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `invoke returns empty list when no active services found`() = runTest {
        // Given
        val servicesAndTimes = createServicesAndTimes(emptyList(), emptyList())
        coEvery { servicesAndTimesForStopUseCase(testStopId) } returns servicesAndTimes

        // When
        val result = useCase(testStopId).first()

        // Then
        assertTrue(result.isEmpty())
        coVerify { servicesAndTimesForStopUseCase(testStopId) }
        coVerify { metadataRepository.timeZone() }
    }

    @Test
    fun `invoke returns empty list when services exist but none are active`() = runTest {
        // Given
        val inactiveService = mockk<Service>()

        val servicesAndTimes = createServicesAndTimes(
            services = listOf(inactiveService),
            times = emptyList()
        )

        // Mock service as inactive for both days
        every { inactiveService.active(any(), any()) } returns false

        coEvery { servicesAndTimesForStopUseCase(testStopId) } returns servicesAndTimes

        // When
        val result = useCase(testStopId).first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke returns future departures when available`() = runTest {
        // Given
        val activeService = mockk<Service>()

        val futureTime = 2.hours
        val timetableTime = DefaultStopTimetableTime.copy(
            serviceId = "service1",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create(futureTime)
        )

        val servicesAndTimes = createServicesAndTimes(
            services = listOf(activeService),
            times = listOf(timetableTime)
        )

        every { activeService.id } returns "service1"
        every { activeService.active(any(), any()) } returns true

        coEvery { servicesAndTimesForStopUseCase(testStopId) } returns servicesAndTimes

        // When
        val result = useCase(testStopId).first()

        // Then
        assertEquals(1, result.size)
        assertEquals(futureTime, result.first().departureTime.durationThroughDay)
        assertEquals("route1", result.first().routeId)
    }

    @Test
    fun `invoke returns today's departure when yesterday's has passed`() = runTest {
        // Given
        val yesterdayService = mockk<Service>()
        val todayService = mockk<Service>()

        // Yesterday's departure that has already passed
        val yesterdayDeparture = DefaultStopTimetableTime.copy(
            serviceId = "yesterday-service",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create(23.hours) // 2 hours ago
        )

        // Today's departure that has also passed
        val todayDeparture = DefaultStopTimetableTime.copy(
            serviceId = "today-service",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create((-1).hours) // 1 hour ago
        )

        val servicesAndTimes = createServicesAndTimes(
            services = listOf(yesterdayService, todayService),
            times = listOf(yesterdayDeparture, todayDeparture)
        )

        // Mock the active service calls for yesterday and today
        every { yesterdayService.id } returns "yesterday-service"
        every { yesterdayService.active(yesterday, testTimeZone) } returns true
        every { yesterdayService.active(today, testTimeZone) } returns false
        every { todayService.id } returns "today-service"
        every { todayService.active(yesterday, testTimeZone) } returns false
        every { todayService.active(today, testTimeZone) } returns true

        coEvery { servicesAndTimesForStopUseCase(testStopId) } returns servicesAndTimes

        // When
        val result = useCase(testStopId).first()

        // Then
        assertEquals(1, result.size)
        assertEquals("today-service", result.first().serviceId)
    }

    @Test
    fun `invoke groups by route and heading correctly`() = runTest {
        // Given
        val activeService = DefaultService.copy(id = "service1")

        val route1CityEarly = DefaultStopTimetableTime.copy(
            serviceId = "service1",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create(1.hours)
        )

        val route1CityLate = DefaultStopTimetableTime.copy(
            serviceId = "service1",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create(3.hours)
        )

        val route1Airport = DefaultStopTimetableTime.copy(
            serviceId = "service1",
            routeId = "route1",
            heading = "Airport",
            departureTime = Time.create(2.hours)
        )

        val servicesAndTimes = createServicesAndTimes(
            services = listOf(activeService),
            times = listOf(route1CityEarly, route1CityLate, route1Airport)
        )

        mockkObject(activeService)
        every { activeService.active(any(), any()) } returns true

        coEvery { servicesAndTimesForStopUseCase(testStopId) } returns servicesAndTimes

        // When
        val result = useCase(testStopId).first()

        // Then
        assertEquals(2, result.size) // One for each route+heading combination

        val cityResult = result.find { it.heading == "City" }
        val airportResult = result.find { it.heading == "Airport" }

        assertNotNull(cityResult)
        assertNotNull(airportResult)

        // Should get the last (latest) departure for City heading
        assertEquals(3.hours, cityResult.departureTime.durationThroughDay)
        assertEquals(2.hours, airportResult.departureTime.durationThroughDay)
    }

    @Test
    fun `invoke handles overnight services correctly`() = runTest {
        // Given
        val yesterdayService = DefaultService.copy(id = "yesterday-service")
        val todayService = DefaultService.copy(id = "today-service")

        val lateNightDeparture = DefaultStopTimetableTime.copy(
            serviceId = "today-service",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create(10.hours)
        )

        val earlyMorningDeparture = DefaultStopTimetableTime.copy(
            serviceId = "yesterday-service",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create(25.hours)
        )

        val servicesAndTimes = createServicesAndTimes(
            services = listOf(yesterdayService, todayService),
            times = listOf(lateNightDeparture, earlyMorningDeparture)
        )

        mockkObject(yesterdayService)
        mockkObject(todayService)
        every { yesterdayService.active(yesterday, testTimeZone) } returns true
        every { yesterdayService.active(today, testTimeZone) } returns false
        every { todayService.active(yesterday, testTimeZone) } returns false
        every { todayService.active(today, testTimeZone) } returns true

        coEvery { servicesAndTimesForStopUseCase(testStopId) } returns servicesAndTimes

        // When
        val result = useCase(testStopId).first()

        // Then
        assertEquals(1, result.size)
        val actualResult = result.first()
        assertNotNull(actualResult)
    }

    @Test
    fun `invoke handles single service across both days`() = runTest {
        // Given
        val service = mockk<Service>()

        val timetableTime = DefaultStopTimetableTime.copy(
            serviceId = "service1",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create(1.hours)
        )

        val servicesAndTimes = createServicesAndTimes(
            services = listOf(service),
            times = listOf(timetableTime)
        )

        every { service.id } returns "service1"
        every { service.active(yesterday, testTimeZone) } returns true
        every { service.active(today, testTimeZone) } returns true

        coEvery { servicesAndTimesForStopUseCase(testStopId) } returns servicesAndTimes

        // When
        val result = useCase(testStopId).first()

        // Then
        assertEquals(1, result.size)
        assertEquals("service1", result.first().serviceId)
    }

    @Test
    fun `invoke prefers future departure over past departure from yesterday`() = runTest {
        // Given
        val yesterdayService = mockk<Service>()
        val todayService = mockk<Service>()

        // Yesterday's departure that has passed
        val yesterdayDeparture = DefaultStopTimetableTime.copy(
            serviceId = "yesterday-service",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create(23.hours) // 1 hour ago
        )

        // Today's future departure
        val todayFutureDeparture = DefaultStopTimetableTime.copy(
            serviceId = "today-service",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create(2.hours)
        )

        val servicesAndTimes = createServicesAndTimes(
            services = listOf(yesterdayService, todayService),
            times = listOf(yesterdayDeparture, todayFutureDeparture)
        )

        every { yesterdayService.id } returns "yesterday-service"
        every { yesterdayService.active(yesterday, testTimeZone) } returns true
        every { yesterdayService.active(today, testTimeZone) } returns false
        every { todayService.id } returns "today-service"
        every { todayService.active(yesterday, testTimeZone) } returns false
        every { todayService.active(today, testTimeZone) } returns true

        coEvery { servicesAndTimesForStopUseCase(testStopId) } returns servicesAndTimes

        // When
        val result = useCase(testStopId).first()

        // Then
        assertEquals(1, result.size)
        assertEquals("today-service", result.first().serviceId)
        assertEquals(2.hours, result.first().departureTime.durationThroughDay)
    }

    @Test
    fun `invoke falls back to today's past departure when no future departures available`() = runTest {
        // Given
        val yesterdayService = mockk<Service>()
        val todayService = mockk<Service>()

        // Yesterday's departure that has passed
        val yesterdayDeparture = DefaultStopTimetableTime.copy(
            serviceId = "yesterday-service",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create(20.hours)
        )

        // Today's departure that has also passed
        val todayPastDeparture = DefaultStopTimetableTime.copy(
            serviceId = "today-service",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create((-1).hours)
        )

        val servicesAndTimes = createServicesAndTimes(
            services = listOf(yesterdayService, todayService),
            times = listOf(yesterdayDeparture, todayPastDeparture)
        )

        every { yesterdayService.id } returns "yesterday-service"
        every { yesterdayService.active(yesterday, testTimeZone) } returns true
        every { yesterdayService.active(today, testTimeZone) } returns false
        every { todayService.id } returns "today-service"
        every { todayService.active(yesterday, testTimeZone) } returns false
        every { todayService.active(today, testTimeZone) } returns true

        coEvery { servicesAndTimesForStopUseCase(testStopId) } returns servicesAndTimes

        // When
        val result = useCase(testStopId).first()

        // Then
        assertEquals(1, result.size)
        assertEquals("today-service", result.first().serviceId)
        assertEquals((-1).hours, result.first().departureTime.durationThroughDay)
    }

    @Test
    fun `invoke returns future departures for specified route when available`() = runTest {
        // Given
        val activeService = mockk<Service>()

        val futureTime = 2.hours
        val timetableTime = DefaultStopTimetableTime.copy(
            serviceId = "service1",
            routeId = "route1",
            heading = "City",
            departureTime = Time.create(futureTime)
        )
        val otherTimetableTime = DefaultStopTimetableTime.copy(
            serviceId = "service1",
            routeId = "route2",
            heading = "City",
            departureTime = Time.create(futureTime)
        )

        val servicesAndTimes = createServicesAndTimes(
            services = listOf(activeService),
            times = listOf(otherTimetableTime, timetableTime)
        )

        every { activeService.id } returns "service1"
        every { activeService.active(any(), any()) } returns true

        coEvery { servicesAndTimesForStopUseCase(testStopId) } returns servicesAndTimes

        // When
        val result = useCase(testStopId, routeId = "route1").first()

        // Then
        assertEquals(1, result.size)
        assertEquals(futureTime, result.first().departureTime.durationThroughDay)
        assertEquals("route1", result.first().routeId)
    }

    // Helper functions
    private fun createServicesAndTimes(
        services: List<Service>,
        times: List<StopTimetableTime>
    ): Cachable<ServicesAndTimes> {
        return Cachable.live(
            ServicesAndTimes(
                services = services,
                times = times
            )
        )
    }
}