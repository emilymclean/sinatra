package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.models.ServiceAlertRegion
import cl.emilym.sinatra.room.dao.ServiceAlertDao
import cl.emilym.sinatra.room.entities.ServiceAlertEntity
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ServiceAlertPersistenceTest {

    private val serviceAlertDao = mockk<ServiceAlertDao>(relaxed = true)
    private val persistence = ServiceAlertPersistence(serviceAlertDao)

    @Test
    fun `save should clear, transform and insert alerts preserving viewed state`() = runTest {
        // Arrange
        val viewedIds = listOf("1", "3")
        coEvery { serviceAlertDao.getViewed() } returns viewedIds
        coEvery { serviceAlertDao.clear() } just Runs
        coEvery { serviceAlertDao.insert(*anyVararg()) } just Runs

        val alerts = listOf(
            ServiceAlert(
                id = "1",
                title = "Alert 1",
                url = null,
                date = null,
                regions = listOf(ServiceAlertRegion.BELCONNEN),
                highlightDuration = null
            ),
            ServiceAlert(
                id = "2",
                title = "Alert 2",
                url = "http://example.com",
                date = null,
                regions = listOf(ServiceAlertRegion.CENTRAL_CANBERRA),
                highlightDuration = null
            )
        )

        // Act
        persistence.save(alerts)

        // Assert
        coVerifySequence {
            serviceAlertDao.getViewed()
            serviceAlertDao.clear()
            serviceAlertDao.insert(
                withArg { firstEntity ->
                    assertEquals("1", firstEntity.id)
                    assertEquals(true, firstEntity.viewed)
                },
                withArg { secondEntity ->
                    assertEquals("2", secondEntity.id)
                    assertEquals(false, secondEntity.viewed)
                }
            )
        }
    }
}
