package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.dto.NominatimPlace
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.network.NominatimApi
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceClientTest {

    private val nominatimApi: NominatimApi = mockk()
    private val remoteConfigRepository: RemoteConfigRepository = mockk()
    private val placeClient = PlaceClient(nominatimApi, remoteConfigRepository)

    @Test
    fun `search returns empty list if nominatimUrl is null`() = runTest {
        coEvery { remoteConfigRepository.nominatimUrl() } returns null

        val result = placeClient.search("query")

        assertEquals(emptyList(), result)
        coVerify(exactly = 0) { nominatimApi.search(any(), any(), any(), any()) }
    }

    @Test
    fun `search filters out station types and removed categories`() = runTest {
        val baseUrl = "example.com"
        val userAgent = "test-agent"
        val email = "test@example.com"

        coEvery { remoteConfigRepository.nominatimUrl() } returns baseUrl
        coEvery { remoteConfigRepository.nominatimUserAgent() } returns userAgent
        coEvery { remoteConfigRepository.nominatimEmail() } returns email

        val response = listOf(
            NominatimPlace(
                placeId = 1L,
                lat = "10.0",
                lon = "20.0",
                name = "Valid Place",
                _displayName = "Valid Display",
                type = "cafe", // allowed
                category = "amenity",
                address = mapOf("place" to "Park", "suburb" to "Suburb", "postcode" to "1234")
            ),
            NominatimPlace(
                placeId = 2L,
                lat = "15.0",
                lon = "25.0",
                name = "Bus Stop",
                _displayName = "Bus Stop Display",
                type = "bus_stop", // should be filtered out
                category = "amenity"
            )
        )

        coEvery {
            nominatimApi.search(
                "https://$baseUrl/search",
                "query",
                userAgent = userAgent,
                email = email
            )
        } returns response

        val result = placeClient.search("query")

        assertEquals(1, result.size)
        assertEquals("Valid Place", result.first().name)
        assertEquals("Park, Suburb, 1234", result.first().displayName)
    }

    @Test
    fun `search deduplicates by display name`() = runTest {
        val baseUrl = "example.com"
        val userAgent = "test-agent"
        val email = "test@example.com"

        coEvery { remoteConfigRepository.nominatimUrl() } returns baseUrl
        coEvery { remoteConfigRepository.nominatimUserAgent() } returns userAgent
        coEvery { remoteConfigRepository.nominatimEmail() } returns email

        val response = listOf(
            NominatimPlace(
                placeId = 1L,
                lat = "10.0",
                lon = "20.0",
                name = "Place 1",
                _displayName = "Same Display Name",
                type = "cafe",
                category = "amenity"
            ),
            NominatimPlace(
                placeId = 2L,
                lat = "11.0",
                lon = "21.0",
                name = "Place 2",
                _displayName = "Same Display Name",
                type = "cafe",
                category = "amenity"
            )
        )

        coEvery {
            nominatimApi.search(
                "https://$baseUrl/search",
                "query",
                userAgent = userAgent,
                email = email
            )
        } returns response

        val result = placeClient.search("query")

        assertEquals(1, result.size)
        assertEquals("Same Display Name", result.first().displayName)
    }

}
