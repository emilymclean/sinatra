package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.models.StopTimetable
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.Time
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

@OptIn(ExperimentalCoroutinesApi::class)
class UpcomingRoutesForStopUseCaseTest {

    private lateinit var liveStopTimetableUseCase: LiveStopTimetableUseCase
    private lateinit var servicesAndTimesForStopUseCase: ServicesAndTimesForStopUseCase
    private lateinit var metadataRepository: TransportMetadataRepository
    private lateinit var clock: Clock
    private lateinit var useCase: UpcomingRoutesForStopUseCase
    private lateinit var service: Service

    @BeforeTest
    fun setup() {
        liveStopTimetableUseCase = mockk()
        servicesAndTimesForStopUseCase = mockk()
        metadataRepository = mockk()
        clock = mockk()
        service = mockk()
        useCase = UpcomingRoutesForStopUseCase(
            liveStopTimetableUseCase,
            servicesAndTimesForStopUseCase,
            clock,
            metadataRepository
        )

        every { service.id } returns "service-1"
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
                arrivalTime = Time.parse("PT14H"),
                departureTime = Time.parse("PT14H5M"),
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
}