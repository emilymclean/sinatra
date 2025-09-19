package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopVisibility
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.GetFilteredStopsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StopTypeSearcherTest {

    private lateinit var getFilteredStopsUseCase: GetFilteredStopsUseCase
    private lateinit var stopTypeSearcher: StopTypeSearcher

    @BeforeTest
    fun setUp() {
        getFilteredStopsUseCase = mockk()
        stopTypeSearcher = StopTypeSearcher(getFilteredStopsUseCase)
    }

    @Test
    fun `invoke should return empty list when repository returns no items`() = runBlocking {
        // Arrange
        val tokens = listOf("stop")
        coEvery { getFilteredStopsUseCase() } returns flowOf(Cachable.live(emptyList()))

        // Act
        val result = stopTypeSearcher(tokens)

        // Assert
        assertEquals(emptyList(), result)
        verify { getFilteredStopsUseCase() }
    }

    @Test
    fun `scoreMultiplier should return 0_75 for stop with parent station`() {
        // Arrange
        val stop = Stop("123", "parent123", "Main Street", null, mockk(), mockk(), StopVisibility(false, true, false, null), false, false)

        // Act
        val multiplier = stopTypeSearcher.scoreMultiplier(stop)

        // Assert
        assertEquals(0.75, multiplier)
    }

    @Test
    fun `scoreMultiplier should return 1_0 for stop without parent station`() {
        // Arrange
        val stop = Stop("123", null, "Main Street", null, mockk(), mockk(), StopVisibility(false, true, false, null), false, false)

        // Act
        val multiplier = stopTypeSearcher.scoreMultiplier(stop)

        // Assert
        assertEquals(1.0, multiplier)
    }

    @Test
    fun `wrap should return SearchResult with StopResult`() {
        // Arrange
        val stop = Stop("123", null, "Main Street", "null", mockk(), mockk(), StopVisibility(false, true, false, null), false, false)

        // Act
        val result = stopTypeSearcher.wrap(stop)

        // Assert
        assert(result is SearchResult.StopResult)
        assertEquals(stop, (result as SearchResult.StopResult).stop)
    }
}
