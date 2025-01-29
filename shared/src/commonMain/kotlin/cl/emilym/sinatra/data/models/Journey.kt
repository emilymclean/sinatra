package cl.emilym.sinatra.data.models

import kotlin.time.Duration

data class Journey(
    val legs: List<JourneyLeg>
) {

    val departureTime: Time
        get() {
            val f = legs.first()
            if (legs.size > 2 && f is JourneyLeg.TransferPoint)
                return legs[1].departureTime - f.travelTime
            return f.departureTime
        }
    val arrivalTime: Time
        get() = legs.last().arrivalTime

    val duration: Duration
        get() = (arrivalTime.instant - departureTime.instant)

    val deduplicationKey by lazy { legs.joinToString(";") { it.deduplicationKey } }

}

sealed interface JourneyLeg {
    interface RouteJourneyLeg: JourneyLeg {
        val stops: List<Stop>
    }

    val travelTime: Duration
    val departureTime: Time
    val arrivalTime: Time
    val deduplicationKey: String

    data class Transfer(
        override val stops: List<Stop>,
        override val travelTime: Duration,
        override val departureTime: Time,
        override val arrivalTime: Time
    ): JourneyLeg, RouteJourneyLeg {

        override val deduplicationKey get() =
            "transfer:${stops.joinToString(",") { it.id }}"

    }

    data class Travel(
        override val stops: List<Stop>,
        override val travelTime: Duration,
        val route: Route,
        val heading: String,
        override val departureTime: Time,
        override val arrivalTime: Time
    ): JourneyLeg, RouteJourneyLeg {

        override val deduplicationKey get() =
            "travel:${stops.joinToString(",") { it.id }}:${route.id}:${heading}"

    }

    data class TransferPoint(
        override val travelTime: Duration,
        override val departureTime: Time,
        override val arrivalTime: Time
    ): JourneyLeg {

        override val deduplicationKey get() =
            "transferPoint"

    }
}