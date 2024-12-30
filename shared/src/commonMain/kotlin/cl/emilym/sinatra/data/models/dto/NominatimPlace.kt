package cl.emilym.sinatra.data.models.dto

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
    val address: NominatimAddress? = null
) {

    val displayName: String get() {
        if (address == null) return _displayName
        return address.formatted ?: _displayName
    }

}

@Serializable
data class NominatimAddress(
    @SerialName("house_number")
    val houseNumber: String? = null,
    val road: String? = null,
    val suburb: String? = null,
    val postcode: String? = null,
    @SerialName("ISO3166-2-lvl4")
    val isoState: String? = null
) {

    val formatted: String? get() {
        if (suburb == null) return null
        val streetAddress = when {
            houseNumber != null -> "$houseNumber $road"
            road != null -> road
            else -> null
        }

        val state = when(isoState) {
            "AU-ACT" -> "ACT"
            "AU-NSW" -> "NSW"
            else -> null
        }

        val region = listOfNotNull(suburb, state, postcode).joinToString(", ")

        return when {
            streetAddress != null -> "$streetAddress, $region"
            else -> region
        }
    }

}