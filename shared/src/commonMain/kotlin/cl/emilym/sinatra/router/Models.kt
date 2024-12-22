package cl.emilym.sinatra.router

import cl.emilym.gtfs.networkgraph.Node

data class RouteAndHeading(
    val routeIndex: RouteIndex,
    val headingIndex: HeadingIndex
)

data class TravelTime<T: Node>(
    val node: T,
    val arrival: EpochSeconds
)

class StopInformation(
    maximumNumberOfTrips: Int
) {
    // τ∗(pi)
    var earliestArrivalTime: EpochSeconds = Long.MAX_VALUE
    // τi(pi)
    val earliestArrivalTimeForTrip: Array<EpochSeconds> = Array(maximumNumberOfTrips) { Long.MAX_VALUE }
}