package cl.emilym.sinatra.domain

import cl.emilym.sinatra.BuildInformation
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheState
import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IsAboveMinimumVersionUseCaseTest {

    private val remoteConfigRepository = mockk<RemoteConfigRepository>()
    private val buildInformation = mockk<BuildInformation>()
    private val useCase = IsAboveMinimumVersionUseCase(remoteConfigRepository, buildInformation)

    @Test
    fun `should return true when minimum version null`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.minimumVersion() } returns null

        // Act
        val result = useCase.invoke()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `should return true when minimum version throws exception`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.minimumVersion() } throws Exception()

        // Act
        val result = useCase.invoke()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `should return true when current major version greater than minimum major version`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.minimumVersion() } returns "1.0.0"
        every { buildInformation.versionName } returns "2.0.0"

        // Act
        val result = useCase.invoke()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `should return true when current major version equals minimum major version`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.minimumVersion() } returns "1.0.0"
        every { buildInformation.versionName } returns "1.0.0"

        // Act
        val result = useCase.invoke()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `should return false when current major version less than minimum major version`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.minimumVersion() } returns "1.0.0"
        every { buildInformation.versionName } returns "0.0.0"

        // Act
        val result = useCase.invoke()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `should return true when current minor version greater than minimum minor version`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.minimumVersion() } returns "1.0.0"
        every { buildInformation.versionName } returns "1.1.0"

        // Act
        val result = useCase.invoke()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `should return false when current minor version greater than minimum minor version`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.minimumVersion() } returns "1.1.0"
        every { buildInformation.versionName } returns "1.0.0"

        // Act
        val result = useCase.invoke()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `should return false when minor versions match but majors are lower`() = runTest {
        // Arrange
        coEvery { remoteConfigRepository.minimumVersion() } returns "1.1.0"
        every { buildInformation.versionName } returns "0.1.0"

        // Act
        val result = useCase.invoke()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `should successfully parse version with alpha`() = runTest {
        // Act
        val result = with(useCase) { "1.0.0-alpha.0".parse }

        // Assert
        assertEquals(result, listOf(1,0,0))
    }

}