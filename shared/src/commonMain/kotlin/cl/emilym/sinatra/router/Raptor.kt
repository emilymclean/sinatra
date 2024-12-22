package cl.emilym.sinatra.router

import cl.emilym.gtfs.networkgraph.EdgeType
import cl.emilym.gtfs.networkgraph.Graph
import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.StopId
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
    private var visitedRoutes = mutableMapOf<RouteAndHeading, StopNodeIndex>()
    private var arrivalStop: StopIndex = 0

    // Currently the graph construction always places stop nodes first, take advantage of that by using
    // an array structure to store stop information rather than a map
    private var stopInformation = Array(graph.mappings.stopIds.size) {
        StopInformation(MAXIMUM_TRIPS)
    }

    fun calculate(departureTime: EpochSeconds, departureStop: StopId, arrivalStop: StopId) {
        initializeDepartureStop(departureTime, departureStop)
        this.arrivalStop = graph.mappings.stopNodes[arrivalStop] ?:
                throw RouterException.stopNotFound(arrivalStop)

        while (round < MAXIMUM_TRIPS && markedStops.isNotEmpty()) {
            calculateRound()
            round++
        }
    }

    private fun initializeDepartureStop(departureTime: EpochSeconds, departureStop: StopId) {
        val stopIndex = graph.mappings.stopNodes[departureStop] ?:
            throw RouterException.stopNotFound(departureStop)

        setStopArrival(stopIndex, 0, departureTime)
        setStopEarliestArrival(stopIndex, departureTime)
        markedStops += stopIndex
    }

    private fun calculateRound() {
        visitedRoutes.clear()

        for (markedStop in markedStops) {
            val routesForStop = routesForStop(getNode(markedStop))
            for (r in routesForStop) {
                if (r.routeId == null || r.headingId == null) continue
                val routeAndHeading = RouteAndHeading(r.routeId, r.headingId)
                if (routeAndHeading in visitedRoutes) {
                    if (!stopBeforeOtherOnRoute(r, visitedRoutes[routeAndHeading]!!)) continue
                }

                visitedRoutes[routeAndHeading] = markedStop
            }
            markedStops.remove(markedStop)
        }

        for (r in visitedRoutes.keys) {
            val initialStopIndex = visitedRoutes[r]!!
            var nextStops = listOf(
                TravelTime(
                    getStopRouteNode(initialStopIndex, r.routeIndex, r.headingIndex)!!,
                    getStopEarliestArrival(initialStopIndex)
                )
            )

            while(nextStops.isNotEmpty()) {
                nextStops = nextStops.flatMap { travelRoute(it.node, it.arrival) }
                for (nextStop in nextStops) {
                    val stopIndex = nextStop.node.stopId
                    if (!nextStop.isEarlier()) continue

                    setStopArrival(stopIndex, round, nextStop.arrival)
                    setStopEarliestArrival(stopIndex, nextStop.arrival)
                    markedStops += stopIndex
                }
            }
        }

        for (markedStop in markedStops) {
            val transfers = transfersForStop(getNode(markedStop), getStopEarliestArrival(markedStop))
            for (transfer in transfers) {
                val stopIndex = transfer.node.stopId
                if (!transfer.isEarlier()) continue

                setStopArrival(stopIndex, round, transfer.arrival)
                setStopEarliestArrival(stopIndex, transfer.arrival)
                markedStops += stopIndex
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

    private fun servicesAreActive(serviceIndices: List<ServiceIndex>): Boolean = true // TODO

    private fun routesForStop(node: StopNode): List<StopRouteNode> {
        return node.edges
            .filter { it.type == EdgeType.STOP_ROUTE }
            .map { getNode(it.toNodeId) }
    }

    private fun stopBeforeOtherOnRoute(node: StopRouteNode, stopIndex: StopIndex): Boolean {
        val travels = travelRouteUnique(node)

        for (destinationNode in travels) {
            if (destinationNode.stopId == stopIndex) return true
        }

        for (destinationNode in travels) {
            if (stopBeforeOtherOnRoute(destinationNode, stopIndex)) return true
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

    private fun travelRouteUnique(node: StopRouteNode): Set<StopRouteNode> {
        return node.edges
            .filter { it.type == EdgeType.TRAVEL && servicesAreActive(it.availableServices) }
            .map { getNode(it.toNodeId) }
            .toSet()
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