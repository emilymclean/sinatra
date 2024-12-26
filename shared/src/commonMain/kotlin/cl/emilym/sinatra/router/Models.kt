package cl.emilym.sinatra.router

import cl.emilym.gtfs.networkgraph.Edge
import cl.emilym.gtfs.networkgraph.Node
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId

data class NodeCost(
    val node: NodeIndex,
    val cost: Long,
    val edge: Edge
)

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
    val arrival: DaySeconds,
    val departureTime: DaySeconds,
) {
    val travelTime: Seconds get() = arrival - departureTime
}

data class TravelTimeWithRootDepartureTime<T: Node>(
    val travelTime: TravelTime<T>,
    val rootDepartureTime: DaySeconds
)

sealed interface BoardedFrom {
    data class Transfer(
        val node: StopIndex,
    ): BoardedFrom
    data class Travel(
        val node: StopRouteNodeIndex,
        val boardingTime: DaySeconds
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
    var earliestArrivalTime: DaySeconds = Long.MAX_VALUE
    // τi(pi)
    val earliestArrivalTimeForTrip: Array<DaySeconds> = Array(maximumNumberOfTrips) { Long.MAX_VALUE }
    var boardedFrom: BoardedFrom? = null

    override fun toString(): String {
        return "StopInformation(boardedFrom=$boardedFrom, earliestArrivalTimeForTrip=${earliestArrivalTimeForTrip.contentToString()}, earliestArrivalTime=$earliestArrivalTime)"
    }

}

sealed interface RaptorJourneyConnection {
    val stops: List<StopId>
    val travelTime: Seconds

    data class Transfer(
        override val stops: List<StopId>,
        override val travelTime: Seconds
    ): RaptorJourneyConnection
    data class Travel(
        override val stops: List<StopId>,
        val routeId: RouteId,
        val heading: String,
        val startTime: DaySeconds,
        val endTime: DaySeconds,
        override val travelTime: Seconds
    ): RaptorJourneyConnection
}

data class RaptorJourney(
    val connections: List<RaptorJourneyConnection>
)