package cl.emilym.sinatra.router

import cl.emilym.gtfs.networkgraph.Edge
import cl.emilym.gtfs.networkgraph.EdgeType
import cl.emilym.gtfs.networkgraph.Graph
import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId

data class RaptorConfig(
    val maximumWalkingTime: Seconds
)

class Raptor(
    private val graph: Graph,
    activeServices: List<ServiceId>,
    private val config: RaptorConfig? = null
) {

    private val activeServices = activeServices.associate { graph.mappings.serviceIds.indexOf(it) to Unit }

    fun calculate(
        departureTime: DaySeconds,
        departureStop: StopId,
        arrivalStop: StopId
    ): RaptorJourney {
        val arrivalStopIndex = graph.mappings.stopNodes[arrivalStop] ?: throw RouterException.stopNotFound(arrivalStop)
        val departureStopIndex = graph.mappings.stopNodes[departureStop] ?: throw RouterException.stopNotFound(departureStop)

        // Dijkstra
        val Q = FibonacciHeap<Int,Long>()
        val dist = Array(graph.nodes.size) { Long.MAX_VALUE }
        val prev = Array<Int?>(graph.nodes.size) { null }
        val prevEdge = Array<Edge?>(graph.nodes.size) { null }

        dist[departureStopIndex] = 0
        Q.add(departureStopIndex, 0)

        while (!Q.isEmpty()) {
            val u = Q.pop()!!
            val neighbours = getNeighbours(u, departureTime + dist[u], departureTime)
            for (neighbour in neighbours) {
                val v = neighbour.index
                val alt = dist[u] + neighbour.cost
                if (alt < dist[v]) {
                    prev[v] = u
                    prevEdge[v] = neighbour.edge
                    dist[v] = alt
                    Q.add(v, alt)
                }
                if (neighbour.edge.type == EdgeType.TRAVEL) {
                    val tV = getNode(neighbour.edge.toNodeId).stopId
                    if (alt < dist[tV]) {
                        prev[tV] = u
                        prevEdge[tV] = neighbour.edge
                        dist[tV] = alt
                    }
                }
            }
        }

        if (prev[arrivalStopIndex] == null) throw RouterException.noJourneyFound()

        // Reconstruct journey
        var cursor = arrivalStopIndex
        val chain = mutableListOf<RaptorJourneyConnection>()
        var connection: RaptorJourneyConnection? = null
        var edgeType: EdgeType? = null

        while (cursor != departureStopIndex) {
            val edge = prevEdge[cursor]!!
            val node = getNode(edge.toNodeId)

            if (edgeType != edge.type) {
                if (connection != null) chain.add(0, connection)
                edgeType = edge.type
                connection = when (edge.type) {
                    EdgeType.TRAVEL -> {
                        RaptorJourneyConnection.Travel(
                            listOf(),
                            graph.mappings.routeIds[node.routeId!!],
                            graph.mappings.headings[node.headingId!!],
                            0,
                            edge.departureTime!! + edge.penalty,
                            0
                        )
                    }
                    EdgeType.TRANSFER, EdgeType.TRANSFER_NON_ADJUSTABLE -> {
                        RaptorJourneyConnection.Transfer(
                            listOf(),
                            0
                        )
                    }
                    else -> null
                }
                println("Pushed new connection of type ${connection} (edge type = ${edge.type})")
            }

            connection = when(edge.type) {
                EdgeType.TRAVEL -> {
                    val c = (connection as RaptorJourneyConnection.Travel)
                    c.copy(
                        stops = listOf(graph.mappings.stopIds[node.stopId]) + c.stops,
                        startTime = edge.departureTime!!,
                        travelTime = c.endTime - edge.departureTime
                    )
                }
                EdgeType.TRANSFER, EdgeType.TRANSFER_NON_ADJUSTABLE -> {
                    val c = (connection as RaptorJourneyConnection.Transfer)
                    c.copy(
                        stops = listOf(graph.mappings.stopIds[node.stopId]) + c.stops,
                        travelTime = c.travelTime + edge.penalty
                    )
                }
                else -> null
            }

            cursor = prev[cursor]!!
        }

        if (connection != null) chain.add(0, connection)
        val c = chain[0]
        chain[0] = when (c) {
            is RaptorJourneyConnection.Travel -> c.copy(
                stops = listOf(graph.mappings.stopIds[departureStopIndex]) + c.stops
            )
            is RaptorJourneyConnection.Transfer -> c.copy(
                stops = listOf(graph.mappings.stopIds[departureStopIndex]) + c.stops
            )
        }

        return RaptorJourney(chain)
    }

    private fun getNode(index: NodeIndex) = graph.nodes[index]

    private fun getNeighbours(index: NodeIndex, departureTime: DaySeconds, referencePoint: DaySeconds): List<NodeCost> {
        val node = getNode(index)
        val edges = node.edges

        return edges.mapNotNull {
            when (it.type) {
                EdgeType.STOP_ROUTE -> NodeCost(it.toNodeId, 0L, it)
                EdgeType.TRANSFER, EdgeType.TRANSFER_NON_ADJUSTABLE -> NodeCost(it.toNodeId, it.penalty, it)
                EdgeType.TRAVEL -> {
                    if (!servicesAreActive(it.availableServices)) return@mapNotNull null
                    val dt = it.departureTime ?: return@mapNotNull null
                    if (dt < departureTime) return@mapNotNull null
                    NodeCost(it.toNodeId, (dt - departureTime) + it.penalty, it)
                }
                else -> null
            }
        }
    }

    private fun servicesAreActive(
        serviceIndices: List<ServiceIndex>
    ): Boolean {
        for (i in serviceIndices) {
            if (activeServices.containsKey(i)) return true
        }
        return false
    }

}