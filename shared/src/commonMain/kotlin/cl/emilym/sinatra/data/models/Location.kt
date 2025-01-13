package cl.emilym.sinatra.data.models

import cl.emilym.kmp.serializable.Serializable
import cl.emilym.sinatra.asDegrees
import cl.emilym.sinatra.asRadians
import cl.emilym.sinatra.degrees
import cl.emilym.sinatra.radians
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
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

    fun combine(coordinateSpan: CoordinateSpan): MapRegion {
        val halfLat = coordinateSpan.deltaLatitude / 2
        val halfLng = coordinateSpan.deltaLongitude / 2
        return MapRegion(
            MapLocation(
                lat + halfLat,
                lng - halfLng
            ),
            MapLocation(
                lat - halfLat,
                lng + halfLat
            )
        ).order()
    }

}

data class MapRegion(
    val topLeft: MapLocation,
    val bottomRight: MapLocation,
) {

    val width get() = abs(bottomRight.lng - topLeft.lng)
    val height get() = abs(bottomRight.lat - topLeft.lat)

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

    private val naiveCenter = MapLocation(
        ((max(topLeft.lat, bottomRight.lat) - min(topLeft.lat, bottomRight.lat)) / 2) + min(topLeft.lat, bottomRight.lat),
        ((max(topLeft.lng, bottomRight.lng) - min(topLeft.lng, bottomRight.lng)) / 2) + min(topLeft.lng, bottomRight.lng),
    )
    val center: MapLocation get() = naiveCenter

    fun contains(location: MapLocation): Boolean {
        return location.lat <= topLeft.lat && location.lat >= bottomRight.lat &&
                location.lng >= topLeft.lng && location.lng <= bottomRight.lng
    }

    val northEast: MapLocation get() = MapLocation(
        topLeft.lat, bottomRight.lng
    )
    val southWest: MapLocation get() = MapLocation(
        bottomRight.lat, topLeft.lng
    )

}

const val EARTH_RADIUS = 6371.0

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

fun distance(m1: MapLocation, m2: MapLocation): Double {
    return acos(
        (sin(m1.lat.asRadians) * sin(m2.lat.asRadians)) +
                (cos(m1.lat.asRadians) * cos(m2.lat.asRadians) * cos(m2.lng.asRadians - m1.lng.asRadians))) *
            EARTH_RADIUS
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