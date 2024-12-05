package cl.emilym.sinatra.data.models

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
    val color: String,
    val onColor: OnColor
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.ColorPair): ColorPair {
            return ColorPair(
                pb.color,
                OnColor.fromPB(pb.color)
            )
        }
    }

}