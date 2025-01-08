package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.StopRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StopTypeSearcherTest {

    private lateinit var stopRepository: StopRepository
    private lateinit var stopTypeSearcher: StopTypeSearcher

    @BeforeTest
    fun setUp() {
        stopRepository = mockk()
        stopTypeSearcher = StopTypeSearcher(stopRepository)
    }

    @Test
    fun `invoke should return empty list when repository returns no items`() = runBlocking {
        // Arrange
        val tokens = listOf("stop")
        coEvery { stopRepository.stops() } returns mockk {
            every { item } returns emptyList()
        }

        // Act
        val result = stopTypeSearcher(tokens)

        // Assert
        assertEquals(emptyList(), result)
        coVerify { stopRepository.stops() }
    }

    @Test
    fun `scoreMultiplier should return 0_75 for stop with parent station`() {
        // Arrange
        val stop = Stop("123", "parent123", "Main Street", mockk(), mockk())

        // Act
        val multiplier = stopTypeSearcher.scoreMultiplier(stop)

        // Assert
        assertEquals(0.75, multiplier)
    }

    @Test
    fun `scoreMultiplier should return 1_0 for stop without parent station`() {
        // Arrange
        val stop = Stop("123", null, "Main Street", mockk(), mockk())

        // Act
        val multiplier = stopTypeSearcher.scoreMultiplier(stop)

        // Assert
        assertEquals(1.0, multiplier)
    }

    @Test
    fun `wrap should return SearchResult with StopResult`() {
        // Arrange
        val stop = Stop("123", null, "Main Street", mockk(), mockk())

        // Act
        val result = stopTypeSearcher.wrap(stop)

        // Assert
        assert(result is SearchResult.StopResult)
        assertEquals(stop, (result as SearchResult.StopResult).stop)
    }
}
