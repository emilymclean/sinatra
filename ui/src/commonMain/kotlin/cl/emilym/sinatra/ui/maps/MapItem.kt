package cl.emilym.sinatra.ui.maps

import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.MapLocation

interface MapItem {
    val id: String
    val visible: Boolean
}

interface ClickableMapItem {
    val onClick: (() -> Unit)?
}

data class MarkerItem(
    val location: MapLocation,
    val icon: MarkerIcon? = null,
    override val onClick: (() -> Unit)? = null,
    override val visible: Boolean = true,
    override val id: String = uuid(),
): MapItem, ClickableMapItem

data class LineItem(
    val points: List<MapLocation>,
    val color: Color? = null,
    override val visible: Boolean = true,
    override val id: String = uuid(),
): MapItem