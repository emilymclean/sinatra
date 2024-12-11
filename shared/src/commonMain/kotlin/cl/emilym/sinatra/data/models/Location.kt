package cl.emilym.sinatra.data.models

import cl.emilym.kmp.serializable.Serializable

data class MapLocation(
    val lat: Latitude,
    val lng: Longitude
): Serializable {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.Location): MapLocation {
            return MapLocation(
                pb.lat,
                pb.lng
            )
        }
    }

}

data class MapRegion(
    val topLeft: MapLocation,
    val bottomRight: MapLocation,
)

data class ScreenLocation(
    val x: Pixel,
    val y: Pixel
) {

    companion object {
        val ZERO = ScreenLocation(0,0)
    }

}

data class ScreenRegion(
    val topLeft: ScreenLocation,
    val bottomRight: ScreenLocation
) {
    val width get() = bottomRight.x - topLeft.x
    val height get() = bottomRight.y - topLeft.y

    val aspect get() = width.toFloat() / height

    fun padded(padding: Pixel): ScreenRegion {
        val halfPadding = padding / 2
        return copy(
            topLeft = ScreenLocation(
                topLeft.x - halfPadding,
                topLeft.y - halfPadding
            ),
            bottomRight = ScreenLocation(
                bottomRight.x + halfPadding,
                bottomRight.y + halfPadding
            )
        )
    }
}