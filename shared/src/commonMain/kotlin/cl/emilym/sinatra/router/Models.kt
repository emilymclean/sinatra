package cl.emilym.sinatra.router

import cl.emilym.gtfs.networkgraph.Node
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId

data class NodeAndIndex<T: Node, I: NodeIndex>(
    val node: T,
    val index: I
)

data class RouteAndHeading(
    val routeIndex: RouteIndex,
    val headingIndex: HeadingIndex
)

data class TravelTime<T: Node>(
    val node: T,
    val arrival: EpochSeconds
)

sealed interface BoardedFrom {
    data class Transfer(
        val node: StopIndex,
    ): BoardedFrom
    data class Travel(
        val node: StopRouteNodeIndex,
        val boardingTime: EpochSeconds
    ): BoardedFrom
}

data class StopAndBoarding(
    val stop: StopIndex,
    val boarding: BoardedFrom
)

class StopInformation(
    maximumNumberOfTrips: Int
) {
    // τ∗(pi)
    var earliestArrivalTime: EpochSeconds = Long.MAX_VALUE
    // τi(pi)
    val earliestArrivalTimeForTrip: Array<EpochSeconds> = Array(maximumNumberOfTrips) { Long.MAX_VALUE }
    var boardedFrom: BoardedFrom? = null
}

sealed interface JourneyConnection {
    data class Transfer(
        val travelTime: EpochSeconds
    ): JourneyConnection
    data class Travel(
        val routeId: RouteId,
        val heading: String,
        val startTime: EpochSeconds,
        val endTime: EpochSeconds
    ): JourneyConnection
}

data class Journey(
    val stops: List<StopId>,
    val connections: List<JourneyConnection>
)