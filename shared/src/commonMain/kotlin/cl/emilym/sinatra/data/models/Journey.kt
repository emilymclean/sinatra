package cl.emilym.sinatra.data.models

import kotlin.time.Duration

data class Journey(
    val legs: List<JourneyLeg>
)

sealed interface JourneyLeg {
    val stops: List<Stop>

    data class Transfer(
        override val stops: List<Stop>,
        val travelTime: Duration
    ): JourneyLeg

    data class Travel(
        override val stops: List<Stop>,
        val route: Route,
        val heading: String,
        val departureTime: Time,
        val arrivalTime: Time
    ): JourneyLeg
}