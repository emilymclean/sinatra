package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteType
import cl.emilym.sinatra.data.models.RouteVisibility
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.repository.LiveServiceRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.timeZone
import com.google.transit.realtime.FeedMessage
import io.github.aakira.napier.Napier
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration

class LiveStopTimetableUseCaseTest {

    private lateinit var liveServiceRepository: LiveServiceRepository
    private lateinit var transportMetadataRepository: TransportMetadataRepository
    private lateinit var useCase: LiveStopTimetableUseCase
    private val scheduleStartOfDay = Instant.fromEpochMilliseconds(0)

    @BeforeTest
    fun setUp() {
        liveServiceRepository = mockk()
        transportMetadataRepository = mockk()
        useCase = LiveStopTimetableUseCase(liveServiceRepository, transportMetadataRepository)

        mockkStatic(Napier::class)
    }

    @Test
    fun `invoke should return scheduled times without real-time updates`() = runTest {
        val stopId = "stop-1"
        val scheduled = listOf(
            StopTimetableTime(
                null,
                "R1",
                "R1",
                "S1",
                "trip-1",
                Time.parse("PT10H").addReference(scheduleStartOfDay),
                Time.parse("PT10H1M").addReference(scheduleStartOfDay),
                "North",
                0,
                Route(
                    "R1",
                    "R1",
                    "R1",
                     null,
                    "R1",
                    null,
                    RouteType.BUS,
                    null,
                    RouteVisibility(
                        false,
                        null
                    )
                )
            )
        )
        coEvery { transportMetadataRepository.scheduleStartOfDay() } returns mockk()
        coEvery { transportMetadataRepository.timeZone() } returns timeZone

        val result = useCase(stopId, scheduled).toList()

        assertEquals(1, result.size)
        assertEquals(scheduled, result[0])
    }

    @Test
    fun `invoke should merge real-time updates with scheduled times when delay provided`() = runTest {
        val sId = "stop-1"
        val realTimeUrl = "https://realtime.example.com"
        val scheduled = listOf(
            StopTimetableTime(
                null,
                "R1",
                "R1",
                "S1",
                "trip-1",
                Time.parse("PT10H").addReference(scheduleStartOfDay),
                Time.parse("PT10H1M").addReference(scheduleStartOfDay),
                "North",
                0,
                Route(
                    "R1",
                    "R1",
                    "R1",
                    null,
                    "R1",
                    realTimeUrl,
                    RouteType.BUS,
                    null,
                    RouteVisibility(
                        false,
                        null
                    )
                )
            )
        )
        val updates = mockk<FeedMessage> {
            every { entity } returns listOf(
                mockk {
                    every { isDeleted } returns false
                    every { tripUpdate } returns mockk {
                        every { trip.tripId } returns "trip-1"
                        every { delay } returns 10
                        every { stopTimeUpdate } returns emptyList()
                    }
                }
            )
        }
        coEvery { transportMetadataRepository.scheduleStartOfDay() } returns scheduleStartOfDay
        coEvery { liveServiceRepository.getRealtimeUpdates(realTimeUrl) } returns flowOf(updates)
        coEvery { transportMetadataRepository.timeZone() } returns timeZone

        val result = useCase(sId, scheduled).toList()

        assertEquals(1, result.size)
        val updatedTimetable = result[0]
        assertEquals(1, updatedTimetable.size)
        assertIs<StationTime.Live>(updatedTimetable[0].stationTime.arrival)
        assertEquals(36010, updatedTimetable[0].stationTime.arrival.time.durationThroughDay.inWholeSeconds)
        assertEquals(10, (updatedTimetable[0].stationTime.arrival as StationTime.Live).delay.inWholeSeconds)
    }

    @Test
    fun `invoke should merge real-time updates with scheduled times when arrival time provided`() = runTest {
        val sId = "stop-1"
        val realTimeUrl = "https://realtime.example.com"
        val scheduled = listOf(
            StopTimetableTime(
                null,
                "R1",
                "R1",
                "S1",
                "trip-1",
                Time.parse("PT10H").addReference(scheduleStartOfDay),
                Time.parse("PT10H1M").addReference(scheduleStartOfDay),
                "North",
                0,
                Route(
                    "R1",
                    "R1",
                    "R1",
                    null,
                    "R1",
                    realTimeUrl,
                    RouteType.BUS,
                    null,
                    RouteVisibility(
                        false,
                        null
                    )
                )
            )
        )
        val updates = mockk<FeedMessage> {
            every { entity } returns listOf(
                mockk {
                    every { isDeleted } returns false
                    every { tripUpdate } returns mockk {
                        every { trip.tripId } returns "trip-1"
                        every { delay } returns 10
                        every { stopTimeUpdate } returns listOf(
                            mockk {
                                every { stopId } returns sId
                                every { arrival } returns mockk {
                                    every { time } returns (scheduleStartOfDay + Duration.parseIsoString("PT10H20S")).epochSeconds
                                }
                                every { departure } returns null
                            }
                        )
                    }
                }
            )
        }
        coEvery { transportMetadataRepository.scheduleStartOfDay() } returns scheduleStartOfDay
        coEvery { liveServiceRepository.getRealtimeUpdates(realTimeUrl) } returns flowOf(updates)
        coEvery { transportMetadataRepository.timeZone() } returns timeZone

        val result = useCase(sId, scheduled).toList()

        assertEquals(1, result.size)
        val updatedTimetable = result[0]
        assertEquals(1, updatedTimetable.size)
        assertIs<StationTime.Live>(updatedTimetable[0].stationTime.arrival)
        assertEquals(36020, updatedTimetable[0].stationTime.arrival.time.durationThroughDay.inWholeSeconds)
        assertEquals(20, (updatedTimetable[0].stationTime.arrival as StationTime.Live).delay.inWholeSeconds)
    }

    @Test
    fun `invoke should merge real-time updates with scheduled times when arrival delay provided`() = runTest {
        val sId = "stop-1"
        val realTimeUrl = "https://realtime.example.com"
        val scheduled = listOf(
            StopTimetableTime(
                null,
                "R1",
                "R1",
                "S1",
                "trip-1",
                Time.parse("PT10H").addReference(scheduleStartOfDay),
                Time.parse("PT10H1M").addReference(scheduleStartOfDay),
                "North",
                0,
                Route(
                    "R1",
                    "R1",
                    "R1",
                    null,
                    "R1",
                    realTimeUrl,
                    RouteType.BUS,
                    null,
                    RouteVisibility(
                        false,
                        null
                    )
                )
            )
        )
        val updates = mockk<FeedMessage> {
            every { entity } returns listOf(
                mockk {
                    every { isDeleted } returns false
                    every { tripUpdate } returns mockk {
                        every { trip.tripId } returns "trip-1"
                        every { delay } returns 10
                        every { stopTimeUpdate } returns listOf(
                            mockk {
                                every { stopId } returns sId
                                every { arrival } returns mockk {
                                    every { time } returns null
                                    every { delay } returns 30
                                }
                                every { departure } returns null
                            }
                        )
                    }
                }
            )
        }
        coEvery { transportMetadataRepository.scheduleStartOfDay() } returns scheduleStartOfDay
        coEvery { liveServiceRepository.getRealtimeUpdates(realTimeUrl) } returns flowOf(updates)
        coEvery { transportMetadataRepository.timeZone() } returns timeZone

        val result = useCase(sId, scheduled).toList()

        assertEquals(1, result.size)
        val updatedTimetable = result[0]
        assertEquals(1, updatedTimetable.size)
        assertIs<StationTime.Live>(updatedTimetable[0].stationTime.arrival)
        assertEquals(36030, updatedTimetable[0].stationTime.arrival.time.durationThroughDay.inWholeSeconds)
        assertEquals(30, (updatedTimetable[0].stationTime.arrival as StationTime.Live).delay.inWholeSeconds)
    }

    @Test
    fun `invoke should handle errors in real-time updates and fallback to scheduled times`() = runTest {
        val stopId = "stop-1"
        val realTimeUrl = "https://realtime.example.com"
        val scheduled = listOf(
            StopTimetableTime(
                null,
                "R1",
                "R1",
                "S1",
                "trip-1",
                Time.parse("PT10H").addReference(scheduleStartOfDay),
                Time.parse("PT10H1M").addReference(scheduleStartOfDay),
                "North",
                0,
                Route(
                    "R1",
                    "R1",
                    "R1",
                    null,
                    "R1",
                    realTimeUrl,
                    RouteType.BUS,
                    null,
                    RouteVisibility(
                        false,
                        null
                    )
                )
            )
        )
        coEvery { transportMetadataRepository.scheduleStartOfDay() } returns mockk()
        coEvery { liveServiceRepository.getRealtimeUpdates(realTimeUrl) } returns flow { throw Exception("Network error") }
        coEvery { transportMetadataRepository.timeZone() } returns timeZone
//        every { Napier.e(any(), any<Throwable>(), any()) } just Runs

        val result = useCase(stopId, scheduled).toList()

        assertEquals(1, result.size)
        assertEquals(scheduled, result[0])
    }
}
