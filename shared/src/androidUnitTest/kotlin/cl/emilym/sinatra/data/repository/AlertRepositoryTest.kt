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

    private val contentRepository = mockk<ContentRepository>()

    private val repository = AlertRepository(
        contentRepository
    )

    @Test
    fun `alerts emits banner Act no GTFS alerts enabled`() = runTest {
        // Arrange
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

}
