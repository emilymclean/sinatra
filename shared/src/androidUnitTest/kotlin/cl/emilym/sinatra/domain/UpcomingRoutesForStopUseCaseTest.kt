package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.IStopTimetableTime
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
    private lateinit var stopRepository: StopRepository
    private lateinit var serviceRepository: ServiceRepository
    private lateinit var metadataRepository: TransportMetadataRepository
    private lateinit var clock: Clock
    private lateinit var useCase: UpcomingRoutesForStopUseCase
    private lateinit var service: Service

    @BeforeTest
    fun setup() {
        liveStopTimetableUseCase = mockk()
        stopRepository = mockk()
        serviceRepository = mockk()
        metadataRepository = mockk()
        clock = mockk()
        service = mockk()
        useCase = UpcomingRoutesForStopUseCase(
            liveStopTimetableUseCase,
            stopRepository,
            serviceRepository,
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

        val timetable = StopTimetable(
            times = listOf(
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
                    route = null
                )
            )
        )


        val services = Cachable.live(
            listOf(
                service
            )
        )

        every { service.active(any(), any(), any()) } returns true
        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { stopRepository.timetable(stopId) } returns Cachable.live(timetable)
        coEvery { serviceRepository.services(listOf("service-1")) } returns services
        coEvery { clock.now() } returns currentTime
        coEvery { liveStopTimetableUseCase.invoke(stopId, any()) } returns flowOf(timetable.times)

        val result = useCase(stopId, number = 1).take(1).first()

        assertEquals(1, result.item.size)
        assertEquals("R1", result.item.first().routeCode)

        coVerify { stopRepository.timetable(stopId) }
        coVerify { serviceRepository.services(listOf("service-1")) }
        coVerify { metadataRepository.timeZone() }
        verify { clock.now() }
    }

    @Test
    fun `should return active stop timetable times filtered by route`() = runTest {
        val stopId = "stop-123"
        val timeZone = TimeZone.UTC
        val currentTime = Instant.parse("2024-01-01T01:00:00Z")

        val timetable = StopTimetable(
            times = listOf(
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
                    route = null
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
                    route = null
                )
            )
        )


        val services = Cachable.live(
            listOf(
                service
            )
        )

        every { service.active(any(), any(), any()) } returns true
        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { stopRepository.timetable(stopId) } returns Cachable.live(timetable)
        coEvery { serviceRepository.services(listOf("service-1")) } returns services
        coEvery { clock.now() } returns currentTime
        coEvery { liveStopTimetableUseCase.invoke(stopId, any()) } answers { flowOf(it.invocation.args[1] as List<IStopTimetableTime>) }

        val result = useCase(stopId, "route-2", number = 1).take(1).first()

        assertEquals(1, result.item.size)
        assertEquals("R2", result.item.first().routeCode)

        coVerify { stopRepository.timetable(stopId) }
        coVerify { serviceRepository.services(listOf("service-1")) }
        coVerify { metadataRepository.timeZone() }
        verify { clock.now() }
    }

    @Test
    fun `should return active stop timetable times filtered by route and heading`() = runTest {
        val stopId = "stop-123"
        val timeZone = TimeZone.UTC
        val currentTime = Instant.parse("2024-01-01T01:00:00Z")

        val timetable = StopTimetable(
            times = listOf(
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
                    route = null
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
                    route = null
                ),
                StopTimetableTime(
                    childStopId = null,
                    routeId = "route-2",
                    routeCode = "R2",
                    serviceId = "service-1",
                    tripId = "trip-1",
                    arrivalTime = Time.parse("PT13H"),
                    departureTime = Time.parse("PT13H5M"),
                    heading = "South",
                    sequence = 1,
                    route = null
                )
            )
        )


        val services = Cachable.live(
            listOf(
                service
            )
        )

        every { service.active(any(), any(), any()) } returns true
        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { stopRepository.timetable(stopId) } returns Cachable.live(timetable)
        coEvery { serviceRepository.services(listOf("service-1")) } returns services
        coEvery { clock.now() } returns currentTime
        coEvery { liveStopTimetableUseCase.invoke(stopId, any()) } answers { flowOf(it.invocation.args[1] as List<IStopTimetableTime>) }

        val result = useCase(stopId, "route-2", "South", number = 1).take(1).first()

        assertEquals(1, result.item.size)
        assertEquals("R2", result.item.first().routeCode)
        assertEquals("South", result.item.first().heading)

        coVerify { stopRepository.timetable(stopId) }
        coVerify { serviceRepository.services(listOf("service-1")) }
        coVerify { metadataRepository.timeZone() }
        verify { clock.now() }
    }

    @Test
    fun `should handle no active times gracefully`() = runTest {
        val stopId = "stop-123"
        val timeZone = TimeZone.UTC
        val currentTime = Instant.parse("2024-01-01T12:00:00Z")

        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { serviceRepository.services(listOf()) } returns Cachable.live(emptyList())
        coEvery { stopRepository.timetable(stopId) } returns Cachable.live(
            StopTimetable(times = emptyList())
        )
        every { clock.now() } returns currentTime

        val result = useCase(stopId).take(1).first()

        assertEquals(0, result.item.size)

        coVerify { stopRepository.timetable(stopId) }
        coVerify { metadataRepository.timeZone() }
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
                route = null
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
                route = null
            )
        )

        val services = Cachable.live(
            listOf(
                service
            )
        )

        every { service.active(any(), any(), any()) } returns true
        coEvery { metadataRepository.timeZone() } returns timeZone
        coEvery { stopRepository.timetable(stopId) } returns Cachable.live(
            StopTimetable(times = timetableTimes)
        )
        coEvery { serviceRepository.services(listOf("service-1")) } returns services
        every { liveStopTimetableUseCase(stopId, any()) } returnsMany listOf(
            flowOf(liveTimes), flowOf(emptyList())
        )

        val result = useCase(stopId).take(1).first()

        assertEquals(1, result.item.size)
        assertEquals("R2", result.item.first().routeCode)

        coVerify { stopRepository.timetable(stopId) }
        verify(exactly = 1) { liveStopTimetableUseCase(stopId, any()) }
    }
}