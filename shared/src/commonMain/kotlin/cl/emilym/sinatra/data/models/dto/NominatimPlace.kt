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
    val displayName: String
)