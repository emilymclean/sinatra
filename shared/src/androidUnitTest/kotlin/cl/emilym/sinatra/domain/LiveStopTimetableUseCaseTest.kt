package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.DelayInformation
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteType
import cl.emilym.sinatra.data.models.RouteVisibility
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopAccessibility
import cl.emilym.sinatra.data.models.StopRealtimeInformation
import cl.emilym.sinatra.data.models.StopRealtimeUpdate
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.StopVisibility
import cl.emilym.sinatra.data.models.StopWheelchairAccessibility
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.repository.LiveServiceRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.timeZone
import com.google.transit.realtime.FeedMessage
import io.github.aakira.napier.Napier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class LiveStopTimetableUseCaseTest {

    private lateinit var liveServiceRepository: LiveServiceRepository
    private lateinit var stopRepository: StopRepository
    private lateinit var useCase: LiveStopTimetableUseCase
    private val scheduleStartOfDay = Instant.fromEpochMilliseconds(0)
    private lateinit var clock: Clock

    @BeforeTest
    fun setUp() {
        liveServiceRepository = mockk()
        stopRepository = mockk()
        clock = mockk()
        useCase = LiveStopTimetableUseCase(liveServiceRepository, stopRepository, clock)

        every { clock.now() } returns Instant.fromEpochMilliseconds(0) + 20.minutes

        mockkStatic(Napier::class)
    }

    @Test
    fun `invoke should merge real-time updates with scheduled times when delay provided`() = runTest {
        val sId = "stop-1"
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
                    true,
                    RouteType.BUS,
                    null,
                    RouteVisibility(
                        false,
                        null
                    )
                )
            )
        )
        val updates = StopRealtimeInformation(
            updates = listOf(
                StopRealtimeUpdate(
                    "trip-1",
                    DelayInformation.Fixed(10.seconds)
                )
            ),
            expire = Instant.DISTANT_FUTURE
        )
        coEvery { liveServiceRepository.getStopRealtimeUpdates(any()) } returns updates
        coEvery { stopRepository.stop(sId) } returns Cachable.live(Stop("stop1", null, "Stop 1", "Stop 1",
            MapLocation(0.0,0.0,), StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), true
        ))

        val result = useCase(sId, scheduled).toList()

        assertEquals(1, result.size)
        val updatedTimetable = result[0]
        assertEquals(1, updatedTimetable.size)
        assertIs<StationTime.Live>(updatedTimetable[0].stationTime.arrival)
        assertEquals(36010, updatedTimetable[0].stationTime.arrival.time.durationThroughDay.inWholeSeconds)
        assertEquals(10, (updatedTimetable[0].stationTime.arrival as StationTime.Live).delay.inWholeSeconds)
    }

    @Test
    fun `invoke should fallback to scheduled times when delay unknown`() = runTest {
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
                    true,
                    RouteType.BUS,
                    null,
                    RouteVisibility(
                        false,
                        null
                    )
                )
            )
        )
        val updates = StopRealtimeInformation(
            updates = listOf(
                StopRealtimeUpdate(
                    "trip-1",
                    DelayInformation.Unknown
                )
            ),
            expire = Instant.DISTANT_FUTURE
        )
        coEvery { liveServiceRepository.getStopRealtimeUpdates(any()) } returns updates
        coEvery { stopRepository.stop(stopId) } returns Cachable.live(Stop("stop1", null, "Stop 1", "Stop 1",
            MapLocation(0.0,0.0,), StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), true
        ))

        val result = useCase(stopId, scheduled).toList()

        assertEquals(1, result.size)
        assertEquals(scheduled, result[0])
    }

    @Test
    fun `invoke should fallback to scheduled times when feed expired`() = runTest {
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
                    true,
                    RouteType.BUS,
                    null,
                    RouteVisibility(
                        false,
                        null
                    )
                )
            )
        )
        val updates = StopRealtimeInformation(
            updates = listOf(
                StopRealtimeUpdate(
                    "trip-1",
                    DelayInformation.Fixed(10.seconds)
                )
            ),
            expire = Instant.fromEpochMilliseconds(0)
        )
        coEvery { liveServiceRepository.getStopRealtimeUpdates(any()) } returns updates
        coEvery { stopRepository.stop(stopId) } returns Cachable.live(Stop("stop1", null, "Stop 1", "Stop 1",
            MapLocation(0.0,0.0,), StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), true
        ))

        val result = useCase(stopId, scheduled).toList()

        assertEquals(1, result.size)
        assertEquals(scheduled, result[0])
    }

    @Test
    fun `invoke should fallback to scheduled times when stop not live`() = runTest {
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
                    true,
                    RouteType.BUS,
                    null,
                    RouteVisibility(
                        false,
                        null
                    )
                )
            )
        )

        coEvery { stopRepository.stop(stopId) } returns Cachable.live(Stop("stop1", null, "Stop 1", "Stop 1",
            MapLocation(0.0,0.0,), StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), false
        ))

        val result = useCase(stopId, scheduled).toList()

        assertEquals(1, result.size)
        assertEquals(scheduled, result[0])
        coVerify(exactly = 0) { liveServiceRepository.getStopRealtimeUpdates(any()) }
    }

    @Test
    fun `invoke should handle errors in real-time updates and fallback to scheduled times`() = runTest {
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
                    true,
                    RouteType.BUS,
                    null,
                    RouteVisibility(
                        false,
                        null
                    )
                )
            )
        )
        coEvery { liveServiceRepository.getStopRealtimeUpdates(any()) } throws Exception("Network error")
        coEvery { stopRepository.stop(stopId) } returns Cachable.live(Stop("stop1", null, "Stop 1", "Stop 1",
            MapLocation(0.0,0.0,), StopAccessibility(StopWheelchairAccessibility.FULL), StopVisibility(false, false, false, null), true
        ))

        val result = useCase(stopId, scheduled).toList()

        assertEquals(1, result.size)
        assertEquals(scheduled, result[0])
    }
}
