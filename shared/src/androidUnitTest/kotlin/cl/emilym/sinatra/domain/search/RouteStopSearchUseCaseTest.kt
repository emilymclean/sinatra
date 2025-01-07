import cl.emilym.sinatra.domain.search.*
import cl.emilym.sinatra.data.models.*
import cl.emilym.sinatra.lib.Tokenizer
import io.github.aakira.napier.Napier
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class RouteStopSearchUseCaseTest {

    private val mockRouteTypeSearcher = mockk<RouteTypeSearcher>()
    private val mockStopTypeSearcher = mockk<StopTypeSearcher>()
    private val mockPlaceTypeSearcher = mockk<PlaceTypeSearcher>()
    private val mockTokenizer = mockk<Tokenizer>()

    private val useCase = RouteStopSearchUseCase(
        routeTypeSearcher = mockRouteTypeSearcher,
        stopTypeSearcher = mockStopTypeSearcher,
        placeTypeSearcher = mockPlaceTypeSearcher,
        tokenizer = mockTokenizer
    )

    @Test
    fun `invoke should return sorted results`() = runBlocking {
        // Arrange
        val query = "central station"
        val tokens = listOf("central", "station")
        val route = Route("1", "R1", "R1", null, "Route 1", null, RouteType.BUS, null)
        val stop = Stop("2", null, "Stop 1", MapLocation(0.0, 0.0), StopAccessibility(StopWheelchairAccessibility.FULL))
        val place = Place("3", "Place 1", "Place 1", MapLocation(0.0, 0.0))

        val routeResult = RankableResult(route, 0.9)
        val stopResult = RankableResult(stop, 0.8)
        val placeResult = RankableResult(place, 0.7)

        coEvery { mockTokenizer.tokenize(query) } returns tokens
        coEvery { mockRouteTypeSearcher(tokens) } returns listOf(routeResult)
        coEvery { mockStopTypeSearcher(tokens) } returns listOf(stopResult)
        coEvery { mockPlaceTypeSearcher(tokens) } returns listOf(placeResult)

        // Act
        val results = useCase(query)

        // Assert
        assertEquals(3, results.size)
        assertTrue(results[0] is SearchResult.RouteResult)
        assertTrue(results[1] is SearchResult.StopResult)
        assertTrue(results[2] is SearchResult.PlaceResult)

        coVerify(exactly = 1) { mockTokenizer.tokenize(query) }
        coVerify(exactly = 1) { mockRouteTypeSearcher(tokens) }
        coVerify(exactly = 1) { mockStopTypeSearcher(tokens) }
        coVerify(exactly = 1) { mockPlaceTypeSearcher(tokens) }
    }

    @Test
    fun `invoke should handle exceptions from searchers gracefully`() = runBlocking {
        // Arrange
        val query = "central station"
        val tokens = listOf("central", "station")
        coEvery { mockTokenizer.tokenize(query) } returns tokens
        coEvery { mockRouteTypeSearcher(tokens) } throws Exception("Route search error")
        coEvery { mockStopTypeSearcher(tokens) } returns emptyList()
        coEvery { mockPlaceTypeSearcher(tokens) } throws Exception("Place search error")

        // Act
        val results = useCase(query)

        // Assert
        assertTrue(results.isEmpty())
        coVerify(exactly = 1) { mockTokenizer.tokenize(query) }
        coVerify(exactly = 1) { mockRouteTypeSearcher(tokens) }
        coVerify(exactly = 1) { mockStopTypeSearcher(tokens) }
        coVerify(exactly = 1) { mockPlaceTypeSearcher(tokens) }
    }
}

class LocalTypeSearcherTest {

    class TestLocalTypeSearcher : LocalTypeSearcher<Place>() {
        private val mockData = listOf(
            Place("1", "Central Station", "Central Station", MapLocation(0.0, 0.0)),
            Place("2", "North Park", "North Park", MapLocation(1.0, 1.0))
        )

        override suspend fun load(): List<Place> = mockData

        override fun fields(t: Place): List<String> = listOf(t.name, t.displayName)

        override fun wrap(item: Place): SearchResult {
            return SearchResult.PlaceResult(item)
        }
    }

    private val searcher = TestLocalTypeSearcher()

    @Test
    fun `invoke should return matching results`() = runBlocking {
        // Arrange
        val tokens = listOf("central", "station")

        // Act
        val results = searcher(tokens)

        // Assert
        assertEquals(1, results.size)
        assertEquals("Central Station", results[0].result.name)
    }
}
