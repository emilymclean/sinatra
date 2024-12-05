package cl.emilym.sinatra.data.models

import kotlinx.datetime.Instant
import kotlin.time.Duration

interface StopTime {
    val arrivalTime: Time
    val departureTime: Time

    fun arrivalTime(startOfDay: Instant): Instant {
        return arrivalTime.forDay(startOfDay)
    }

    fun departureTime(startOfDay: Instant): Instant {
        return departureTime.forDay(startOfDay)
    }
}

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

enum class ServiceBikesAllowed {
    UNKNOWN, ALLOWED, DISALLOWED;

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.ServiceBikesAllowed): ServiceBikesAllowed {
            return when (pb) {
                is cl.emilym.gtfs.ServiceBikesAllowed.ALLOWED -> ALLOWED
                is cl.emilym.gtfs.ServiceBikesAllowed.DISALLOWED -> DISALLOWED
                else -> UNKNOWN
            }
        }
    }
}

enum class ServiceWheelchairAccessible {
    UNKNOWN, ACCESSIBLE, INACCESSIBLE;

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.ServiceWheelchairAccessible): ServiceWheelchairAccessible {
            return when (pb) {
                is cl.emilym.gtfs.ServiceWheelchairAccessible.ACCESSIBLE -> ACCESSIBLE
                is cl.emilym.gtfs.ServiceWheelchairAccessible.INACCESSIBLE -> INACCESSIBLE
                else -> UNKNOWN
            }
        }
    }
}

interface ServiceAccessibility {
    val bikesAllowed: ServiceBikesAllowed
    val wheelchairAccessible: ServiceWheelchairAccessible
}