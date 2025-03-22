package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.EndpointDigestPair
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.CacheState
import cl.emilym.sinatra.data.models.ResourceKey
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.hours

class BaseCacheWorkerTest {

    private val mockShaRepository = mockk<ShaRepository>(relaxed = true)
    private val mockRemoteConfigRepository = mockk<RemoteConfigRepository>(relaxed = true) {
        coEvery { dataCachePeriodMultiplier() } returns 1.0
    }
    private val mockCacheWorkerLockProvider = mockk<CacheWorkerLockProvider>(relaxed = true)
    private val mockLock = Mutex()

    private val _cacheWorkerDependencies = CacheWorkerDependencies(
        shaRepository = mockShaRepository,
        remoteConfigRepository = mockRemoteConfigRepository,
        cacheWorkerLockProvider = mockCacheWorkerLockProvider,
    )

    private val testClock = mockk<Clock>() {
        every { now() } returns Instant.parse("2024-01-01T00:00:00Z")
    }

    private val testCacheCategory = CacheCategory.ROUTE

    private val testWorker = object : BaseCacheWorker<String, Unit>() {
        override val cacheWorkerDependencies: CacheWorkerDependencies = _cacheWorkerDependencies
        override val clock: Clock = testClock
        override val cacheCategory: CacheCategory = testCacheCategory

        suspend operator fun invoke(pair: EndpointDigestPair<String>, resource: ResourceKey, extras: Unit): Cachable<String> {
            return run(pair, resource, extras)
        }

        override suspend fun saveToPersistence(data: String, resource: ResourceKey) {
            // Mock save operation
        }

        override suspend fun getFromPersistence(resource: ResourceKey, extras: Unit): String? {
            return "cached-data"
        }

        override suspend fun existsInPersistence(resource: ResourceKey): Boolean {
            return true
        }
    }

    @Test
    fun `run should return cached data when update is not required`() = runTest {
        val resourceKey = "test-resource"
        val endpointDigestPair = mockk<EndpointDigestPair<String>>(relaxed = true) {
            coEvery { digest() } returns "digest"
        }

        coEvery { mockShaRepository.cached(testCacheCategory, resourceKey) } returns CacheInformation.Available(
            digest = "digest",
            added = testClock.now().minus(10.hours)
        )

        coEvery { mockCacheWorkerLockProvider.lock(resourceKey) } returns mockLock

        val result = testWorker(endpointDigestPair, resourceKey, Unit)

        assertEquals(CacheState.CACHED, result.state)
        assertEquals("cached-data", result.item)
    }

    @Test
    fun `run should fetch and save new data when digest changes`() = runTest {
        val resourceKey = "test-resource"
        val endpointDigestPair = mockk<EndpointDigestPair<String>>(relaxed = true) {
            coEvery { digest() } returns "new-digest"
            coEvery { endpoint() } returns "new-data"
        }

        coEvery { mockShaRepository.cached(testCacheCategory, resourceKey) } returns CacheInformation.Available(
            digest = "old-digest",
            added = testClock.now().minus(30.hours)
        )

        coEvery { mockCacheWorkerLockProvider.lock(resourceKey) } returns mockLock

        val result = testWorker(endpointDigestPair, resourceKey, Unit)

        assertEquals(CacheState.LIVE, result.state)
        coVerify { mockShaRepository.save("new-digest", testCacheCategory, resourceKey) }
    }

    @Test
    fun `run should return expired data when fetch fails`() = runTest {
        val resourceKey = "test-resource"
        val endpointDigestPair = mockk<EndpointDigestPair<String>>(relaxed = true) {
            coEvery { digest() } returns "new-digest"
            coEvery { endpoint() } throws Exception("Error")
        }

        coEvery { mockShaRepository.cached(testCacheCategory, resourceKey) } returns CacheInformation.Available(
            digest = "old-digest",
            added = testClock.now().minus(30.hours)
        )

        coEvery { mockCacheWorkerLockProvider.lock(resourceKey) } returns mockLock

        val result = testWorker(endpointDigestPair, resourceKey, Unit)

        assertEquals(CacheState.EXPIRED_CACHE, result.state)
    }

    @Test
    fun `run should throw when data cannot be fetched or retrieved from persistence`() = runTest {
        val resourceKey = "test-resource"
        val endpointDigestPair = mockk<EndpointDigestPair<String>>(relaxed = true) {
            coEvery { digest() } throws Exception("Digest error")
        }

        coEvery { mockShaRepository.cached(testCacheCategory, resourceKey) } returns CacheInformation.Unavailable

        coEvery { mockCacheWorkerLockProvider.lock(resourceKey) } returns mockLock

        assertFailsWith<Exception> {
            testWorker(endpointDigestPair, resourceKey, Unit)
        }
    }
}
