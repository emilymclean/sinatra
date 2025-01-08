package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.ColorPair
import cl.emilym.sinatra.data.models.OnColor
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteType
import cl.emilym.sinatra.data.repository.RouteRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RouteTypeSearcherTest {

    private lateinit var routeRepository: RouteRepository
    private lateinit var routeTypeSearcher: RouteTypeSearcher

    @BeforeTest
    fun setUp() {
        routeRepository = mockk()
        routeTypeSearcher = RouteTypeSearcher(routeRepository)
    }

    @Test
    fun `scoreMultiplier should return 1_7 when colors is not null`() {
        // Arrange
        val route = Route("1", "Route Name", "Display Code", ColorPair("FFFFFF", OnColor.LIGHT), "Test", null, RouteType.BUS, null)

        // Act
        val multiplier = routeTypeSearcher.scoreMultiplier(route)

        // Assert
        assertEquals(1.7, multiplier)
    }

    @Test
    fun `scoreMultiplier should return 0_2 when name is NIS`() {
        // Arrange
        val route = Route("1", "Route Name", "Display Code", null, "NIS", null, RouteType.BUS, null)

        // Act
        val multiplier = routeTypeSearcher.scoreMultiplier(route)

        // Assert
        assertEquals(0.2, multiplier)
    }

    @Test
    fun `scoreMultiplier should return 1_1 for other cases`() {
        // Arrange
        val route = Route("1", "Route Name", "Display Code", null, "Test", null, RouteType.BUS, null)

        // Act
        val multiplier = routeTypeSearcher.scoreMultiplier(route)

        // Assert
        assertEquals(1.1, multiplier)
    }

    @Test
    fun `wrap should return SearchResult with PlaceResult`() {
        // Arrange
        val route = Route("1", "Route Name", "Display Code", null, "Test", null, RouteType.BUS, null)

        // Act
        val result = routeTypeSearcher.wrap(route)

        // Assert
        assert(result is SearchResult.RouteResult)
        assertEquals(route, (result as SearchResult.RouteResult).route)
    }
}
