package cl.emilym.sinatra.router

import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.router.data.NetworkGraph
import cl.emilym.sinatra.router.data.NetworkGraphEdge

typealias Raptor = DepartureBasedRouter

class DepartureBasedRouter(
    override val graph: NetworkGraph,
    override val activeServiceIds: List<List<ServiceId>>,
    override val config: RaptorConfig,
    override val prefs: RouterPrefs = DEFAULT_ROUTER_PREFS
): Router() {

    override fun initializeDjikstra(
        Q: FibonacciHeap<Int, Long>,
        dist: Array<Long>,
        distP: Array<Long>,
        departureStopIndices: List<Pair<Int, RaptorStop>>,
        arrivalStopIndices: List<Pair<Int, RaptorStop>>
    ) {
        for (departureStopIndex in departureStopIndices) {
            dist[departureStopIndex.first] = departureStopIndex.second.addedTime
            distP[departureStopIndex.first] = departureStopIndex.second.addedTime
            Q.add(departureStopIndex.first, 0)
        }
    }

    override fun calculateAnchorTime(anchorTime: DaySeconds, dist: Long): Long = anchorTime + dist

    override fun isValidTravelEdge(
        edge: NetworkGraphEdge,
        dayAdjustment: Seconds,
        anchorTime: DaySeconds
    ): Boolean = (edge.departureTime.toLong() + dayAdjustment + 60) >= anchorTime

    override fun getTravelAnchorTime(
        edge: NetworkGraphEdge,
        dayAdjustment: Seconds,
        anchorTime: DaySeconds
    ): Long = (edge.departureTime.toLong() + dayAdjustment) - anchorTime

    override fun reconstruct(
        arrivalStopIndices: List<Pair<Int, RaptorStop>>,
        departureStopIndices: List<Pair<Int, RaptorStop>>,
        prev: Array<Int?>,
        prevEdge: Array<NetworkGraphEdge?>,
        dist: Array<Long>,
        dayIndex: Array<Int?>
    ): RaptorJourney {
        return RaptorJourney(
            doReconstruction(
                arrivalStopIndices,
                departureStopIndices,
                prev,
                prevEdge,
                dist,
                dayIndex
            )
        )
    }
}

class ArrivalBasedRouter(
    reversedGraph: NetworkGraph,
    override val activeServiceIds: List<List<ServiceId>>,
    override val config: RaptorConfig,
    override val prefs: RouterPrefs = DEFAULT_ROUTER_PREFS
): Router() {

    override val graph: NetworkGraph = reversedGraph

    override fun initializeDjikstra(
        Q: FibonacciHeap<Int, Long>,
        dist: Array<Long>,
        distP: Array<Long>,
        departureStopIndices: List<Pair<Int, RaptorStop>>,
        arrivalStopIndices: List<Pair<Int, RaptorStop>>
    ) {
        for (arrivalStopIndex in arrivalStopIndices) {
            dist[arrivalStopIndex.first] = arrivalStopIndex.second.addedTime
            distP[arrivalStopIndex.first] = arrivalStopIndex.second.addedTime
            Q.add(arrivalStopIndex.first, 0)
        }
    }

    override fun calculateAnchorTime(anchorTime: DaySeconds, dist: Long): Long = anchorTime - dist

    override fun isValidTravelEdge(
        edge: NetworkGraphEdge,
        dayAdjustment: Seconds,
        anchorTime: DaySeconds
    ): Boolean = (edge.departureTime.toLong() + dayAdjustment - 60) <= anchorTime

    override fun getTravelAnchorTime(
        edge: NetworkGraphEdge,
        dayAdjustment: Seconds,
        anchorTime: DaySeconds
    ): Long = anchorTime - (edge.departureTime.toLong() + dayAdjustment)

    override fun reconstruct(
        arrivalStopIndices: List<Pair<Int, RaptorStop>>,
        departureStopIndices: List<Pair<Int, RaptorStop>>,
        prev: Array<Int?>,
        prevEdge: Array<NetworkGraphEdge?>,
        dist: Array<Long>,
        dayIndex: Array<Int?>
    ): RaptorJourney {
        val reconstructed = doReconstruction(
            departureStopIndices,
            arrivalStopIndices,
            prev,
            prevEdge,
            dist,
            dayIndex
        )

        return RaptorJourney(
            reconstructed.reversed().map {
                when (it) {
                    is RaptorJourneyConnection.Transfer -> RaptorJourneyConnection.Transfer(
                        it.stops.reversed(),
                        it.travelTime
                    )
                    is RaptorJourneyConnection.Travel -> RaptorJourneyConnection.Travel(
                        it.stops.reversed(),
                        it.routeId,
                        it.heading,
                        it.endTime,
                        it.startTime,
                        it.dayIndex,
                        -it.travelTime
                    )
                }
            }
        )
    }

}