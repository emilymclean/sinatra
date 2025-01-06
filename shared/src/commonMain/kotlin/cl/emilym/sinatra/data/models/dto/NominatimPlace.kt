package cl.emilym.sinatra.data.models.dto

import cl.emilym.sinatra.nullIf
import cl.emilym.sinatra.nullIfEmpty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NominatimPlace(
    @SerialName("place_id")
    val placeId: Long,
    val lat: String,
    val lon: String,
    val name: String,
    @SerialName("display_name")
    private val _displayName: String,
    val type: String,
    val category: String,
    val address: Map<String, String>? = null
) {

    companion object {
        private val PLACE_MARKERS = listOf("emergency", "historic", "military", "natural",
            "landuse", "place", "railway", "man_made", "aerialway", "boundary", "amenity", "aeroway",
            "club", "craft", "leisure", "office", "mountain_pass", "shop", "tourism", "bridge",
            "tunnel", "waterway", "city_block", "residential", "farm", "farmyard", "industrial",
            "commercial", "retail", "building")
        private val HOUSE_MARKERS = listOf("house_number", "house_name")
        private val STREET_ADDRESS = listOf(HOUSE_MARKERS, listOf("road"))

        private const val SUBURB = "suburb"
        private const val STATE = "ISO3166-2-lvl"
        private const val POSTCODE = "postcode"
    }

    val displayName: String get() {
        if (address == null) return _displayName
        return address.formatted ?: _displayName
    }

    val dedupeKeys: List<String> get() = address?.let {
        listOfNotNull(
            PLACE_MARKERS.firstNotNullOfOrNull { address[it] },
            address["suburb"]
        )
    } ?: listOf()

    private val Map<String, String>.formatted: String? get() {
        if (get(SUBURB) == null) return null
        val place = PLACE_MARKERS.firstNotNullOfOrNull { get(it) }
        val streetAddress = STREET_ADDRESS.mapNotNull {
            it.firstNotNullOfOrNull { get(it) }
        }.nullIfEmpty()?.joinToString(" ")?.nullIf { it.isBlank() }

        val state = when(get(STATE)) {
            "AU-ACT" -> "ACT"
            "AU-NSW" -> "NSW"
            else -> null
        }

        val region = listOfNotNull(get(SUBURB), state, get(POSTCODE)).joinToString(", ")

        return listOfNotNull(place, streetAddress, region).joinToString(", ")
    }

}