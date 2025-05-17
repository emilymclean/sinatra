package cl.emilym.sinatra.domain.navigation

import cl.emilym.sinatra.data.models.MapLocation
import kotlinx.datetime.Instant

sealed interface JourneyCalculationTime {
    val time: Instant

    data class DepartureTime(
        override val time: Instant
    ): JourneyCalculationTime
    data class ArrivalTime(
        override val time: Instant
    ): JourneyCalculationTime
}

data class JourneyLocation(
    val location: MapLocation,
    val exact: Boolean
)