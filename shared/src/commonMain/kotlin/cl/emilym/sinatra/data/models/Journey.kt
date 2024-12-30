package cl.emilym.sinatra.data.models

import kotlin.time.Duration

data class Journey(
    val legs: List<JourneyLeg>
)

sealed interface JourneyLeg {
    interface RouteJourneyLeg: JourneyLeg {
        val stops: List<Stop>
    }

    val travelTime: Duration
    val departureTime: Time
    val arrivalTime: Time

    data class Transfer(
        override val stops: List<Stop>,
        override val travelTime: Duration,
        override val departureTime: Time,
        override val arrivalTime: Time
    ): JourneyLeg, RouteJourneyLeg

    data class Travel(
        override val stops: List<Stop>,
        override val travelTime: Duration,
        val route: Route,
        val heading: String,
        override val departureTime: Time,
        override val arrivalTime: Time
    ): JourneyLeg, RouteJourneyLeg

    data class TransferPoint(
        override val travelTime: Duration,
        override val departureTime: Time,
        override val arrivalTime: Time
    ): JourneyLeg
}