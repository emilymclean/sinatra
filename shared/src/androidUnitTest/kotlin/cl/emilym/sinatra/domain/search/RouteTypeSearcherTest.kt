package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.ColorPair
import cl.emilym.sinatra.data.models.OnColor
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteType
import cl.emilym.sinatra.data.models.RouteVisibility
import cl.emilym.sinatra.data.repository.RouteRepository
import io.mockk.mockk
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
        val route = Route("1", "R1", "R1", ColorPair("", OnColor.LIGHT), "R1", null, "https://fake.url", false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)

        // Act
        val multiplier = routeTypeSearcher.scoreMultiplier(route)

        // Assert
        assertEquals(1.7, multiplier)
    }

    @Test
    fun `scoreMultiplier should return 0_2 when name is NIS`() {
        // Arrange
        val route = Route("2", "NIS", "NIS", null, "NIS", null, "https://fake.url", false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)

        // Act
        val multiplier = routeTypeSearcher.scoreMultiplier(route)

        // Assert
        assertEquals(0.2, multiplier)
    }

    @Test
    fun `scoreMultiplier should return 1_1 for other cases`() {
        // Arrange
        val route = Route("1", "R1", "R1", null, "R1", null, "https://fake.url", false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)

        // Act
        val multiplier = routeTypeSearcher.scoreMultiplier(route)

        // Assert
        assertEquals(1.1, multiplier)
    }

    @Test
    fun `wrap should return SearchResult with PlaceResult`() {
        // Arrange
        val route = Route("1", "R1", "R1", null, "R1", null, "https://fake.url", false, RouteType.BUS, null, RouteVisibility(false, null, false), false, null)

        // Act
        val result = routeTypeSearcher.wrap(route)

        // Assert
        assert(result is SearchResult.RouteResult)
        assertEquals(route, (result as SearchResult.RouteResult).route)
    }
}
