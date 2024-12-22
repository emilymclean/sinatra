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
    val arrival: EpochSeconds,
    val departureTime: EpochSeconds
)

data class TravelTimeWithRootDepartureTime<T: Node>(
    val travelTime: TravelTime<T>,
    val rootDepartureTime: EpochSeconds
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

    override fun toString(): String {
        return "StopInformation(boardedFrom=$boardedFrom, earliestArrivalTimeForTrip=${earliestArrivalTimeForTrip.contentToString()}, earliestArrivalTime=$earliestArrivalTime)"
    }

}

sealed interface RaptorJourneyConnection {
    data class Transfer(
        val travelTime: EpochSeconds
    ): RaptorJourneyConnection
    data class Travel(
        val routeId: RouteId,
        val heading: String,
        val startTime: EpochSeconds,
        val endTime: EpochSeconds
    ): RaptorJourneyConnection
}

data class RaptorJourney(
    val stops: List<StopId>,
    val connections: List<RaptorJourneyConnection>
)