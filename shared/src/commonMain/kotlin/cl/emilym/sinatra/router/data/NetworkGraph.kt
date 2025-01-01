package cl.emilym.sinatra.router.data

import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId

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
    val routeIndex: UInt
    val headingIndex: UInt
    val type: NodeType
    val wheelchairAccessible: Boolean
    val edges: List<NetworkGraphEdge>
}

enum class EdgeType {
    TRAVEL, UNWEIGHTED, TRANSFER, TRANSFER_NON_ADJUSTABLE
}

interface NetworkGraphEdge {
    val connectedNodeIndex: UInt
    val cost: UInt
    val departureTime: UInt
    val availableServices: List<UInt>
    val type: EdgeType
    val wheelchairAccessible: Boolean
    val bikesAllowed: Boolean
}

