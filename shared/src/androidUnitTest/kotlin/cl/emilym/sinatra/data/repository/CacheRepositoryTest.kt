package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.CacheClient
import cl.emilym.sinatra.data.persistence.CachePersistence
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CacheRepositoryTest {

    private lateinit var cacheClient: CacheClient
    private lateinit var cachePersistence: CachePersistence
    private lateinit var repository: CacheRepository

    @BeforeTest
    fun setup() {
        cacheClient = mockk()
        cachePersistence = mockk()
        repository = CacheRepository(cacheClient, cachePersistence)
    }

    @Test
    fun `shouldInvalidate returns false and saves current if no stored value`() = runTest {
        // Arrange
        coEvery { cacheClient.cacheInvalidationKey() } returns "current_key"
        coEvery { cachePersistence.get() } returns null
        coEvery { cachePersistence.save("current_key") } just Runs

        // Act
        val result = repository.shouldInvalidate()

        // Assert
        assertFalse(result)
        coVerify { cachePersistence.save("current_key") }
    }

    @Test
    fun `shouldInvalidate returns true if stored key is different`() = runTest {
        // Arrange
        coEvery { cacheClient.cacheInvalidationKey() } returns "new_key"
        coEvery { cachePersistence.get() } returns "old_key"
        coEvery { cachePersistence.save("new_key") } just Runs

        // Act
        val result = repository.shouldInvalidate()

        // Assert
        assertTrue(result)
        coVerify { cachePersistence.save("new_key") }
    }

    @Test
    fun `shouldInvalidate returns false if stored key is the same`() = runTest {
        // Arrange
        coEvery { cacheClient.cacheInvalidationKey() } returns "same_key"
        coEvery { cachePersistence.get() } returns "same_key"

        // No save should happen
        // (We could also allow it and not assert if you prefer)
        coEvery { cachePersistence.save(any()) } just Runs

        // Act
        val result = repository.shouldInvalidate()

        // Assert
        assertFalse(result)
        coVerify(exactly = 0) { cachePersistence.save(any()) }
    }

    @Test
    fun `shouldInvalidate returns false Act cacheClient throws exception`() = runTest {
        // Arrange
        coEvery { cacheClient.cacheInvalidationKey() } throws RuntimeException("Failed")
        // No persistence interaction expected

        // Act
        val result = repository.shouldInvalidate()

        // Assert
        assertFalse(result)
        coVerify(exactly = 0) { cachePersistence.save(any()) }
    }
    
}