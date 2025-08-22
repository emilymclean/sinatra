package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteType
import cl.emilym.sinatra.data.models.RouteVisibility
import cl.emilym.sinatra.data.repository.RouteRepository
import coil3.util.CoilUtils.result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DisplayRoutesUseCaseTest {

    private val getFilteredRoutesUseCase = mockk<GetFilteredRoutesUseCase>()
    private val displayRoutesUseCase = DisplayRoutesUseCase(getFilteredRoutesUseCase)

    @Test
    fun `should return empty list when routes are empty`() = runBlocking {
        // Arrange
        coEvery { getFilteredRoutesUseCase() } returns flowOf(Cachable.live(emptyList()))

        // Act
        val result = displayRoutesUseCase().first()

        // Assert
        assertEquals(Cachable.live(emptyList<Route>()), result)
        verify(exactly = 1) { getFilteredRoutesUseCase.invoke() }
    }

    @Test
    fun `should handle failure from routes call`() = runBlocking {
        // Arrange
        coEvery { getFilteredRoutesUseCase() } throws Exception("Failed to fetch routes")

        // Act
        assertFailsWith<Exception>("Failed to fetch routes") {
            val result = displayRoutesUseCase()
        }

        // Assert
        coVerify(exactly = 1) { getFilteredRoutesUseCase.invoke() }
    }
}