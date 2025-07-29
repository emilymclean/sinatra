package cl.emilym.sinatra.domain

import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.models.StopTimetable
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalCoroutinesApi::class)
class UpcomingRoutesForStopUseCaseTest {

    private lateinit var liveStopTimetableUseCase: LiveStopTimetableUseCase
    private lateinit var servicesAndTimesForStopUseCase: ServicesAndTimesForStopUseCase
    private lateinit var metadataRepository: TransportMetadataRepository
    private lateinit var remoteConfigRepository: RemoteConfigRepository
    private lateinit var clock: Clock
    private lateinit var useCase: UpcomingRoutesForStopUseCase
    private lateinit var service: Service

    @BeforeTest
    fun setup() {
        liveStopTimetableUseCase = mockk()
        servicesAndTimesForStopUseCase = mockk()
        metadataRepository = mockk()
        remoteConfigRepository = mockk()
        clock = mockk()
        service = mockk()
        useCase = UpcomingRoutesForStopUseCase(
            liveStopTimetableUseCase,
            servicesAndTimesForStopUseCase,
            clock,
            metadataRepository,
            remoteConfigRepository
        )

        every { service.id } returns "service-1"
        coEvery { remoteConfigRepository.feature(any()) } returns true
    }

    @Test
    fun `should return active stop timetable times`() = runTest {
        val stopId = "stop-123"
        val timeZone = TimeZone.UTC
        val currentTime = Instant.parse("2024-01-01T01:00:00Z")

        val timetable = listOf(
            StopTimetableTime(
                childStopId = null,
                routeId = "route-1",
                routeCode = "R1",
                serviceId = "service-1",
                tripId = "trip-1",
                arrivalTime = Time.parse("PT13H"),
                departureTime = Time.parse("PT13H5M"),
                heading = "North",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            )
        )


        val services = listOf(
            service
        )

        every { service.active(any(), any(), any()) } returns true
        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { servicesAndTimesForStopUseCase.invoke(stopId) } returns Cachable.live(
            ServicesAndTimes(
                services = services,
                times = timetable
            )
        )
        coEvery { clock.now() } returns currentTime

        val result = useCase(stopId, number = 1, live = false).take(1).first()

        assertEquals(1, result.item.size)
        assertEquals("R1", result.item.first().routeCode)

        coVerify { servicesAndTimesForStopUseCase.invoke(stopId) }
        coVerify { metadataRepository.timeZone() }
        coVerify(exactly = 0) { liveStopTimetableUseCase.invoke(any(), any()) }
        verify { clock.now() }
    }

    @Test
    fun `should handle no active times gracefully`() = runTest {
        val stopId = "stop-123"
        val timeZone = TimeZone.UTC
        val currentTime = Instant.parse("2024-01-01T12:00:00Z")

        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { servicesAndTimesForStopUseCase.invoke(stopId) } returns Cachable.live(
            ServicesAndTimes(
                services = emptyList(),
                times = emptyList()
            )
        )
        every { clock.now() } returns currentTime

        val result = useCase(stopId, live = false).take(1).first()

        assertEquals(0, result.item.size)

        coVerify { servicesAndTimesForStopUseCase.invoke(stopId) }
        coVerify { metadataRepository.timeZone() }
        coVerify(exactly = 0) { liveStopTimetableUseCase.invoke(any(), any()) }
        coVerify { clock.now() }
    }

    @Test
    fun `should emit live timetable times`() = runTest {
        val instant = Instant.parse("2024-01-05T12:00:00Z")
        every { clock.now() } returns instant

        val stopId = "stop-123"
        val timeZone = TimeZone.UTC
        val timetableTimes = listOf(
            StopTimetableTime(
                childStopId = null,
                routeId = "route-1",
                routeCode = "R1",
                serviceId = "service-1",
                tripId = "trip-1",
                arrivalTime = Time.parse("PT13H"),
                departureTime = Time.parse("PT13H5M"),
                heading = "North",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            )
        )

        val liveTimes = listOf(
            StopTimetableTime(
                childStopId = null,
                routeId = "route-2",
                routeCode = "R2",
                serviceId = "service-2",
                tripId = "trip-2",
                arrivalTime = Time.parse("PT14H").forDay(instant),
                departureTime = Time.parse("PT14H5M").forDay(instant),
                heading = "South",
                sequence = 2,
                last = false,
                route = null,
                childStop = null
            )
        )

        val services = listOf(
            service
        )

        every { service.active(any(), any(), any()) } returns true
        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { servicesAndTimesForStopUseCase.invoke(stopId) } returns Cachable.live(
            ServicesAndTimes(
                services = services,
                times = timetableTimes
            )
        )
        every { liveStopTimetableUseCase(stopId, any()) } returnsMany listOf(
            flowOf(liveTimes), flowOf(emptyList())
        )

        val result = useCase(stopId).take(1).first()

        assertEquals(1, result.item.size)
        assertEquals("R2", result.item.first().routeCode)

        coVerify { servicesAndTimesForStopUseCase.invoke(stopId) }
        verify(exactly = 1) { liveStopTimetableUseCase(stopId, any()) }
    }

    @Test
    fun `should return stop timetable times from requested routes`() = runTest {
        val stopId = "stop-123"
        val timeZone = TimeZone.UTC
        val currentTime = Instant.parse("2024-01-01T01:00:00Z")

        val timetable = listOf(
            StopTimetableTime(
                childStopId = null,
                routeId = "route-1",
                routeCode = "R1",
                serviceId = "service-1",
                tripId = "trip-1",
                arrivalTime = Time.parse("PT13H"),
                departureTime = Time.parse("PT13H5M"),
                heading = "North",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            ),
            StopTimetableTime(
                childStopId = null,
                routeId = "route-2",
                routeCode = "R2",
                serviceId = "service-1",
                tripId = "trip-1",
                arrivalTime = Time.parse("PT13H"),
                departureTime = Time.parse("PT13H5M"),
                heading = "North",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            )
        )


        val services = listOf(
            service
        )

        every { service.active(any(), any(), any()) } returns true
        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { servicesAndTimesForStopUseCase.invoke(stopId) } returns Cachable.live(
            ServicesAndTimes(
                services = services,
                times = timetable
            )
        )
        coEvery { clock.now() } returns currentTime

        val result = useCase(stopId, routeIds = listOf("route-1"), live = false).take(1).first()

        assertEquals(1, result.item.size)
        assertEquals("R1", result.item.first().routeCode)

        coVerify { servicesAndTimesForStopUseCase.invoke(stopId) }
        coVerify { metadataRepository.timeZone() }
        coVerify(exactly = 0) { liveStopTimetableUseCase.invoke(any(), any()) }
        verify { clock.now() }
    }

    @Test
    fun `should include next day times when after 10pm and feature flag enabled`() = runTest {
        val stopId = "stop-123"
        val timeZone = TimeZone.UTC
        // Set time to 10:30 PM
        val currentTime = Instant.parse("2024-01-01T22:30:00Z")

        val timetable = listOf(
            // Next day time
            StopTimetableTime(
                childStopId = null,
                routeId = "route-2",
                routeCode = "R2",
                serviceId = "service-1",
                tripId = "trip-2",
                arrivalTime = Time.parse("PT6H"), // 6 AM next day
                departureTime = Time.parse("PT6H5M"),
                heading = "South",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            ),
            // Current day time that has already passed
            StopTimetableTime(
                childStopId = null,
                routeId = "route-1",
                routeCode = "R1",
                serviceId = "service-1",
                tripId = "trip-1",
                arrivalTime = Time.parse("PT20H"), // 8 PM - already passed
                departureTime = Time.parse("PT20H5M"),
                heading = "North",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            ),
        )

        val services = listOf(service)

        every { service.active(any(), any(), any()) } returns true
        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { servicesAndTimesForStopUseCase.invoke(stopId) } returns Cachable.live(
            ServicesAndTimes(
                services = services,
                times = timetable
            )
        )
        coEvery { clock.now() } returns currentTime
        coEvery { remoteConfigRepository.feature(FeatureFlag.UPCOMING_ROUTES_INCLUDE_NEXT_DAY) } returns true

        val result = useCase(stopId, number = 10, live = false).take(1).first()

        assertEquals(2, result.item.size)
        assertEquals("R2", result.item.first().routeCode)
        assertEquals("R1", result.item.last().routeCode)
        assertEquals((currentTime + 1.days).startOfDay(timeZone), result.item.last().arrivalTime.instant.startOfDay(timeZone))

        coVerify { servicesAndTimesForStopUseCase.invoke(stopId) }
        coVerify { metadataRepository.timeZone() }
        coVerify { remoteConfigRepository.feature(FeatureFlag.UPCOMING_ROUTES_INCLUDE_NEXT_DAY) }
        verify { clock.now() }
    }

    @Test
    fun `should not include next day times when after 10pm but feature flag disabled`() = runTest {
        val stopId = "stop-123"
        val timeZone = TimeZone.UTC
        // Set time to 10:30 PM
        val currentTime = Instant.parse("2024-01-01T22:30:00Z")

        val timetable = listOf(
            // Current day time that has already passed
            StopTimetableTime(
                childStopId = null,
                routeId = "route-1",
                routeCode = "R1",
                serviceId = "service-1",
                tripId = "trip-1",
                arrivalTime = Time.parse("PT20H"), // 8 PM - already passed
                departureTime = Time.parse("PT20H5M"),
                heading = "North",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            ),
            // Next day time
            StopTimetableTime(
                childStopId = null,
                routeId = "route-2",
                routeCode = "R2",
                serviceId = "service-1",
                tripId = "trip-2",
                arrivalTime = Time.parse("PT6H"), // 6 AM next day
                departureTime = Time.parse("PT6H5M"),
                heading = "South",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            )
        )

        val services = listOf(service)

        every { service.active(any(), any(), any()) } returns true
        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { servicesAndTimesForStopUseCase.invoke(stopId) } returns Cachable.live(
            ServicesAndTimes(
                services = services,
                times = timetable
            )
        )
        coEvery { clock.now() } returns currentTime
        coEvery { remoteConfigRepository.feature(FeatureFlag.UPCOMING_ROUTES_INCLUDE_NEXT_DAY) } returns false

        val result = useCase(stopId, number = 10, live = false).take(1).first()

        assertEquals(0, result.item.size)

        coVerify { servicesAndTimesForStopUseCase.invoke(stopId) }
        coVerify { metadataRepository.timeZone() }
        coVerify { remoteConfigRepository.feature(FeatureFlag.UPCOMING_ROUTES_INCLUDE_NEXT_DAY) }
        verify { clock.now() }
    }

    @Test
    fun `should not include next day times when before 10pm even with feature flag enabled`() = runTest {
        val stopId = "stop-123"
        val timeZone = TimeZone.UTC
        // Set time to 9:30 PM (before 10 PM)
        val currentTime = Instant.parse("2024-01-01T21:30:00Z")

        val timetable = listOf(
            // Current day time that is upcoming
            StopTimetableTime(
                childStopId = null,
                routeId = "route-1",
                routeCode = "R1",
                serviceId = "service-1",
                tripId = "trip-1",
                arrivalTime = Time.parse("PT23H"), // 11 PM - still upcoming
                departureTime = Time.parse("PT23H5M"),
                heading = "North",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            ),
            // Next day time
            StopTimetableTime(
                childStopId = null,
                routeId = "route-2",
                routeCode = "R2",
                serviceId = "service-1",
                tripId = "trip-2",
                arrivalTime = Time.parse("PT6H"), // 6 AM next day
                departureTime = Time.parse("PT6H5M"),
                heading = "South",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            )
        )

        val services = listOf(service)

        every { service.active(any(), any(), any()) } returns true
        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { servicesAndTimesForStopUseCase.invoke(stopId) } returns Cachable.live(
            ServicesAndTimes(
                services = services,
                times = timetable
            )
        )
        coEvery { clock.now() } returns currentTime
        coEvery { remoteConfigRepository.feature(FeatureFlag.UPCOMING_ROUTES_INCLUDE_NEXT_DAY) } returns true

        val result = useCase(stopId, number = 10, live = false).take(1).first()

        // Should only return the current day time (11 PM)
        assertEquals(1, result.item.size)
        assertEquals("R1", result.item.first().routeCode)
        assertEquals(currentTime.startOfDay(timeZone), result.item.first().arrivalTime.instant.startOfDay(timeZone))

        coVerify { servicesAndTimesForStopUseCase.invoke(stopId) }
        coVerify { metadataRepository.timeZone() }
        verify { clock.now() }
    }

    @Test
    fun `should include both current and next day times when after 10pm with feature flag enabled`() = runTest {
        val stopId = "stop-123"
        val timeZone = TimeZone.UTC
        // Set time to 10:30 PM
        val currentTime = Instant.parse("2024-01-01T22:30:00Z")

        val timetable = listOf(
            // Next day time
            StopTimetableTime(
                childStopId = null,
                routeId = "route-2",
                routeCode = "R2",
                serviceId = "service-1",
                tripId = "trip-2",
                arrivalTime = Time.parse("PT6H"), // 6 AM next day
                departureTime = Time.parse("PT6H5M"),
                heading = "South",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            ),
            // Current day time that is still upcoming
            StopTimetableTime(
                childStopId = null,
                routeId = "route-1",
                routeCode = "R1",
                serviceId = "service-1",
                tripId = "trip-1",
                arrivalTime = Time.parse("PT23H"), // 11 PM - still upcoming
                departureTime = Time.parse("PT23H5M"),
                heading = "North",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            ),
        )

        val services = listOf(service)

        every { service.active(any(), any(), any()) } returns true
        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { servicesAndTimesForStopUseCase.invoke(stopId) } returns Cachable.live(
            ServicesAndTimes(
                services = services,
                times = timetable
            )
        )
        coEvery { clock.now() } returns currentTime
        coEvery { remoteConfigRepository.feature(FeatureFlag.UPCOMING_ROUTES_INCLUDE_NEXT_DAY) } returns true

        val result = useCase(stopId, number = 10, live = false).take(1).first()

        // Should return both times, sorted by arrival time
        assertEquals(3, result.item.size)
        assertEquals("R1", result.item.first().routeCode) // 11 PM today
        assertEquals("R2", result.item[1].routeCode) // 6 AM tomorrow
        assertEquals("R1", result.item.last().routeCode) // 11 PM tomorrow
        assertEquals((currentTime).startOfDay(timeZone), result.item.first().arrivalTime.instant.startOfDay(timeZone))
        assertEquals((currentTime + 1.days).startOfDay(timeZone), result.item[1].arrivalTime.instant.startOfDay(timeZone))
        assertEquals((currentTime + 1.days).startOfDay(timeZone), result.item.last().arrivalTime.instant.startOfDay(timeZone))

        coVerify { servicesAndTimesForStopUseCase.invoke(stopId) }
        coVerify { metadataRepository.timeZone() }
        coVerify { remoteConfigRepository.feature(FeatureFlag.UPCOMING_ROUTES_INCLUDE_NEXT_DAY) }
        verify { clock.now() }
    }

    @Test
    fun `should respect number limit when including next day times`() = runTest {
        val stopId = "stop-123"
        val timeZone = TimeZone.UTC
        // Set time to 10:30 PM
        val currentTime = Instant.parse("2024-01-01T22:30:00Z")

        val timetable = listOf(
            // Next day times
            StopTimetableTime(
                childStopId = null,
                routeId = "route-2",
                routeCode = "R2",
                serviceId = "service-1",
                tripId = "trip-2",
                arrivalTime = Time.parse("PT6H"), // 6 AM next day
                departureTime = Time.parse("PT6H5M"),
                heading = "South",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            ),
            StopTimetableTime(
                childStopId = null,
                routeId = "route-3",
                routeCode = "R3",
                serviceId = "service-1",
                tripId = "trip-3",
                arrivalTime = Time.parse("PT7H"), // 7 AM next day
                departureTime = Time.parse("PT7H5M"),
                heading = "East",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            ),
            // Current day time
            StopTimetableTime(
                childStopId = null,
                routeId = "route-1",
                routeCode = "R1",
                serviceId = "service-1",
                tripId = "trip-1",
                arrivalTime = Time.parse("PT23H"), // 11 PM - still upcoming
                departureTime = Time.parse("PT23H5M"),
                heading = "North",
                sequence = 1,
                last = false,
                route = null,
                childStop = null
            ),
        )

        val services = listOf(service)

        every { service.active(any(), any(), any()) } returns true
        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { servicesAndTimesForStopUseCase.invoke(stopId) } returns Cachable.live(
            ServicesAndTimes(
                services = services,
                times = timetable
            )
        )
        coEvery { clock.now() } returns currentTime
        coEvery { remoteConfigRepository.feature(FeatureFlag.UPCOMING_ROUTES_INCLUDE_NEXT_DAY) } returns true

        val result = useCase(stopId, number = 2, live = false).take(1).first()

        println("${result.item}")

        // Should return only 2 times due to number limit
        assertEquals(2, result.item.size)
        assertEquals("R1", result.item.first().routeCode) // 11 PM today
        assertEquals("R2", result.item.last().routeCode) // 6 AM tomorrow (R3 excluded due to limit)
        assertEquals((currentTime + 1.days).startOfDay(timeZone), result.item.last().arrivalTime.instant.startOfDay(timeZone))

        coVerify { servicesAndTimesForStopUseCase.invoke(stopId) }
        coVerify { metadataRepository.timeZone() }
        coVerify { remoteConfigRepository.feature(FeatureFlag.UPCOMING_ROUTES_INCLUDE_NEXT_DAY) }
        verify { clock.now() }
    }
}