package cl.emilym.sinatra.data.models.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NominatimPlaceTest {

    @Test
    fun `displayName returns _displayName when address is null`() {
        val place = NominatimPlace(
            placeId = 1,
            lat = "0.0",
            lon = "0.0",
            name = "Test Place",
            _displayName = "Raw Display Name",
            type = "place",
            category = "amenity",
            address = null
        )

        assertEquals("Raw Display Name", place.displayName)
    }

    @Test
    fun `displayName returns formatted address when available`() {
        val place = NominatimPlace(
            placeId = 2,
            lat = "0.0",
            lon = "0.0",
            name = "Test Place",
            _displayName = "Raw Display Name",
            type = "place",
            category = "amenity",
            address = mapOf(
                "suburb" to "Test Suburb",
                "place" to "Test Place Marker",
                "road" to "Main St",
                "ISO3166-2-lvl" to "AU-NSW",
                "postcode" to "2000"
            )
        )

        assertEquals(
            "Test Place Marker, Main St, Test Suburb, NSW, 2000",
            place.displayName
        )
    }

    @Test
    fun `dedupeKeys returns place marker and suburb`() {
        val place = NominatimPlace(
            placeId = 3,
            lat = "0.0",
            lon = "0.0",
            name = "Test Place",
            _displayName = "Raw Display Name",
            type = "place",
            category = "amenity",
            address = mapOf(
                "place" to "Test Place Marker",
                "suburb" to "Test Suburb"
            )
        )

        assertEquals(listOf("Test Place Marker", "Test Suburb"), place.dedupeKeys)
    }

    @Test
    fun `dedupeKeys returns only suburb when place marker is missing`() {
        val place = NominatimPlace(
            placeId = 4,
            lat = "0.0",
            lon = "0.0",
            name = "Test Place",
            _displayName = "Raw Display Name",
            type = "place",
            category = "amenity",
            address = mapOf(
                "suburb" to "Only Suburb"
            )
        )

        assertEquals(listOf("Only Suburb"), place.dedupeKeys)
    }

    @Test
    fun `formatted address returns null when suburb is missing`() {
        val place = NominatimPlace(
            placeId = 5,
            lat = "0.0",
            lon = "0.0",
            name = "Test Place",
            _displayName = "Raw Display Name",
            type = "place",
            category = "amenity",
            address = mapOf(
                "place" to "Test Place Marker"
                // No suburb!
            )
        )

        // Should fallback to _displayName because formatted() returns null
        assertEquals("Raw Display Name", place.displayName)
    }

    @Test
    fun `formatted includes street address if available`() {
        val place = NominatimPlace(
            placeId = 6,
            lat = "0.0",
            lon = "0.0",
            name = "Test Place",
            _displayName = "Raw Display Name",
            type = "place",
            category = "amenity",
            address = mapOf(
                "suburb" to "Street Suburb",
                "road" to "First Ave",
                "place" to "Important Place",
                "postcode" to "3000"
            )
        )

        assertEquals(
            "Important Place, First Ave, Street Suburb, 3000",
            place.displayName
        )
    }

    @Test
    fun `state abbreviation mapping works correctly`() {
        val placeACT = NominatimPlace(
            placeId = 7,
            lat = "0.0",
            lon = "0.0",
            name = "Test Place",
            _displayName = "Raw Display Name",
            type = "place",
            category = "amenity",
            address = mapOf(
                "suburb" to "Capital Hill",
                "place" to "Parliament House",
                "ISO3166-2-lvl" to "AU-ACT",
                "postcode" to "2600"
            )
        )

        assertTrue(placeACT.displayName.contains("ACT"))
    }
}
