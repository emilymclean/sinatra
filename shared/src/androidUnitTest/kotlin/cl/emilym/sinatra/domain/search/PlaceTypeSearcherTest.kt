package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.repository.PlaceRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PlaceTypeSearcherTest {

    private lateinit var placeRepository: PlaceRepository
    private lateinit var placeTypeSearcher: PlaceTypeSearcher

    @BeforeTest
    fun setUp() {
        placeRepository = mockk()
        placeTypeSearcher = PlaceTypeSearcher(placeRepository)
    }

    @Test
    fun `invoke should return empty list when token length is less than MIN_QUERY_LENGTH`() = runBlocking {
        // Arrange
        val tokens = listOf("abc")

        // Act
        val result = placeTypeSearcher(tokens)

        // Assert
        assertEquals(emptyList(), result)
    }

    @Test
    fun `invoke should return empty list when repository is unavailable`() = runTest {
        // Arrange
        val tokens = listOf("test")
        coEvery { placeRepository.available() } returns false

        // Act
        val result = placeTypeSearcher(tokens)

        // Assert
        assertEquals(emptyList(), result)
        coVerify { placeRepository.available() }
    }

    @Test
    fun `invoke should call search and return results`() = runTest {
        // Arrange
        val tokens = listOf("test")
        val place = Place("1", "Test Place", "Test Display Name", mockk())
        val searchSpace = listOf(place)

        coEvery { placeRepository.available() } returns true
        coEvery { placeRepository.search("test") } returns searchSpace

        // Act
        val result = placeTypeSearcher(tokens)

        // Assert
        assertEquals(1, result.size)
        assertEquals(place, result.first().result)
        coVerify { placeRepository.available() }
        coVerify { placeRepository.search("test") }
    }

    @Test
    fun `wrap should return SearchResult with PlaceResult`() {
        // Arrange
        val place = Place("1", "Test Place", "Test Display Name", mockk())

        // Act
        val result = placeTypeSearcher.wrap(place)

        // Assert
        assert(result is SearchResult.PlaceResult)
        assertEquals(place, (result as SearchResult.PlaceResult).place)
    }
}
