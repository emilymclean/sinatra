package cl.emilym.sinatra.router

import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.router.data.EdgeType
import cl.emilym.sinatra.router.data.NetworkGraph
import cl.emilym.sinatra.router.data.NetworkGraphEdge

data class RaptorStop(
    val id: StopId,
    val addedTime: Seconds
)

data class RaptorConfig(
    val maximumWalkingTime: Seconds,
    val transferPenalty: Seconds
)

class Raptor(
    private val graph: NetworkGraph,
    activeServices: List<ServiceId>,
    private val config: RaptorConfig? = null
) {

    private val activeServices = activeServices.associate { graph.mappings.serviceIds.indexOf(it) to Unit }

    fun calculate(
        departureTime: DaySeconds,
        departureStop: StopId,
        arrivalStop: StopId
    ): RaptorJourney {
        return calculate(
            departureTime,
            listOf(RaptorStop(departureStop, 0L)),
            listOf(RaptorStop(arrivalStop, 0L))
        )
    }

    fun calculate(
        departureTime: DaySeconds,
        departureStops: List<RaptorStop>,
        arrivalStops: List<RaptorStop>
    ): RaptorJourney {
        val arrivalStopIndices = arrivalStops.mapNotNull { stop -> graph.mappings.stopIdToIndex[stop.id]?.let { it to stop } }
        val departureStopIndices = departureStops.mapNotNull { stop -> graph.mappings.stopIdToIndex[stop.id]?.let { it to stop } }
        if (arrivalStopIndices.isEmpty() || departureStopIndices.isEmpty()) {
            throw RouterException.stopNotFound()
        }

        val nodeCount = graph.metadata.nodeCount.toInt()

        // Dijkstra
        val Q = FibonacciHeap<Int,Long>()
        val dist = Array(nodeCount) { Long.MAX_VALUE }
        val prev = Array<Int?>(nodeCount) { null }
        val prevEdge = Array<NetworkGraphEdge?>(nodeCount) { null }

        for (departureStopIndex in departureStopIndices) {
            dist[departureStopIndex.first] = departureStopIndex.second.addedTime
            Q.add(departureStopIndex.first, 0)
        }

        while (!Q.isEmpty()) {
            val u = Q.pop()!!
            val neighbours = getNeighbours(
                u,
                departureTime + dist[u],
                prevEdge[u]?.type == EdgeType.TRANSFER
            )

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
                    val tV = getNode(neighbour.edge.connectedNodeIndex.toInt()).stopIndex.toInt()
                    if (alt < dist[tV]) {
                        prev[tV] = u
                        prevEdge[tV] = neighbour.edge
                        dist[tV] = alt
                        Q.add(tV, alt)
                    }
                }
            }
        }

        if (arrivalStopIndices.all { prev[it.first] == null }) throw RouterException.noJourneyFound()

        // Reconstruct journey
        var cursor = arrivalStopIndices.filterNot { prev[it.first] == null }.minBy { dist[it.first] + it.second.addedTime }.first
        val chain = mutableListOf<RaptorJourneyConnection>()
        var connection: RaptorJourneyConnection? = null
        var edgeType: EdgeType? = null

        fun addConnection(c: RaptorJourneyConnection) {
            val c = when (c) {
                is RaptorJourneyConnection.Travel -> c.copy(
                    stops = listOf(graph.mappings.stopIds[getNode(cursor).stopIndex.toInt()]) + c.stops
                )
                is RaptorJourneyConnection.Transfer -> c.copy(
                    stops = listOf(graph.mappings.stopIds[getNode(cursor).stopIndex.toInt()]) + c.stops
                )
            }
            chain.add(0, c)
        }

        while (cursor !in departureStopIndices.map { it.first }) {
            val edge = prevEdge[cursor]!!
            val node = getNode(edge.connectedNodeIndex.toInt())

            if (edgeType != edge.type) {
                if (connection != null) addConnection(connection)
                edgeType = edge.type
                connection = when (edge.type) {
                    EdgeType.TRAVEL -> {
                        RaptorJourneyConnection.Travel(
                            listOf(),
                            graph.mappings.routeIds[node.routeIndex.toInt()],
                            graph.mappings.headings[node.headingIndex.toInt()],
                            0,
                            edge.departureTime.toLong() + edge.cost.toLong(),
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
            }

            connection = when(edge.type) {
                EdgeType.TRAVEL -> {
                    val c = (connection as RaptorJourneyConnection.Travel)
                    c.copy(
                        stops = listOf(graph.mappings.stopIds[node.stopIndex.toInt()]) + c.stops,
                        startTime = edge.departureTime.toLong(),
                        travelTime = c.endTime - edge.departureTime.toLong()
                    )
                }
                EdgeType.TRANSFER, EdgeType.TRANSFER_NON_ADJUSTABLE -> {
                    val c = (connection as RaptorJourneyConnection.Transfer)
                    c.copy(
                        stops = listOf(graph.mappings.stopIds[node.stopIndex.toInt()]) + c.stops,
                        travelTime = c.travelTime + edge.cost.toLong()
                    )
                }
                else -> null
            }

            cursor = prev[cursor]!!
        }

        if (connection != null) addConnection(connection)

        return RaptorJourney(chain)
    }

    private fun getNode(index: NodeIndex) = graph.node(index)

    private fun getNeighbours(index: NodeIndex, departureTime: DaySeconds, ignoreTransfer: Boolean = false): List<NodeCost> {
        val node = graph.node(index)
        val edges = node.edges

        return edges.mapNotNull {
            when (it.type) {
                EdgeType.UNWEIGHTED -> NodeCost(it.connectedNodeIndex.toInt(), 0L, it)
                EdgeType.TRANSFER, EdgeType.TRANSFER_NON_ADJUSTABLE -> {
                    if (ignoreTransfer) return@mapNotNull null
                    if (it.cost.toLong() > (config?.maximumWalkingTime ?: Long.MAX_VALUE)) return@mapNotNull null
                    NodeCost(it.connectedNodeIndex.toInt(), it.cost.toLong() + (config?.transferPenalty ?: 0L), it)
                }
                EdgeType.TRAVEL -> {
                    if (!servicesAreActive(it.availableServices)) return@mapNotNull null
                    val dt = it.departureTime.toInt()
                    if ((dt + 60) < departureTime) return@mapNotNull null
                    NodeCost(it.connectedNodeIndex.toInt(), (dt - departureTime) + it.cost.toLong(), it)
                }
                else -> null
            }
        }
    }

    private fun servicesAreActive(
        serviceIndices: List<ServiceIndex>
    ): Boolean {
        for (i in serviceIndices) {
            if (activeServices.containsKey(i.toInt())) return true
        }
        return false
    }

}