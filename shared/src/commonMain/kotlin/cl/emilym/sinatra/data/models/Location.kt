package cl.emilym.sinatra.data.models

import cl.emilym.kmp.serializable.Serializable
import cl.emilym.sinatra.asDegrees
import cl.emilym.sinatra.asRadians
import cl.emilym.sinatra.degrees
import io.github.aakira.napier.Napier
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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
) {

    val width get() = abs(bottomRight.lat - topLeft.lat)
    val height get() = abs(bottomRight.lng - topLeft.lng)

    val center: MapLocation
        get() {
            Napier.d("Getting midpoint of topLeft = $topLeft and bottomRight = $bottomRight")
            val dLng = (bottomRight.lng - topLeft.lng).degrees.asRadians

            val tlLat = topLeft.lat.degrees.asRadians
            val brLat = bottomRight.lat.degrees.asRadians
            val tlLng = topLeft.lng.degrees.asRadians

            val bX = cos(brLat) * cos(dLng)
            val bY = cos(brLat) * sin(dLng)

            val mLat = atan2(
                sin(tlLat) + sin(brLat),
                sqrt((cos(tlLat) + bX).pow(2) + bY * bY)
            )
            val mLng = tlLng + atan2(bY, cos(tlLat) + bX)

            return MapLocation(mLat.asDegrees, mLng.asDegrees)
        }

}

data class ScreenLocation(
    val x: Pixel,
    val y: Pixel
)

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

data class CoordinateSpan(
    val deltaLatitude: Double,
    val deltaLongitude: Double
)