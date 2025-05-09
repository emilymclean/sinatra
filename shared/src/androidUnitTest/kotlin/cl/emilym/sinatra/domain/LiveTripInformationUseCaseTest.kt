package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.RouteServiceAccessibility
import cl.emilym.sinatra.data.models.RouteTripInformation
import cl.emilym.sinatra.data.models.RouteTripStop
import cl.emilym.sinatra.data.models.RouteTripTimetable
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.repository.LiveServiceRepository
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.timeZone
import com.google.transit.realtime.FeedMessage
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.time.Duration

class LiveTripInformationUseCaseTest {

    private val liveServiceRepository = mockk<LiveServiceRepository>()
    private val routeRepository = mockk<RouteRepository>()
    private val transportMetadataRepository = mockk<TransportMetadataRepository>()
    private val scheduleStartOfDay = LocalDateTime(2024, Month.JANUARY, 5, 0, 0, 0).toInstant(
        timeZone
    )

    private val useCase = LiveTripInformationUseCase(
        liveServiceRepository = liveServiceRepository,
        routeRepository = routeRepository,
        metadataRepository = transportMetadataRepository
    )

    @Test
    fun `invoke should merge real-time updates with scheduled times when delay provided`() = runTest {
        // Arrange
        val instant = Instant.parse("2024-01-05T12:00:00Z")
        val liveInformationUrl = "http://realtime.url"
        val routeId = "route-1"
        val serviceId = "service-1"
        val tripId = "trip-1"
        val sId = "stop-1"

        val scheduledTimetable = RouteTripTimetable(
            trip = RouteTripInformation(
                startTime = null,
                endTime = null,
                accessibility = RouteServiceAccessibility(
                    ServiceBikesAllowed.ALLOWED,
                    ServiceWheelchairAccessible.ACCESSIBLE
                ),
                heading = "North",
                stops = listOf(
                    RouteTripStop(
                        stopId = sId,
                        arrivalTime = Time.parse("PT10H").addReference(scheduleStartOfDay),
                        departureTime = Time.parse("PT10H1M").addReference(scheduleStartOfDay),
                        sequence = 1,
                        stop = null
                    )
                )
            )
        )

        val feedMessage = mockk<FeedMessage> {
            every { entity } returns listOf(
                mockk {
                    every { isDeleted } returns false
                    every { tripUpdate } returns mockk {
                        every { trip.tripId } returns tripId
                        every { delay } returns 10
                        every { stopTimeUpdate } returns emptyList()
                    }
                }
            )
        }

        coEvery { routeRepository.tripTimetable(routeId, serviceId, tripId, any()) } returns Cachable.live(scheduledTimetable)
        coEvery { transportMetadataRepository.scheduleStartOfDay() } returns scheduleStartOfDay
        coEvery { liveServiceRepository.getRealtimeUpdates(liveInformationUrl) } returns flowOf(feedMessage)
        coEvery { transportMetadataRepository.timeZone() } returns timeZone

        // Act
        val result = useCase.invoke(liveInformationUrl, routeId, serviceId, tripId, instant).first()

        // Assert
        val updatedTimetable = result.item
        assertEquals(1, updatedTimetable.stops.size)
        assertEquals(sId, updatedTimetable.stops[0].stopId)
        assertNotNull(updatedTimetable.stops[0].stationTime)
        assertIs<StationTime.Live>(updatedTimetable.stops[0].stationTime!!.arrival)
        assertEquals(36010, updatedTimetable.stops[0].stationTime!!.arrival.time.durationThroughDay.inWholeSeconds)
        assertEquals(10, (updatedTimetable.stops[0].stationTime!!.arrival as StationTime.Live).delay.inWholeSeconds)
    }

    @Test
    fun `invoke should merge real-time updates with scheduled times when arrival time provided`() = runTest {
        // Arrange
        val instant = scheduleStartOfDay
        val liveInformationUrl = "http://realtime.url"
        val routeId = "route-1"
        val serviceId = "service-1"
        val tripId = "trip-1"
        val sId = "stop-1"

        val scheduledTimetable = RouteTripTimetable(
            trip = RouteTripInformation(
                startTime = null,
                endTime = null,
                accessibility = RouteServiceAccessibility(
                    ServiceBikesAllowed.ALLOWED,
                    ServiceWheelchairAccessible.ACCESSIBLE
                ),
                heading = "North",
                stops = listOf(
                    RouteTripStop(
                        stopId = sId,
                        arrivalTime = Time.parse("PT10H").addReference(scheduleStartOfDay),
                        departureTime = Time.parse("PT10H1M").addReference(scheduleStartOfDay),
                        sequence = 1,
                        stop = null
                    )
                )
            )
        )

        val feedMessage = mockk<FeedMessage> {
            every { entity } returns listOf(
                mockk {
                    every { isDeleted } returns false
                    every { tripUpdate } returns mockk {
                        every { trip.tripId } returns tripId
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

        coEvery { routeRepository.tripTimetable(routeId, serviceId, tripId, any()) } returns Cachable.live(scheduledTimetable)
        coEvery { transportMetadataRepository.scheduleStartOfDay() } returns scheduleStartOfDay
        coEvery { liveServiceRepository.getRealtimeUpdates(liveInformationUrl) } returns flowOf(feedMessage)
        coEvery { transportMetadataRepository.timeZone() } returns timeZone

        // Act
        val result = useCase.invoke(liveInformationUrl, routeId, serviceId, tripId, instant).first()

        // Assert
        val updatedTimetable = result.item
        assertEquals(1, updatedTimetable.stops.size)
        assertEquals(sId, updatedTimetable.stops[0].stopId)
        assertNotNull(updatedTimetable.stops[0].stationTime)
        assertIs<StationTime.Live>(updatedTimetable.stops[0].stationTime!!.arrival)
        assertEquals(36020, updatedTimetable.stops[0].stationTime!!.arrival.time.durationThroughDay.inWholeSeconds)
        assertEquals(20, (updatedTimetable.stops[0].stationTime!!.arrival as StationTime.Live).delay.inWholeSeconds)
    }

    @Test
    fun `invoke should merge real-time updates with scheduled times when stop delay provided`() = runTest {
        // Arrange
        val instant = Instant.parse("2024-01-05T12:00:00Z")
        val liveInformationUrl = "http://realtime.url"
        val routeId = "route-1"
        val serviceId = "service-1"
        val tripId = "trip-1"
        val sId = "stop-1"

        val scheduledTimetable = RouteTripTimetable(
            trip = RouteTripInformation(
                startTime = null,
                endTime = null,
                accessibility = RouteServiceAccessibility(
                    ServiceBikesAllowed.ALLOWED,
                    ServiceWheelchairAccessible.ACCESSIBLE
                ),
                heading = "North",
                stops = listOf(
                    RouteTripStop(
                        stopId = sId,
                        arrivalTime = Time.parse("PT10H").addReference(scheduleStartOfDay),
                        departureTime = Time.parse("PT10H1M").addReference(scheduleStartOfDay),
                        sequence = 1,
                        stop = null
                    )
                )
            )
        )

        val feedMessage = mockk<FeedMessage> {
            every { entity } returns listOf(
                mockk {
                    every { isDeleted } returns false
                    every { tripUpdate } returns mockk {
                        every { trip.tripId } returns tripId
                        every { delay } returns 10
                        every { stopTimeUpdate } returns listOf(
                            mockk {
                                every { stopId } returns sId
                                every { arrival } returns mockk {
                                    every { delay } returns 30
                                    every { time } returns null
                                }
                                every { departure } returns null
                            }
                        )
                    }
                }
            )
        }

        coEvery { routeRepository.tripTimetable(routeId, serviceId, tripId, any()) } returns Cachable.live(scheduledTimetable)
        coEvery { transportMetadataRepository.scheduleStartOfDay() } returns scheduleStartOfDay
        coEvery { liveServiceRepository.getRealtimeUpdates(liveInformationUrl) } returns flowOf(feedMessage)
        coEvery { transportMetadataRepository.timeZone() } returns timeZone

        // Act
        val result = useCase.invoke(liveInformationUrl, routeId, serviceId, tripId, instant).first()

        // Assert
        val updatedTimetable = result.item
        assertEquals(1, updatedTimetable.stops.size)
        assertEquals(sId, updatedTimetable.stops[0].stopId)
        assertNotNull(updatedTimetable.stops[0].stationTime)
        assertIs<StationTime.Live>(updatedTimetable.stops[0].stationTime!!.arrival)
        assertEquals(36030, updatedTimetable.stops[0].stationTime!!.arrival.time.durationThroughDay.inWholeSeconds)
        assertEquals(30, (updatedTimetable.stops[0].stationTime!!.arrival as StationTime.Live).delay.inWholeSeconds)
    }

    @Test
    fun `invoke emits scheduled timetable on error`() = runTest {
        val instant = Instant.parse("2024-01-05T12:00:00Z")

        // Arrange
        val liveInformationUrl = "http://realtime.url"
        val routeId = "route-1"
        val serviceId = "service-1"
        val tripId = "trip-1"
        val sId = "stop-1"

        val scheduledTimetable = RouteTripTimetable(
            trip = RouteTripInformation(
                startTime = null,
                endTime = null,
                accessibility = RouteServiceAccessibility(
                    ServiceBikesAllowed.ALLOWED,
                    ServiceWheelchairAccessible.ACCESSIBLE
                ),
                heading = "North",
                stops = listOf(
                    RouteTripStop(
                        stopId = sId,
                        arrivalTime = Time.parse("PT10H").addReference(scheduleStartOfDay),
                        departureTime = Time.parse("PT10H1M").addReference(scheduleStartOfDay),
                        sequence = 1,
                        stop = null
                    )
                )
            )
        )

        coEvery { routeRepository.tripTimetable(routeId, serviceId, tripId, any()) } returns Cachable.live(scheduledTimetable)
        coEvery { transportMetadataRepository.scheduleStartOfDay() } returns scheduleStartOfDay
        coEvery { liveServiceRepository.getRealtimeUpdates(liveInformationUrl) } returns flow { throw Exception("Network error") }
        coEvery { transportMetadataRepository.timeZone() } returns timeZone

        // Act
        val result = useCase.invoke(liveInformationUrl, routeId, serviceId, tripId, instant).first()

        // Assert
        val updatedTimetable = result.item
        assertEquals(1, updatedTimetable.stops.size)
        assertEquals(sId, updatedTimetable.stops[0].stopId)
        assertNotNull(updatedTimetable.stops[0].stationTime)
        assertIs<StationTime.Scheduled>(updatedTimetable.stops[0].stationTime!!.arrival)
        assertEquals(36000, updatedTimetable.stops[0].stationTime!!.arrival.time.durationThroughDay.inWholeSeconds)
    }
}
