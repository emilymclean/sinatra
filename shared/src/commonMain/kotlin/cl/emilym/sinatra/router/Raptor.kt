package cl.emilym.sinatra.router

import cl.emilym.gtfs.networkgraph.EdgeType
import cl.emilym.gtfs.networkgraph.Graph
import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.StopId
import io.github.aakira.napier.Napier
import kotlin.math.min

class Raptor(
    private val graph: Graph
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

    // Currently the graph construction always places stop nodes first, take advantage of that by using
    // an array structure to store stop information rather than a map
    var stopInformation = Array(graph.mappings.stopIds.size) {
        StopInformation(MAXIMUM_TRIPS)
    }

    fun calculate(departureTime: EpochSeconds, departureStop: StopId, arrivalStop: StopId): Journey {
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

    private fun calculateJourney(): Journey {
        val boardings = collectJourneyBoardings()
        if (boardings.isEmpty()) throw RouterException.noJourneyFound()
        val stops = listOf(departureStop) + boardings.map { it.stop }
        val stopArrivals = stops.map { getStopEarliestArrival(it) }
        return Journey(
            stops.map { graph.mappings.stopIds[it] },
            boardings.mapIndexed { i, it ->
                when (it.boarding) {
                    is BoardedFrom.Transfer -> JourneyConnection.Transfer(
                        stopArrivals[i + 1] - stopArrivals[i]
                    )
                    is BoardedFrom.Travel -> {
                        val stopRouteNode = getNode(it.boarding.node)
                        JourneyConnection.Travel(
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

    private fun initializeDepartureStop(departureTime: EpochSeconds, departureStop: StopId) {
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
            Napier.d("Processing marked stop ${markedStop}")
            val routesForStop = routesForStop(getNode(markedStop))
            for (r in routesForStop) {
                if (r.node.routeId == null || r.node.headingId == null) continue
                val routeAndHeading = RouteAndHeading(r.node.routeId, r.node.headingId)
                if (routeAndHeading in visitedRoutes) {
                    if (!stopBeforeOtherOnRoute(r.node, visitedRoutes[routeAndHeading]!!.node.stopId)) continue
                }

                visitedRoutes[routeAndHeading] = r
            }
        }

        Napier.d("Found ${visitedRoutes.size} connected routes/headings (${
            visitedRoutes.map { "{${it.key.routeIndex}, ${it.key.headingIndex}}" }
        })")

        for (r in visitedRoutes.keys) {
            val initialStopIndex = visitedRoutes[r]!!
            val currentEarliestArrival = getStopEarliestArrival(initialStopIndex.node.stopId)
            var nextStops = listOf(
                TravelTime(
                    getStopRouteNode(initialStopIndex.node.stopId, r.routeIndex, r.headingIndex)!!,
                    currentEarliestArrival
                )
            )

            Napier.d("Searching for next stops starting at ${nextStops[0].arrival}")
            while(nextStops.isNotEmpty()) {
                nextStops = nextStops.flatMap { travelRoute(it.node, it.arrival) }
                Napier.d("Found ${nextStops.size} connected stops")
                for (nextStop in nextStops) {
                    val stopIndex = nextStop.node.stopId
                    if (!nextStop.isEarlier()) continue
                    updateEarliestArrival(stopIndex, nextStop.arrival, BoardedFrom.Travel(
                        initialStopIndex.index,
                        currentEarliestArrival
                    ))
                }
            }
        }

        for (markedStop in currentMarkedStops) {
            val markedStopNode = getNode(markedStop)
            val transfers = transfersForStop(markedStopNode, getStopEarliestArrival(markedStop))
            for (transfer in transfers) {
                val stopIndex = transfer.node.stopId
                if (!transfer.isEarlier()) continue
                updateEarliestArrival(stopIndex, transfer.arrival, BoardedFrom.Transfer(
                    markedStopNode.stopId
                ))
            }
        }
    }

    private fun getStopRouteNode(
        stopNodeIndex: StopNodeIndex,
        routeIndex: RouteIndex,
        headingIndex: HeadingIndex
    ): StopRouteNode? {
        val stopNode = getNode(stopNodeIndex)

        for (edge in stopNode.edges) {
            if (edge.type != EdgeType.STOP_ROUTE) continue
            val stopRouteNode = getNode(edge.toNodeId)
            if (stopRouteNode.routeId == routeIndex && stopRouteNode.headingId == headingIndex)
                return stopRouteNode
        }

        return null
    }

    private fun getNode(index: NodeIndex) = graph.nodes[index]

    private fun updateEarliestArrival(stopIndex: StopIndex, arrival: Long, boardedFrom: BoardedFrom?) {
        setStopArrival(stopIndex, round, arrival)
        setStopEarliestArrival(stopIndex, arrival)
        if (boardedFrom != null)
            setStopBoardedFrom(stopIndex, boardedFrom)
        markedStops += stopIndex
    }

    private fun setStopArrival(stopIndex: StopNodeIndex, trip: Int, time: EpochSeconds) {
        stopInformation[stopIndex].earliestArrivalTimeForTrip[trip] = time
    }

    private fun getStopArrival(stopIndex: StopNodeIndex, trip: Int): EpochSeconds {
        return stopInformation[stopIndex].earliestArrivalTimeForTrip[trip]
    }

    private fun setStopEarliestArrival(stopIndex: StopNodeIndex, time: EpochSeconds) {
        stopInformation[stopIndex].earliestArrivalTime = time
    }

    private fun getStopEarliestArrival(stopIndex: StopNodeIndex): EpochSeconds {
        return stopInformation[stopIndex].earliestArrivalTime
    }

    private fun setStopBoardedFrom(stopIndex: StopNodeIndex, boardedFrom: BoardedFrom) {
        stopInformation[stopIndex].boardedFrom = boardedFrom
    }

    private fun getStopBoardedFrom(stopIndex: StopNodeIndex): BoardedFrom? {
        return stopInformation[stopIndex].boardedFrom
    }

    private fun servicesAreActive(serviceIndices: List<ServiceIndex>): Boolean = true // TODO

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

    private fun transfersForStop(node: StopNode, departureTime: EpochSeconds): List<TravelTime<StopNode>> {
        return node.edges
            .filter { it.type == EdgeType.TRANSFER }
            .map {
                TravelTime(
                    getNode(it.toNodeId),
                    it.penalty + departureTime
                )
            }
    }

    private fun travelRoute(node: StopRouteNode, minimumDepartureTime: EpochSeconds = 0): List<TravelTime<StopRouteNode>> {
        return node.edges
            .filter { it.type == EdgeType.TRAVEL && servicesAreActive(it.availableServices) }
            .filter { (it.departureTime ?: 0) > minimumDepartureTime }
            .groupBy { it.toNodeId }
            .map {
                val edge = it.value.minBy { it.penalty + (it.departureTime ?: 0) }
                TravelTime(
                    getNode(edge.toNodeId),
                    edge.penalty + (edge.departureTime ?: 0)
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