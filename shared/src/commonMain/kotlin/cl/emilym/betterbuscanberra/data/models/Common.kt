package cl.emilym.betterbuscanberra.data.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.emilym.betterbuscanberra.misc.toColor

data class Location(
    val lat: Latitude,
    val lng: Longitude
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.Location): Location {
            return Location(
                pb.lat,
                pb.lng
            )
        }
    }

}

enum class OnColor {
    DARK, LIGHT;

    @get:Composable
    val color get() = when (this) {
        DARK -> Color.Black
        LIGHT -> Color.White
    }

    companion object {
        fun fromPB(color: String): OnColor {
            return when (color.lowercase()) {
                "dark" -> DARK
                "light" -> LIGHT
                else -> DARK
            }
        }
    }
}

data class ColorPair(
    val _color: String,
    val _onColor: OnColor
) {

    @get:Composable
    val color get() = _color.toColor()
    val onColor
        @Composable
        get() = _onColor.color

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.ColorPair): ColorPair {
            return ColorPair(
                pb.color,
                OnColor.fromPB(pb.color)
            )
        }
    }

}