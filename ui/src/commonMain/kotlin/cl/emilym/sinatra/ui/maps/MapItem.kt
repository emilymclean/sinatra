package cl.emilym.sinatra.ui.maps

import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.MapLocation

interface MapItem {
    val id: String
    val visible: Boolean
}

data class MarkerItem(
    val location: MapLocation,
    val icon: MarkerIcon? = null,
    val onClick: (() -> Unit)? = null,
    override val visible: Boolean = true,
    override val id: String = uuid(),
): MapItem

data class LineItem(
    val points: List<MapLocation>,
    val color: Color? = null,
    override val visible: Boolean = true,
    override val id: String = uuid(),
): MapItem