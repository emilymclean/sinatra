package cl.emilym.sinatra.router

import cl.emilym.gtfs.networkgraph.EdgeType
import cl.emilym.gtfs.networkgraph.Graph
import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import io.github.aakira.napier.Napier
import kotlin.math.min

data class RaptorConfig(
    val maximumWalkingTime: Seconds
)

class Raptor(
    private val graph: Graph,
    activeServices: List<ServiceId>,
    private val config: RaptorConfig? = null
) {

    companion object {
        const val MAXIMUM_TRIPS = 5
    }

    private var round = 0
    private val markedStops = mutableSetOf<StopNodeIndex>()
    // Q
    private var visitedRoutes = mutableMapOf<RouteAndHeading, NodeAndIndex<StopRouteNode, StopRouteNodeIndex>>()
    private var arrivalStop: StopNodeIndex = 0
    private var departureStop: StopNodeIndex = 0
    private val activeServices =
        activeServices.associate { graph.mappings.serviceIds.indexOf(it) to Unit }.also {
            Napier.d("Active services = ${it}")
        }

    // Currently the graph construction always places stop nodes first, take advantage of that by using
    // an array structure to store stop information rather than a map
    var stopInformation = Array(graph.mappings.stopIds.size) {
        StopInformation(MAXIMUM_TRIPS)
    }

    fun calculate(
        departureTime: DaySeconds,
        departureStop: StopId,
        arrivalStop: StopId
    ): RaptorJourney {
        initializeDepartureStop(departureTime, departureStop)
        this.arrivalStop = graph.mappings.stopNodes[arrivalStop] ?:
                throw RouterException.stopNotFound(arrivalStop)

        while (round < MAXIMUM_TRIPS && markedStops.isNotEmpty()) {
            calculateRound()
            round++
        }

        Napier.d("Arrival stop was visited = ${stopInformation[this.arrivalStop].boardedFrom != null}")

        return calculateJourney()
    }

    private fun calculateJourney(): RaptorJourney {
        val boardings = collectJourneyBoardings()
        if (boardings.isEmpty()) throw RouterException.noJourneyFound()
        val stops = listOf(departureStop) + boardings.map { it.stop }
        val stopArrivals = stops.map { getStopEarliestArrival(it) }
        return RaptorJourney(
            stops.map { graph.mappings.stopIds[it] },
            boardings.mapIndexed { i, it ->
                when (it.boarding) {
                    is BoardedFrom.Transfer -> RaptorJourneyConnection.Transfer(
                        stopArrivals[i + 1] - stopArrivals[i]
                    )
                    is BoardedFrom.Travel -> {
                        val stopRouteNode = getNode(it.boarding.node)
                        RaptorJourneyConnection.Travel(
                            graph.mappings.routeIds[stopRouteNode.routeId!!],
                            graph.mappings.headings[stopRouteNode.headingId!!],
                            it.boarding.boardingTime,
                            stopArrivals[i + 1]
                        )
                    }
                }
            }
        )
    }

    private fun collectJourneyBoardings(): List<StopAndBoarding> {
        val collection = mutableListOf<StopAndBoarding>()
        var stop: StopNodeIndex = arrivalStop
        while (stop != departureStop) {
            val from = getStopBoardedFrom(stop) ?: return emptyList()
            collection.add(0, StopAndBoarding(stop, from))
            stop = when (from) {
                is BoardedFrom.Transfer -> from.node
                is BoardedFrom.Travel -> getNode(from.node).stopId
            }
        }
        return collection
    }

    private fun initializeDepartureStop(departureTime: DaySeconds, departureStop: StopId) {
        val stopIndex = graph.mappings.stopNodes[departureStop] ?:
            throw RouterException.stopNotFound(departureStop)
        this.departureStop = stopIndex
        updateEarliestArrival(stopIndex, departureTime, null)
    }

    private fun calculateRound() {
        visitedRoutes.clear()
        val currentMarkedStops = markedStops.toSet()
        markedStops.clear()

        for (markedStop in currentMarkedStops) {
            val routesForStop = routesForStop(getNode(markedStop))
            for (r in routesForStop) {
                if (r.node.routeId == null || r.node.headingId == null) continue
                val routeAndHeading = RouteAndHeading(r.node.routeId, r.node.headingId)

                if (routeAndHeading in visitedRoutes) {
                    val existing = visitedRoutes[routeAndHeading]!!.node.stopId
                    if (!stopBeforeOtherOnRoute(r.node, existing)) continue
                    if (getStopEarliestArrival(r.node.stopId) > getStopEarliestArrival(existing)) continue
                }

                visitedRoutes[routeAndHeading] = r
            }
        }

        for (r in visitedRoutes.keys) {
            val initialStopIndex = visitedRoutes[r]!!
            val currentEarliestArrival = getStopEarliestArrival(initialStopIndex.node.stopId)
            var nextStops = travelRoute(initialStopIndex.node, currentEarliestArrival).map {
                TravelTimeWithRootDepartureTime(
                    it,
                    it.departureTime
                )
            }

            while(nextStops.isNotEmpty()) {
                for (nextStop in nextStops) {
                    val stopIndex = nextStop.travelTime.node.stopId
                    if (!nextStop.travelTime.isEarlier()) continue
                    updateEarliestArrival(stopIndex, nextStop.travelTime.arrival, BoardedFrom.Travel(
                        initialStopIndex.index,
                        nextStop.rootDepartureTime
                    ))
                }
                nextStops = nextStops.flatMap { travelRoute(it.travelTime.node, it.travelTime.arrival).map { tt ->
                    TravelTimeWithRootDepartureTime(tt, it.rootDepartureTime)
                } }
            }
        }

        for (markedStop in currentMarkedStops) {
            val markedStopNode = getNode(markedStop)
            val transfers = transfersForStop(markedStopNode, getStopEarliestArrival(markedStop))
            for (transfer in transfers) {
                val stopIndex = transfer.node.stopId
                if (!transfer.isEarlier() || transfer.travelTime > (config?.maximumWalkingTime ?: Long.MAX_VALUE)) continue
                updateEarliestArrival(stopIndex, transfer.arrival, BoardedFrom.Transfer(
                    markedStopNode.stopId
                ))
            }
        }
    }

    private fun getNode(index: NodeIndex) = graph.nodes[index]

    private fun updateEarliestArrival(stopIndex: StopIndex, arrival: Long, boardedFrom: BoardedFrom?) {
        setStopArrival(stopIndex, round, arrival)
        setStopEarliestArrival(stopIndex, arrival)
        if (boardedFrom != null)
            setStopBoardedFrom(stopIndex, boardedFrom)
        markedStops += stopIndex
    }

    private fun setStopArrival(stopIndex: StopNodeIndex, trip: Int, time: DaySeconds) {
        stopInformation[stopIndex].earliestArrivalTimeForTrip[trip] = time
    }

    private fun setStopEarliestArrival(stopIndex: StopNodeIndex, time: DaySeconds) {
        stopInformation[stopIndex].earliestArrivalTime = time
    }

    private fun getStopEarliestArrival(stopIndex: StopNodeIndex): DaySeconds {
        return stopInformation[stopIndex].earliestArrivalTime
    }

    private fun setStopBoardedFrom(stopIndex: StopNodeIndex, boardedFrom: BoardedFrom) {
        stopInformation[stopIndex].boardedFrom = boardedFrom
    }

    private fun getStopBoardedFrom(stopIndex: StopNodeIndex): BoardedFrom? {
        return stopInformation[stopIndex].boardedFrom
    }

    private fun servicesAreActive(
        serviceIndices: List<ServiceIndex>
    ): Boolean {
        for (i in serviceIndices) {
            if (activeServices.containsKey(i)) return true
        }
        return false
    }

    private fun routesForStop(node: StopNode): List<NodeAndIndex<StopRouteNode, StopRouteNodeIndex>> {
        return node.edges
            .filter { it.type == EdgeType.STOP_ROUTE }
            .map { NodeAndIndex(getNode(it.toNodeId), it.toNodeId) }
    }

    private fun stopBeforeOtherOnRoute(node: StopRouteNode, stopIndex: StopIndex): Boolean {
        val visited = mutableSetOf<StopRouteNodeIndex>()
        val toVisit = mutableSetOf<StopRouteNode>()

        fun next(node: StopRouteNode) {
            toVisit.addAll(
                node.edges
                    .filter { it.type == EdgeType.TRAVEL && servicesAreActive(it.availableServices) }
                    .filterNot { it.toNodeId in visited }
                    .also { visited.addAll(it.map { it.toNodeId }) }
                    .map { getNode(it.toNodeId) }
            )
        }

        next(node)
        while(toVisit.isNotEmpty()) {
            val destination = toVisit.first().also { toVisit.remove(it) }
            if (destination.stopId == stopIndex) return true
            next(destination)
        }

        return false
    }

    private fun transfersForStop(node: StopNode, departureTime: DaySeconds): List<TravelTime<StopNode>> {
        return node.edges
            .filter { it.type == EdgeType.TRANSFER }
            .map {
                TravelTime(
                    getNode(it.toNodeId),
                    it.penalty + departureTime,
                    departureTime
                )
            }
    }

    private fun travelRoute(node: StopRouteNode, minimumDepartureTime: DaySeconds = 0): List<TravelTime<StopRouteNode>> {
        return node.edges
            .filter { it.type == EdgeType.TRAVEL && servicesAreActive(it.availableServices) }
            .filter { (it.departureTime ?: 0) > minimumDepartureTime }
            .groupBy { it.toNodeId }
            .map {
                val edge = it.value.minBy { it.penalty + (it.departureTime ?: 0) }
                TravelTime(
                    getNode(edge.toNodeId),
                    edge.penalty + (edge.departureTime ?: 0),
                    (edge.departureTime ?: 0)
                )
            }
    }

    private fun TravelTime<*>.isEarlier(): Boolean = arrival < min(
        getStopEarliestArrival(node.stopId),
        getStopEarliestArrival(arrivalStop)
    )

//    private fun travelWholeRoute(node: StopRouteNode): List<TravelTime<StopRouteNode>> {
//        val destinations = travelRoute(node)
//        val children = mutableListOf<TravelTime<StopRouteNode>>()
//
//        for (destination in destinations) {
//            children += travelWholeRoute(destination.node)
//        }
//
//        return destinations + children
//    }

}