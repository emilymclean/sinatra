package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteType
import cl.emilym.sinatra.data.models.RouteVisibility
import cl.emilym.sinatra.data.repository.RouteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DisplayRoutesUseCaseTest {

    private val routeRepository = mockk<RouteRepository>()
    private val displayRoutesUseCase = DisplayRoutesUseCase(routeRepository)

    @Test
    fun `should return filtered and sorted routes`() = runBlocking {
        // Arrange
        val ignoredRoutes = listOf("1", "3")
        val routes = listOf(
            Route("1", "1", "1", null, "R1", null, RouteType.LIGHT_RAIL, null, RouteVisibility(false, null)),
            Route("2", "2", "2", null, "R2", null, RouteType.LIGHT_RAIL, null, RouteVisibility(false, null)),
            Route("3", "3", "3", null, "R3", null, RouteType.LIGHT_RAIL, null, RouteVisibility(false, null)),
            Route("4", "4", "4", null, "R4", null, RouteType.LIGHT_RAIL, null, RouteVisibility(false, null)),
        )
        val expectedFilteredSortedRoutes = listOf(
            Route("2", "2", "2", null, "R2", null, RouteType.LIGHT_RAIL, null, RouteVisibility(false, null)),
            Route("4", "4", "4", null, "R4", null, RouteType.LIGHT_RAIL, null, RouteVisibility(false, null))
        )

        coEvery { routeRepository.routes() } returns Cachable.live(routes)

        // Act
        val result = displayRoutesUseCase()

        // Assert
        assertEquals(Cachable.live(expectedFilteredSortedRoutes), result)
        coVerify(exactly = 1) { routeRepository.routes() }
    }

    @Test
    fun `should return empty list when routes are empty`() = runBlocking {
        // Arrange
        coEvery { routeRepository.routes() } returns Cachable.live(emptyList())

        // Act
        val result = displayRoutesUseCase()

        // Assert
        assertEquals(Cachable.live(emptyList<Route>()), result)
        coVerify(exactly = 1) { routeRepository.routes() }
    }

    @Test
    fun `should handle failure from routes call`() = runBlocking {
        // Arrange
        coEvery { routeRepository.routes() } throws Exception("Failed to fetch routes")

        // Act
        assertFailsWith<Exception>("Failed to fetch routes") {
            val result = displayRoutesUseCase()
        }

        // Assert
        coVerify(exactly = 1) { routeRepository.routes() }
    }
}