package cl.emilym.sinatra.ui.maps

import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.lib.FloatRange

interface MapItem {
    val id: String
    val visible: Boolean
}

interface DescribedMapItem {
    val contentDescription: String?
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
    val visibleZoomRange: FloatRange? = null,
    override val contentDescription: String? = null
): MapItem, ClickableMapItem, DescribedMapItem {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MarkerItem

        if (location != other.location) return false
        if (visible != other.visible) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = location.hashCode()
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + visible.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}

data class LineItem(
    val points: List<MapLocation>,
    val color: Color? = null,
    override val visible: Boolean = true,
    override val id: String = uuid(),
): MapItem