package cl.emilym.sinatra.router.data

import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.TripId

data class DataAndSize<T>(
    val data: T,
    val size: Int
)

interface NetworkGraph {
    val metadata: NetworkGraphMetadata
    val mappings: NetworkGraphMappings

    fun node(index: Int): NetworkGraphNode

    companion object {

        fun byteFormatForByteArray(byteArray: ByteArray): NetworkGraph {
            return ByteNetworkGraph(ByteArrayRandomByteReader(byteArray))
        }

    }
}

interface NetworkGraphMappings {
    val stopIds: List<StopId>
    val stopIdToIndex: Map<StopId, Int>
    val routeIds: List<RouteId>
    val headings: List<String>
    val tripIds: List<TripId>
    val serviceIds: List<ServiceId>
}

interface NetworkGraphMetadata {
    val version: UInt
    val availableServicesLength: UInt
    val nodesStart: UInt
    val edgesStart: UInt
    val penaltyMultiplier: Float
    val assumedWalkingSecondsPerKilometer: UInt
    val nodeCount: UInt
}

enum class NodeType {
    STOP, STOP_ROUTE
}

interface NetworkGraphNode {
    val stopIndex: UInt
    val type: NodeType
    val edges: List<NetworkGraphEdge>
}

interface StopNetworkGraphNode: NetworkGraphNode {
    val lat: Float
    val lng: Float
    val wheelchairAccessible: Boolean
}

interface RouteNetworkGraphNode: NetworkGraphNode {
    val routeIndex: UInt
    val headingIndex: UInt
}

val NetworkGraphNode.routeIndexCompat: UInt
    get() = when (this) {
        is RouteNetworkGraphNode -> routeIndex
        else -> 0U
    }

val NetworkGraphNode.headingIndexCompat: UInt
    get() = when (this) {
        is RouteNetworkGraphNode -> headingIndex
        else -> 0U
    }

enum class EdgeType {
    TRAVEL, TRANSFER, TO_STOP_NODE, TO_ROUTE_NODE
}

interface NetworkGraphEdge {
    val connectedNodeIndex: UInt
    val cost: UInt
    val departureTime: UInt
    val tripIndex: UInt
    val availableServices: List<UInt>
    val type: EdgeType
    val wheelchairAccessible: Boolean
    val bikesAllowed: Boolean
}

