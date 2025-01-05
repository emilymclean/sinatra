package cl.emilym.sinatra.data.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheState
import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.domain.ActiveServicesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActiveServicesUseCaseTest {

    private val serviceRepository = mockk<ServiceRepository>()
    private val clock = mockk<Clock>()
    private val metadataRepository = mockk<TransportMetadataRepository>()
    private val useCase = ActiveServicesUseCase(serviceRepository, clock, metadataRepository)

    @Test
    fun `should filter active services`() = runTest {
        // Arrange
        val instant = Instant.parse("2024-01-05T12:00:00Z")
        every { clock.now() } returns instant
        coEvery { metadataRepository.timeZone() } returns TimeZone.UTC

        val activeService = mockk<Service> {
            every { active(instant, TimeZone.UTC) } returns true
        }
        val inactiveService = mockk<Service> {
            every { active(instant, TimeZone.UTC) } returns false
        }
        val services = listOf(activeService, inactiveService)
        coEvery { serviceRepository.services() } returns Cachable(services, CacheState.LIVE)

        // Act
        val result = useCase.invoke(instant)

        // Assert
        assertTrue(result is Cachable<List<Service>>)
        assertEquals(1, result.item.size)
        assertTrue(result.item.contains(activeService))
    }

    @Test
    fun `should return empty list when no services are active`() = runTest {
        // Arrange
        val instant = Instant.parse("2024-01-05T12:00:00Z")
        every { clock.now() } returns instant
        coEvery { metadataRepository.timeZone() } returns TimeZone.UTC

        val inactiveService = mockk<Service> {
            every { active(instant, TimeZone.UTC) } returns false
        }
        val services = listOf(inactiveService)
        coEvery { serviceRepository.services() } returns Cachable(services, CacheState.LIVE)

        // Act
        val result = useCase.invoke(instant)

        // Assert
        assertTrue(result is Cachable<List<Service>>)
        assertTrue(result.item.isEmpty())
    }

    @Test
    fun `should use current time when no instant is provided`() = runTest {
        // Arrange
        val instant = Instant.parse("2024-01-05T12:00:00Z")
        every { clock.now() } returns instant
        coEvery { metadataRepository.timeZone() } returns TimeZone.UTC

        val activeService = mockk<Service> {
            every { active(instant, TimeZone.UTC) } returns true
        }
        val services = listOf(activeService)
        coEvery { serviceRepository.services() } returns Cachable(services, CacheState.LIVE)

        // Act
        val result = useCase.invoke()

        // Assert
        assertTrue(result is Cachable<List<Service>>)
        assertEquals(1, result.item.size)
        assertTrue(result.item.contains(activeService))
    }
}