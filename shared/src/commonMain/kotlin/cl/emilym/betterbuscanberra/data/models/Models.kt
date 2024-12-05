package cl.emilym.betterbuscanberra.data.models

import cl.emilym.gtfs.WheelchairStopAccessibility

data class Stop(
    val id: StopId,
    val parentStation: StopId?,
    val name: String,
    val location: Location,
    val accessibility: StopAccessibility
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.Stop): Stop {
            return Stop(
                pb.id,
                pb.parentStation,
                pb.name,
                Location.fromPB(pb.location),
                StopAccessibility.fromPB(pb.accessibility)
            )
        }
    }

}

data class StopAccessibility(
    val wheelchair: StopWheelchairAccessibility
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.StopAccessibility): StopAccessibility {
            return StopAccessibility(
                StopWheelchairAccessibility.fromPB(pb.stopWheelchairAccessible)
            )
        }
    }

}

enum class StopWheelchairAccessibility {
    UNKNOWN, NONE, PARTIAL, FULL;

    companion object {

        fun fromPB(pb: WheelchairStopAccessibility): StopWheelchairAccessibility {
            return when (pb) {
                is WheelchairStopAccessibility.UNKNOWN, is WheelchairStopAccessibility.UNRECOGNIZED ->
                    UNKNOWN
                is WheelchairStopAccessibility.FULL -> FULL
                is WheelchairStopAccessibility.PARTIAL -> PARTIAL
                is WheelchairStopAccessibility.NONE -> NONE
            }
        }

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