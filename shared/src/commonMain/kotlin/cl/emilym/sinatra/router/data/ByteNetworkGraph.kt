package cl.emilym.sinatra.router.data

import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.TripId
import kotlin.experimental.and

const val METADATA_BYTE_SIZE =
    5 + // Magic Number
    1 + // Version
    1 + // Available Services Length
    4 + // Nodes Start
    4 + // Edges Start
    4 + // Penalty Multiplier
    4 + // assumedWalkingSecondsPerKilometer
    4   // Node count
// The number of bytes used to represent an edge, excluding available services
const val EDGE_BYTE_SIZE =
    4 + // Connected Node Index
    4 + // Cost
    4 + // Departure Time
    4 + // Trip Index
    1   // Flags
        // Available Services not here
const val NODE_BYTE_SIZE =
    4 + // Stop Index
    4 + // Latitude/Route Index
    4 + // Longitude/Heading Index
    1 + // Flags
    4 + // Edge Pointer
    4   // Edge Count

class ByteNetworkGraph(
    private val data: RandomByteReader
): NetworkGraph {

    override val metadata: ByteNetworkGraphMetadata by lazy { ByteNetworkGraphMetadata(data) }
    override val mappings: ByteNetworkGraphMappings by lazy {
        ByteNetworkGraphMappings(
            METADATA_BYTE_SIZE,
            data
        )
    }

    init {
        if (MagicNumberByteNetworkGraphEntry(data).magicNumber != "emily")
            throw IllegalStateException("Invalid network graph format")
        if (metadata.version != 2u)
            throw IllegalStateException("Invalid network graph version")
    }

    override fun node(index: Int): ByteNetworkGraphNode {
        val start = metadata.nodesStart.toInt() + (NODE_BYTE_SIZE * index)
        val type = data.read(start + 0x0C) and 0b1

        return when (type) {
            0b0.toByte() -> ByteStopNetworkGraphNode(
                start,
                data,
                metadata.availableServicesLength.toInt(),
                metadata.edgesStart.toInt()
            )
            else -> ByteRouteNetworkGraphNode(
                start,
                data,
                metadata.availableServicesLength.toInt(),
                metadata.edgesStart.toInt()
            )
        }
    }

    override fun toString(): String {
        return "DefaultNetworkGraph(metadata=$metadata, mappings=$mappings)"
    }

}

abstract class ByteNetworkGraphEntry(
    private val position: Int,
    private val data: RandomByteReader
) {

    companion object {
        private const val STRING_TERMINATOR = 0x00.toByte()
    }

    protected fun readString(offset: Int): DataAndSize<String> {
        val strBytes = mutableListOf<Byte>()
        var position = offset + this.position
        while(true) {
            val byte = data.read(position)
            if (byte == STRING_TERMINATOR) break
            strBytes.add(byte)
            position++
        }

        return DataAndSize(strBytes.toByteArray().decodeToString(), strBytes.size + 1)
    }

    protected fun readUInt(offset: Int, length: Int = 4): UInt {
        val intBytes = ByteArray(length).also { data.read(position + offset, it) }
        return intBytes.foldIndexed(0u) { i, acc, b -> acc or ((b.toUInt() and 0xFFu) shl (i * 8)) }
    }

    protected fun readInt(offset: Int, length: Int = 4): Int {
        val intBytes = ByteArray(length).also { data.read(this.position + offset, it) }
        return intBytes.foldIndexed(0) { i, acc, b -> acc or ((b.toInt() and 0xFF) shl (i * 8)) }
    }

    protected fun readFloat(offset: Int): Float {
        return Float.fromBits(readInt(offset))
    }

    protected fun readByte(offset: Int): Byte {
        return data.read(this.position + offset)
    }

    protected fun readBytes(offset: Int, length: Int): ByteArray {
        val bytes = ByteArray(length).also { data.read(this.position + offset, it) }
        return bytes
    }

}

private class MagicNumberByteNetworkGraphEntry(
    data: RandomByteReader
): ByteNetworkGraphEntry(0, data) {

    val magicNumber by lazy { readBytes(0x00, 5).decodeToString() }

}

class ByteNetworkGraphMappings(
    position: Int,
    data: RandomByteReader
): ByteNetworkGraphEntry(position, data), NetworkGraphMappings {
    override val stopIds: List<StopId>
    override val stopIdToIndex: Map<StopId, Int>
    override val routeIds: List<RouteId>
    override val headings: List<String>
    override val tripIds: List<TripId>
    override val serviceIds: List<ServiceId>

    init {
        val stopIds = mutableListOf<StopId>()
        val stopIdToIndex = mutableMapOf<StopId, Int>()
        val routeIds = mutableListOf<RouteId>()
        val headings = mutableListOf<String>()
        val tripIds = mutableListOf<String>()
        val serviceIds = mutableListOf<ServiceId>()

        val stopsCount = readUInt(0x00)
        val routesCount = readUInt(0x04)
        val headingCount = readUInt(0x08)
        val tripsCount = readUInt(0x0C)
        val servicesCount = readUInt(0x10)

        val paired = listOf(
            stopsCount to stopIds,
            routesCount to routeIds,
            headingCount to headings,
            tripsCount to tripIds,
            servicesCount to serviceIds,
        )

        var cursor = 0x14
        for (pi in paired.indices) {
            val p = paired[pi]
            val out = p.second
            for (i in 0.until(p.first.toInt())) {
                val str = readString(cursor)

                if (pi == 0) {
                    stopIdToIndex[str.data] = out.size
                }

                out.add(str.data)
                cursor += str.size
            }
        }

        this.stopIds = stopIds.toList()
        this.stopIdToIndex = stopIdToIndex.toMap()
        this.routeIds = routeIds.toList()
        this.headings = headings.toList()
        this.tripIds = tripIds.toList()
        this.serviceIds = serviceIds.toList()
    }

    override fun toString(): String {
        return "NetworkGraphMappings(stopIds=$stopIds, stopIdToIndex=$stopIdToIndex, routeIds=$routeIds, headings=$headings, serviceIds=$serviceIds)"
    }

}

class ByteNetworkGraphMetadata(
    data: RandomByteReader,
): ByteNetworkGraphEntry(5, data), NetworkGraphMetadata {

    override val version by lazy { readUInt(0x00, 1) }
    override val availableServicesLength by lazy { readUInt(0x01, 1) }
    override val nodesStart by lazy { readUInt(0x02) }
    override val edgesStart by lazy { readUInt(0x06) }
    override val penaltyMultiplier by lazy { readFloat(0x0A) }
    override val assumedWalkingSecondsPerKilometer by lazy { readUInt(0x0E) }
    override val nodeCount: UInt by lazy { readUInt(0x12) }

    override fun toString(): String {
        return "ByteNetworkGraphMetadata(version=$version, availableServicesLength=$availableServicesLength, nodesStart=$nodesStart, edgesStart=$edgesStart, penaltyMultiplier=$penaltyMultiplier, assumedWalkingSecondsPerKilometer=$assumedWalkingSecondsPerKilometer, nodeCount=$nodeCount)"
    }

}

abstract class ByteNetworkGraphNode(
    position: Int,
    data: RandomByteReader,
    private val availableServicesLength: Int,
    private val edgesStartPosition: Int,
): ByteNetworkGraphEntry(position, data), NetworkGraphNode {

    override val stopIndex by lazy { readUInt(0x00) }

    protected val flags by lazy { readByte(0x0C) }
    private val edgePointer by lazy { readUInt(0x0D).toInt() }
    private val edgeCount by lazy { readUInt(0x11).toInt() }

    override val type: NodeType
        get() = when (flags and 0b1) {
        0b0.toByte() -> NodeType.STOP
        else -> NodeType.STOP_ROUTE
    }

    override val edges: List<ByteNetworkGraphEdge> by lazy {
        List(edgeCount) { i ->
            ByteNetworkGraphEdge(
                edgesStartPosition + edgePointer + ((EDGE_BYTE_SIZE + availableServicesLength) * i),
                data,
                availableServicesLength
            )
        }
    }
}

class ByteStopNetworkGraphNode(
    position: Int,
    data: RandomByteReader,
    availableServicesLength: Int,
    edgesStartPosition: Int,
): ByteNetworkGraphNode(
    position, data, availableServicesLength, edgesStartPosition
), StopNetworkGraphNode {
    override val lat by lazy { readFloat(0x04) }
    override val lng by lazy { readFloat(0x08) }

    override val wheelchairAccessible: Boolean get() = (flags and 0b10) == 0b10.toByte()

    override fun toString(): String {
        return "StopNetworkGraphNode(stopIndex=$stopIndex, lat=$lat, lng=$lng, type=$type, wheelchairAccessible=$wheelchairAccessible, edges=$edges)"
    }
}

class ByteRouteNetworkGraphNode(
    position: Int,
    data: RandomByteReader,
    availableServicesLength: Int,
    edgesStartPosition: Int,
): ByteNetworkGraphNode(
    position, data, availableServicesLength, edgesStartPosition
), RouteNetworkGraphNode {
    override val routeIndex by lazy { readUInt(0x04) }
    override val headingIndex by lazy { readUInt(0x08) }

    override fun toString(): String {
        return "RouteNetworkGraphNode(stopIndex=$stopIndex, routeIndex=$routeIndex, headingIndex=$headingIndex, type=$type, edges=$edges)"
    }
}

class ByteNetworkGraphEdge(
    position: Int,
    data: RandomByteReader,
    private val availableServicesLength: Int
): ByteNetworkGraphEntry(position, data), NetworkGraphEdge {
    override val connectedNodeIndex by lazy { readUInt(0x00) }
    override val cost by lazy { readUInt(0x04) }
    override val departureTime by lazy { readUInt(0x08) }
    override val tripIndex by lazy { readUInt(0x0C) }
    private val flags by lazy { readByte(0x10 + availableServicesLength) }

    override val availableServices: List<UInt> by lazy {
        val bytes = readBytes(0x10, availableServicesLength)
        val active = mutableListOf<UInt>()
        for (byteIndex in bytes.indices) {
            for (bitIndex in 0.until(8)) {
                val bit = (bytes[byteIndex].toInt() shr bitIndex)
                if (bit and 0b1 == 0b1) active.add((bitIndex + (byteIndex * 8)).toUInt())
            }
        }
        active.toList()
    }

    override val type: EdgeType
        get() = when (flags and 0b11) {
        0b00.toByte() -> EdgeType.TRAVEL
        0b10.toByte() -> EdgeType.TRANSFER
        0b01.toByte() -> EdgeType.TO_STOP_NODE
        else -> EdgeType.TO_ROUTE_NODE
    }

    override val wheelchairAccessible: Boolean get() = (flags and 0b100) == 0b100.toByte()
    override val bikesAllowed: Boolean get() = (flags and 0b1000) == 0b1000.toByte()

    override fun toString(): String {
        return "NetworkGraphEdge(connectedNodeIndex=$connectedNodeIndex, cost=$cost, departureTime=$departureTime, availableServices=$availableServices, type=$type, wheelchairAccessible=$wheelchairAccessible, bikesAllowed=$bikesAllowed)"
    }

}