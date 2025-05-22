package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.AlertSeverity
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteType
import cl.emilym.sinatra.data.models.RouteVisibility
import com.google.transit.realtime.EntitySelector
import com.google.transit.realtime.FeedEntity
import com.google.transit.realtime.FeedMessage
import com.google.transit.realtime.TripDescriptor
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import pbandk.decodeFromStream
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AlertRepositoryTest {

    private val liveServiceRepository = mockk<LiveServiceRepository>()
    private val routeRepository = mockk<RouteRepository>()
    private val contentRepository = mockk<ContentRepository>()
    private val remoteConfigRepository = mockk<RemoteConfigRepository>()

    private val repository = AlertRepository(
        liveServiceRepository,
        routeRepository,
        contentRepository,
        remoteConfigRepository
    )

    @Test
    fun `alerts emits banner Act no GTFS alerts enabled`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.feature(AlertRepository.GTFS_ALERTS_ENABLED, false) } returns false
        coEvery { contentRepository.banner(ContentRepository.HOME_BANNER_ID) } returns Alert.Content(
            "title",
            "message",
            AlertSeverity.SEVERE,
            null
        )

        // Act
        val result = repository.alerts().first()

        // Assert
        assertEquals(1, result.size)
        val banner = result.first()
        assertTrue(banner is Alert.Content)
    }

    @Test
    fun `alerts emits realtime alerts Act GTFS alerts enabled`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.feature(AlertRepository.GTFS_ALERTS_ENABLED, false) } returns true
        coEvery { contentRepository.banner(ContentRepository.HOME_BANNER_ID) } returns null

        coEvery { routeRepository.routes() } returns Cachable.live(listOf(
            Route("ACTO01", "R1", "R1", null, "R1", null, "https://fake.url", false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)
        ))

        val testFeed = mockk<FeedMessage>()
        every { testFeed.entity } returns listOf(
            FeedEntity(
                "testAlert",
                alert = com.google.transit.realtime.Alert(
                    informedEntity = listOf(
                        EntitySelector(
                            agencyId = "TC"
                        )
                    )
                )
            )
        )

        coEvery { liveServiceRepository.getMultipleRealtimeUpdates(any()) } returns flow {
            emit(listOf(testFeed))
        }

        // Act
        val result = repository.alerts().first()

        println(result)

        // Assert
        assertTrue(result.isNotEmpty())
        assertIs<Alert.Realtime>(result.first())
    }

    @Test
    fun `alerts filters alerts by trip`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.feature(AlertRepository.GTFS_ALERTS_ENABLED, false) } returns true

        val tripId = "263"

        coEvery { contentRepository.banner(ContentRepository.HOME_BANNER_ID) } returns null

        coEvery { routeRepository.routes() } returns Cachable.live(listOf(
            Route("ACTO01", "R1", "R1", null, "R1", null, "https://fake.url", false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)
        ))

        val testFeed = mockk<FeedMessage>()
        every { testFeed.entity } returns listOf(
            FeedEntity(
                "testAlert",
                alert = com.google.transit.realtime.Alert(
                    informedEntity = listOf(
                        EntitySelector(
                            trip = TripDescriptor(
                                tripId = tripId
                            )
                        )
                    )
                )
            ),
            FeedEntity(
                "testAlert2",
                alert = com.google.transit.realtime.Alert(
                    informedEntity = listOf(
                        EntitySelector(
                            routeId = "ACTO01"
                        )
                    )
                )
            )
        )
        coEvery { liveServiceRepository.getMultipleRealtimeUpdates(any()) } returns flow {
            emit(listOf(testFeed))
        }

        // Act
        val result = repository.alerts(tripId = tripId).first()

        // Assert
        assertEquals(1, result.size)
        val realtimeAlert = result.first()
        assertIs<Alert.Realtime>(realtimeAlert)
    }

    @Test
    fun `alerts filters alerts by route`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.feature(AlertRepository.GTFS_ALERTS_ENABLED, false) } returns true

        val routeId = "ACTO01"

        coEvery { contentRepository.banner(ContentRepository.HOME_BANNER_ID) } returns null

        coEvery { routeRepository.route(routeId = routeId) } returns Cachable.live(
            Route("ACTO01", "R1", "R1", null, "R1", null, "https://fake.url", false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)
        )

        val testFeed = mockk<FeedMessage>()
        every { testFeed.entity } returns listOf(
            FeedEntity(
                "testAlert",
                alert = com.google.transit.realtime.Alert(
                    informedEntity = listOf(
                        EntitySelector(
                            trip = TripDescriptor(
                                tripId = "12313321"
                            )
                        )
                    )
                )
            ),
            FeedEntity(
                "testAlert2",
                alert = com.google.transit.realtime.Alert(
                    informedEntity = listOf(
                        EntitySelector(
                            routeId = routeId
                        )
                    )
                )
            )
        )
        coEvery { liveServiceRepository.getMultipleRealtimeUpdates(any()) } returns flow {
            emit(listOf(testFeed))
        }

        // Act
        val result = repository.alerts(routeId = routeId).first()

        // Assert
        assertEquals(1, result.size)
        val realtimeAlert = result.first()
        assertIs<Alert.Realtime>(realtimeAlert)
    }

    @Test
    fun `alerts filters alerts by stop`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.feature(AlertRepository.GTFS_ALERTS_ENABLED, false) } returns true

        val stopId = "stop1"

        coEvery { contentRepository.banner(ContentRepository.HOME_BANNER_ID) } returns null
        coEvery { routeRepository.routes() } returns Cachable.live(listOf(
            Route("ACTO01", "R1", "R1", null, "R1", null, "https://fake.url", false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)
        ))

        val testFeed = mockk<FeedMessage>()
        every { testFeed.entity } returns listOf(
            FeedEntity(
                "testAlert",
                alert = com.google.transit.realtime.Alert(
                    informedEntity = listOf(
                        EntitySelector(
                            trip = TripDescriptor(
                                tripId = "12313321"
                            )
                        )
                    )
                )
            ),
            FeedEntity(
                "testAlert2",
                alert = com.google.transit.realtime.Alert(
                    informedEntity = listOf(
                        EntitySelector(
                            stopId = stopId
                        )
                    )
                )
            )
        )
        coEvery { liveServiceRepository.getMultipleRealtimeUpdates(any()) } returns flow {
            emit(listOf(testFeed))
        }

        // Act
        val result = repository.alerts(stopId = stopId).first()

        // Assert
        assertEquals(1, result.size)
        val realtimeAlert = result.first()
        assertIs<Alert.Realtime>(realtimeAlert)
    }
}
