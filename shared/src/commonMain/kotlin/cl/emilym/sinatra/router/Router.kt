package cl.emilym.sinatra.router

import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.router.data.EdgeType
import cl.emilym.sinatra.router.data.NetworkGraph
import cl.emilym.sinatra.router.data.NetworkGraphEdge

data class RaptorStop(
    val id: StopId,
    val addedTime: Seconds
)

data class RaptorConfig(
    val maximumWalkingTime: Seconds,
    val transferTime: Seconds,
    val transferPenalty: Int,
    val changeOverTime: Seconds,
    val changeOverPenalty: Int,
)

data class RouterPrefs(
    val wheelchairAccessible: Boolean,
    val bikesAllowed: Boolean
)

internal val DEFAULT_ROUTER_PREFS = RouterPrefs(
    wheelchairAccessible = false,
    bikesAllowed = false
)

abstract class Router {

    protected abstract val graph: NetworkGraph
    protected abstract val activeServiceIds: List<List<ServiceId>>
    protected abstract val config: RaptorConfig
    protected abstract val prefs: RouterPrefs

    private val activeServices by lazy {
        activeServiceIds.map {
            it.associate { graph.mappings.serviceIds.indexOf(it) to Unit }
        }
    }

    fun calculate(
        anchorTime: DaySeconds,
        departureStop: StopId,
        arrivalStop: StopId
    ): RaptorJourney {
        return calculate(
            anchorTime,
            listOf(RaptorStop(departureStop, 0L)),
            listOf(RaptorStop(arrivalStop, 0L))
        )
    }

    fun calculate(
        anchorTime: DaySeconds,
        departureStops: List<RaptorStop>,
        arrivalStops: List<RaptorStop>
    ): RaptorJourney {
        return doCalculation(
            anchorTime,
            departureStops,
            arrivalStops
        )
    }

    protected abstract fun initializeDjikstra(
        Q: FibonacciHeap<Int, Long>,
        dist: Array<Long>,
        distP: Array<Long>,
        departureStopIndices: List<Pair<Int, RaptorStop>>,
        arrivalStopIndices: List<Pair<Int, RaptorStop>>
    )

    protected abstract fun calculateAnchorTime(
        anchorTime: DaySeconds,
        dist: Long
    ): Long

    private fun doCalculation(
        anchorTime: DaySeconds,
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
        val checked = mutableMapOf<Int, Unit>()
        val dist = Array(nodeCount) { Long.MAX_VALUE }
        val distP = Array(nodeCount) { Long.MAX_VALUE }
        val prev = Array<Int?>(nodeCount) { null }
        val prevEdge = Array<NetworkGraphEdge?>(nodeCount) { null }
        val dayIndex = Array<Int?>(nodeCount) { null }

        initializeDjikstra(Q, dist, distP, departureStopIndices, arrivalStopIndices)

        while (!Q.isEmpty()) {
            val u = Q.pop()!!
            if (checked[u] != null) continue
            checked[u] = Unit

            val neighbours = getNeighbours(
                u,
                calculateAnchorTime(anchorTime, dist[u]),
                prevEdge[u]?.type == EdgeType.TRANSFER
            )

            for (neighbour in neighbours) {
                val v = neighbour.index
                val altP = distP[u] + neighbour.costP
                val alt = dist[u] + neighbour.cost
                if (altP < distP[v]) {
                    prev[v] = u
                    prevEdge[v] = neighbour.edge
                    distP[v] = altP
                    dist[v] = alt
                    dayIndex[v] = neighbour.dayIndex
                    Q.add(v, altP)
                    checked.remove(v)
                }
                if (neighbour.edge.type == EdgeType.TRAVEL) {
                    val tV = getNode(neighbour.edge.connectedNodeIndex.toInt()).stopIndex.toInt()
                    val addedPenalty = config.changeOverPenalty
                    val addedTime = config.changeOverTime
                    if ((altP + addedPenalty) < distP[tV]) {
                        prev[tV] = u
                        prevEdge[tV] = neighbour.edge
                        distP[tV] = altP + addedPenalty
                        dist[tV] = alt + addedTime
                        dayIndex[v] = neighbour.dayIndex
                        Q.add(tV, altP)
                        checked.remove(v)
                    }
                }
            }
        }

        return reconstruct(
            arrivalStopIndices,
            departureStopIndices,
            prev,
            prevEdge,
            dist,
            dayIndex
        )
    }

    protected abstract fun reconstruct(
        arrivalStopIndices: List<Pair<Int, RaptorStop>>,
        departureStopIndices: List<Pair<Int, RaptorStop>>,
        prev: Array<Int?>,
        prevEdge: Array<NetworkGraphEdge?>,
        dist: Array<Long>,
        dayIndex: Array<Int?>
    ): RaptorJourney

    protected fun doReconstruction(
        startStopIndicies: List<Pair<Int, RaptorStop>>,
        endStopIndicies: List<Pair<Int, RaptorStop>>,
        prev: Array<Int?>,
        prevEdge: Array<NetworkGraphEdge?>,
        dist: Array<Long>,
        dayIndex: Array<Int?>
    ): List<GroupedGraphEdges> {
        if (startStopIndicies.all { prev[it.first] == null }) throw RouterException.noJourneyFound()

        val options = startStopIndicies
            .filterNot { prev[it.first] == null }

        // Reconstruct journey
        var cursor = (
                options.filter {
                    prevEdge[it.first]?.type != EdgeType.TRANSFER ||
                            (prevEdge[it.first]?.type == EdgeType.TRANSFER &&
                                    (dist[it.first] + it.second.addedTime) >= config.maximumWalkingTime)
                }.nullIfEmpty()
        )?.firstOrNull()?.first ?: throw RouterException.noJourneyFound()

        val chain = mutableListOf<GroupedGraphEdges>()
        val stops = mutableListOf<StopId>()
        val edges = mutableListOf<NetworkGraphEdge>()
        val dayIndicies = mutableListOf<Int?>()
        var edgeType: EdgeType? = null

        fun addConnection() {
            if (edges.isEmpty()) return
            when (edgeType) {
                EdgeType.TRAVEL -> GroupedGraphEdges.Travel(
                    stops.toList(),
                    edges.toList(),
                    dayIndicies.toList()
                )
                EdgeType.TRANSFER, EdgeType.TRANSFER_NON_ADJUSTABLE ->
                    GroupedGraphEdges.Transfer(
                        stops.toList(),
                        edges.toList()
                    )
                else -> null
            }?.let {
                chain.add(it)
            }
        }

        while (cursor !in endStopIndicies.map { it.first }) {
            val edge = prevEdge[cursor]!!
            val stopId = graph.mappings.stopIds[getNode(edge.connectedNodeIndex).stopIndex.toInt()]

            if (edgeType != edge.type) {
                stops.add(stopId)
                addConnection()

                edgeType = edge.type
                edges.clear()
                stops.clear()
                dayIndicies.clear()
            }

            stops.add(stopId)
            edges.add(edge)
            dayIndicies.add(dayIndex[cursor])

            cursor = prev[cursor]!!
        }

        stops.add(graph.mappings.stopIds[getNode(cursor).stopIndex.toInt()])
        addConnection()

        return chain.toList()
    }

    private fun getNode(index: NodeIndex) = graph.node(index)
    private fun getNode(index: UInt) = graph.node(index.toInt())

    abstract fun isValidTravelEdge(
        edge: NetworkGraphEdge,
        dayAdjustment: Seconds,
        anchorTime: DaySeconds
    ): Boolean

    abstract fun getTravelAnchorTime(
        edge: NetworkGraphEdge,
        dayAdjustment: Seconds,
        anchorTime: DaySeconds
    ): Long

    private fun getNeighbours(index: NodeIndex, anchorTime: DaySeconds, ignoreTransfer: Boolean = false): List<NodeCost> {
        val node = graph.node(index)
        val edges = node.edges

        return edges.flatMap {
            when (it.type) {
                EdgeType.UNWEIGHTED -> listOf(
                    NodeCost(it.connectedNodeIndex.toInt(), 0L, 0L, it, null)
                )
                EdgeType.TRANSFER, EdgeType.TRANSFER_NON_ADJUSTABLE -> {
                    if (ignoreTransfer) return@flatMap emptyList()
                    if (it.cost.toLong() > (config.maximumWalkingTime)) return@flatMap emptyList()
                    listOf(NodeCost(
                        it.connectedNodeIndex.toInt(),
                        it.cost.toLong() + config.transferTime,
                        it.cost.toLong() + config.transferPenalty,
                        it,
                        null
                    ))
                }
                EdgeType.TRAVEL -> {
                    val daysActive = (-1..1).filter { d -> servicesAreActive(it.availableServices, d) }
                    if (daysActive.isEmpty()) return@flatMap emptyList()
                    daysActive.mapNotNull { d ->
                        if (prefs.bikesAllowed && !it.bikesAllowed) return@mapNotNull null
                        if (prefs.wheelchairAccessible && !it.wheelchairAccessible) return@mapNotNull null
                        val dayAdjustment = (86400 * d).toLong()
                        if (!isValidTravelEdge(it, dayAdjustment, anchorTime)) return@mapNotNull null
                        val cost = getTravelAnchorTime(it, dayAdjustment, anchorTime) + it.cost.toLong()
                        NodeCost(it.connectedNodeIndex.toInt(), cost, cost, it, d)
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

    protected fun GroupedGraphEdges.Transfer.toRaptorJourneyConnection(
        stops: List<StopId>
    ): RaptorJourneyConnection.Transfer {
        return RaptorJourneyConnection.Transfer(
            stops,
            edges.sumOf { it.cost }.toLong()
        )
    }

    protected fun GroupedGraphEdges.Travel.toRaptorJourneyConnection(
        stops: List<StopId>,
        arrivalEdgeIndex: Int,
        departureEdgeIndex: Int
    ): RaptorJourneyConnection.Travel {
        val firstNode = getNode(edges.first().connectedNodeIndex)
        val arrival = edges[arrivalEdgeIndex].departureTime.toLong()
        val departure = edges[departureEdgeIndex].departureTime.toLong()
        return RaptorJourneyConnection.Travel(
            stops,
            graph.mappings.routeIds[firstNode.routeIndex.toInt()],
            graph.mappings.headings[firstNode.headingIndex.toInt()],
            departure,
            arrival,
            dayIndicies[departureEdgeIndex] ?: 0,
            arrival - departure
        )
    }

}