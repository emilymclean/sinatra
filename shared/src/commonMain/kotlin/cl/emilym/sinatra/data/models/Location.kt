package cl.emilym.sinatra.data.models

import cl.emilym.gtfs.Location
import cl.emilym.kmp.serializable.Serializable
import cl.emilym.sinatra.asDegrees
import cl.emilym.sinatra.asRadians
import cl.emilym.sinatra.degrees
import cl.emilym.sinatra.radians
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

    fun order(): MapRegion {
        return copy(
            topLeft = MapLocation(
                if (topLeft.lat > bottomRight.lat) topLeft.lat else bottomRight.lat,
                topLeft.lng
            ),
            bottomRight = MapLocation(
                if (topLeft.lat <= bottomRight.lat) topLeft.lat else bottomRight.lat,
                bottomRight.lng
            )
        )
    }

    val center: MapLocation = listOf(topLeft, bottomRight).findMidpoint()

}

fun List<MapLocation>.findMidpoint(): MapLocation {
    val latR = map { it.lat.degrees.asRadians }
    val lngR = map { it.lng.degrees.asRadians }

    val X = mapIndexed { i, it -> cos(latR[i]) * cos(lngR[i]) }
    val Y = mapIndexed { i, it -> cos(latR[i]) * sin(lngR[i]) }
    val Z = mapIndexed { i, it -> sin(latR[i]) }

    val x = X.sum() / X.size
    val y = Y.sum() / Y.size
    val z = Z.sum() / Z.size

    val oLng = atan2(y,x)
    val hyp = sqrt(x.pow(2) + y.pow(2))
    val oLat = atan2(z,hyp)

    return MapLocation(oLat.radians.asDegrees, oLng.radians.asDegrees)
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

    fun order(): ScreenRegion {
        return copy(
            topLeft = ScreenLocation(
                if (topLeft.x < bottomRight.x) topLeft.x else bottomRight.x,
                if (topLeft.y < bottomRight.y) topLeft.y else bottomRight.y
            ),
            bottomRight = ScreenLocation(
                if (topLeft.x >= bottomRight.x) topLeft.x else bottomRight.x,
                if (topLeft.y >= bottomRight.y) topLeft.y else bottomRight.y
            )
        )
    }
}

data class CoordinateSpan(
    val deltaLatitude: Double,
    val deltaLongitude: Double
)