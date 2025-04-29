package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import io.mockk.*
import kotlin.time.Duration.Companion.days

class CleanupWorkerTest {

    private lateinit var shaRepository: ShaRepository
    private lateinit var clock: Clock
    private lateinit var worker: CleanupWorker

    private val now = Instant.parse("2024-04-29T12:00:00Z")
    private val oldInstant = now.minus(4.days)
    private val recentInstant = now.minus(1.days)

    private val cacheCategory = CacheCategory.STOP

    @BeforeTest
    fun setup() {
        shaRepository = mockk(relaxed = true)
        clock = mockk()

        worker = object : CleanupWorker() {
            override val shaRepository = this@CleanupWorkerTest.shaRepository
            override val clock = this@CleanupWorkerTest.clock
            override val cacheCategory = this@CleanupWorkerTest.cacheCategory

            override suspend fun delete(resource: ResourceKey) {
                // just mock behavior
            }
        }
    }

    @Test
    fun `invokes delete and remove for old items only`() = runTest {
        val oldResource = "old"
        val recentResource = "recent"

        val cachedItems = listOf(
            CachedResource(oldResource, oldInstant),
            CachedResource(recentResource, recentInstant)
        )

        every { clock.now() } returns now
        coEvery { shaRepository.cached(cacheCategory) } returns cachedItems
        coEvery { shaRepository.remove(cacheCategory, any()) } just Runs

        val deleteSpy = spyk(worker)
        coEvery { deleteSpy.delete(any()) } just Runs

        deleteSpy()

        coVerify(exactly = 1) { deleteSpy.delete(oldResource) }
        coVerify(exactly = 1) { shaRepository.remove(cacheCategory, oldResource) }
        coVerify(exactly = 0) { deleteSpy.delete(recentResource) }
        coVerify(exactly = 0) { shaRepository.remove(cacheCategory, recentResource) }
    }

    @Test
    fun `does not delete anything if all items are recent`() = runTest {
        val recentResource = "recent"

        val cachedItems = listOf(
            CachedResource(recentResource, recentInstant)
        )

        every { clock.now() } returns now
        coEvery { shaRepository.cached(cacheCategory) } returns cachedItems

        val deleteSpy = spyk(worker)
        coEvery { deleteSpy.delete(any()) } just Runs

        deleteSpy()

        coVerify(exactly = 0) { deleteSpy.delete(any()) }
        coVerify(exactly = 0) { shaRepository.remove(cacheCategory, any()) }
    }

    @Test
    fun `deletes multiple old items`() = runTest {
        val oldResource1 = "old1"
        val oldResource2 = "old2"

        val cachedItems = listOf(
            CachedResource(oldResource1, oldInstant),
            CachedResource(oldResource2, oldInstant)
        )

        every { clock.now() } returns now
        coEvery { shaRepository.cached(cacheCategory) } returns cachedItems
        coEvery { shaRepository.remove(cacheCategory, any()) } just Runs

        val deleteSpy = spyk(worker)
        coEvery { deleteSpy.delete(any()) } just Runs

        deleteSpy()

        coVerify(exactly = 1) { deleteSpy.delete(oldResource1) }
        coVerify(exactly = 1) { deleteSpy.delete(oldResource2) }
        coVerify(exactly = 1) { shaRepository.remove(cacheCategory, oldResource1) }
        coVerify(exactly = 1) { shaRepository.remove(cacheCategory, oldResource2) }
    }

    @Test
    fun `respects custom deleteTime`() = runTest {
        val customWorker = object : CleanupWorker() {
            override val shaRepository = this@CleanupWorkerTest.shaRepository
            override val clock = this@CleanupWorkerTest.clock
            override val cacheCategory = this@CleanupWorkerTest.cacheCategory
            override val deleteTime = 1.days

            override suspend fun delete(resource: ResourceKey) {}
        }

        val oldEnoughResource = "oldEnough"

        val cachedItems = listOf(
            CachedResource(oldEnoughResource, now.minus(2.days))
        )

        every { clock.now() } returns now
        coEvery { shaRepository.cached(cacheCategory) } returns cachedItems
        coEvery { shaRepository.remove(cacheCategory, any()) } just Runs

        val deleteSpy = spyk(customWorker)
        coEvery { deleteSpy.delete(any()) } just Runs

        deleteSpy()

        coVerify { deleteSpy.delete(oldEnoughResource) }
        coVerify { shaRepository.remove(cacheCategory, oldEnoughResource) }
    }
}
