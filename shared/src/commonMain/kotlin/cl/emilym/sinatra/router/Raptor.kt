package cl.emilym.sinatra.router

import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.router.data.EdgeType
import cl.emilym.sinatra.router.data.NetworkGraph
import cl.emilym.sinatra.router.data.NetworkGraphEdge
import kotlin.math.roundToInt
import kotlin.math.roundToLong

data class RaptorStop(
    val id: StopId,
    val addedTime: Seconds
)

data class RaptorConfig(
    val maximumWalkingTime: Seconds,
    val transferPenalty: Seconds,
    val changeOverPenalty: Seconds,
    val penaltyMultiplier: Float = 1000f,
)

class Raptor(
    private val graph: NetworkGraph,
    activeServices: List<List<ServiceId>>,
    private val config: RaptorConfig = RaptorConfig(
        Long.MAX_VALUE,
        0,
        0,
        1f,
    )
) {

    private val activeServices = activeServices.map {
        it.associate { graph.mappings.serviceIds.indexOf(it) to Unit }
    }

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
        val distP = Array(nodeCount) { Long.MAX_VALUE }
        val prev = Array<Int?>(nodeCount) { null }
        val prevEdge = Array<NetworkGraphEdge?>(nodeCount) { null }
        val dayIndex = Array<Int?>(nodeCount) { null }

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
                val altP = distP[u] + neighbour.cost
                val alt = dist[u] + neighbour.cost
                if (altP < distP[v]) {
                    prev[v] = u
                    prevEdge[v] = neighbour.edge
                    distP[v] = altP
                    dist[v] = alt
                    dayIndex[v] = neighbour.dayIndex
                    Q.add(v, alt)
                }
                if (neighbour.edge.type == EdgeType.TRAVEL) {
                    val tV = getNode(neighbour.edge.connectedNodeIndex.toInt()).stopIndex.toInt()
                    val addedCost = config.changeOverPenalty.let {
                        with(prevEdge[tV]) {
                            when (this?.type) {
                                EdgeType.TRAVEL -> with(getNode(prev[tV]!!)) {
                                    if (routeIndex != getNode(u).routeIndex)
                                        it
                                    else 0L
                                }
                                else -> 0L
                            }
                        }
                    }
                    val addedCostP = (altP + (addedCost * config.penaltyMultiplier)).roundToLong()
                    if ((altP + addedCostP) < distP[tV]) {
                        prev[tV] = u
                        prevEdge[tV] = neighbour.edge
                        distP[tV] = altP + addedCostP
                        dist[tV] = alt + addedCost
                        dayIndex[v] = neighbour.dayIndex
                        Q.add(tV, alt)
                    }
                }
            }
        }

        if (arrivalStopIndices.all { prev[it.first] == null }) throw RouterException.noJourneyFound()

        val options = arrivalStopIndices
            .filterNot { prev[it.first] == null }

        // Reconstruct journey
        var cursor = (
            options.filter {
                prevEdge[it.first]?.type != EdgeType.TRANSFER ||
                        (prevEdge[it.first]?.type == EdgeType.TRANSFER &&
                                (dist[it.first] + it.second.addedTime) >= config.maximumWalkingTime)
            }.nullIfEmpty()
        )?.minBy { dist[it.first] + it.second.addedTime }?.first ?: throw RouterException.noJourneyFound()

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
                            dayIndex[cursor] ?: 0,
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
                        travelTime = c.endTime - edge.departureTime.toLong(),
                        dayIndex = dayIndex[cursor] ?: 0
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

        return edges.flatMap {
            when (it.type) {
                EdgeType.UNWEIGHTED -> listOf(NodeCost(it.connectedNodeIndex.toInt(), 0L, it, null))
                EdgeType.TRANSFER, EdgeType.TRANSFER_NON_ADJUSTABLE -> {
                    if (ignoreTransfer) return@flatMap emptyList()
                    if (it.cost.toLong() > (config.maximumWalkingTime)) return@flatMap emptyList()
                    listOf(NodeCost(it.connectedNodeIndex.toInt(), it.cost.toLong() + (config.transferPenalty), it, null))
                }
                EdgeType.TRAVEL -> {
                    val daysActive = (-1..1).filter { d -> servicesAreActive(it.availableServices, d) }
                    if (daysActive.isEmpty()) return@flatMap emptyList()
                    daysActive.mapNotNull { d ->
                        val dt = it.departureTime.toInt() + (86400 * d)
                        if ((dt + 60) < departureTime) return@mapNotNull null
                        NodeCost(it.connectedNodeIndex.toInt(), (dt - departureTime) + it.cost.toLong(), it, d)
                    }
                }
            }
        }
    }

    private fun servicesAreActive(
        serviceIndices: List<ServiceIndex>,
        day: Int
    ): Boolean {
        for (i in serviceIndices) {
            if (activeServices[day + 1].containsKey(i.toInt())) return true
        }
        return false
    }

}