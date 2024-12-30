package cl.emilym.sinatra.router

import cl.emilym.gtfs.networkgraph.Edge
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import pbandk.ByteArr
import kotlin.experimental.and

// The number of bytes used to represent an edge, excluding available services
const val METADATA_BYTE_SIZE = 5 + 1 + 1 + 4 + 4 + 4 + 4
const val EDGE_BYTE_SIZE = 4 + 4 + 4 + 1
const val NODE_BYTE_SIZE = 4 + 4 + 4 + 1 + 4 + 4

data class DataAndSize<T>(
    val data: T,
    val size: Int
)

interface RandomByteReader {
    fun read(position: Int, out: ByteArray)
    fun read(position: Int): Byte
}

class ByteArrayRandomByteReader(
    val data: ByteArray
): RandomByteReader {

    override fun read(position: Int, out: ByteArray) {
        data.copyInto(out, 0, position, position + out.size)
    }

    override fun read(position: Int): Byte {
        return data[position]
    }

}

interface NetworkGraph {
    val metadata: NetworkGraphMetadata
    val mappings: NetworkGraphMappings

    fun node(index: Int): NetworkGraphNode

    companion object {

        fun forByteArray(byteArray: ByteArray): NetworkGraph {
            return DefaultNetworkGraph(ByteArrayRandomByteReader(byteArray))
        }

    }
}

private class DefaultNetworkGraph(
    private val data: RandomByteReader
): NetworkGraph {

    override val metadata: NetworkGraphMetadata by lazy { NetworkGraphMetadata(data) }
    override val mappings: NetworkGraphMappings by lazy { NetworkGraphMappings(METADATA_BYTE_SIZE, data) }

    override fun node(index: Int): NetworkGraphNode {
        val start = metadata.nodesStart.toInt() + (NODE_BYTE_SIZE * index)
        println("Node start = $start")
        return NetworkGraphNode(
            start,
            data,
            metadata.availableServicesLength.toInt(),
            metadata.edgesStart.toInt()
        )
    }

    override fun toString(): String {
        return "DefaultNetworkGraph(metadata=$metadata, mappings=$mappings)"
    }

}

abstract class NetworkGraphEntry(
    private val position: Int,
    private val data: RandomByteReader
) {

    companion object {
        private const val STRING_TERMINATOR = 0x00.toByte()
    }

    protected fun readString(offset: Int): DataAndSize<String> {
        val strBytes = mutableListOf<Byte>()
        var position = offset
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

class NetworkGraphMappings(
    position: Int,
    data: RandomByteReader
): NetworkGraphEntry(position, data) {
    val stopIds: List<StopId>
    val stopIdToIndex: Map<StopId, Int>
    val routeIds: List<RouteId>
    val headings: List<String>
    val serviceIds: List<ServiceId>

    init {
        val stopIds = mutableListOf<StopId>()
        val stopIdToIndex = mutableMapOf<StopId, Int>()
        val routeIds = mutableListOf<RouteId>()
        val headings = mutableListOf<String>()
        val serviceIds = mutableListOf<ServiceId>()

        val stopsCount = readUInt(0x00)
        val routesCount = readUInt(0x04)
        val headingCount = readUInt(0x08)
        val servicesCount = readUInt(0x0C)

        println("Stops count = $stopsCount")

        val paired = listOf(
            stopsCount to stopIds,
            routesCount to routeIds,
            headingCount to headings,
            servicesCount to serviceIds,
        )

        var cursor = 0x10
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

        this.stopIds = stopIds
        this.stopIdToIndex = stopIdToIndex
        this.routeIds = routeIds
        this.headings = headings
        this.serviceIds = serviceIds
    }

    override fun toString(): String {
        return "NetworkGraphMappings(stopIds=$stopIds, stopIdToIndex=$stopIdToIndex, routeIds=$routeIds, headings=$headings, serviceIds=$serviceIds)"
    }

}

class NetworkGraphMetadata(
    data: RandomByteReader,
): NetworkGraphEntry(5, data) {

    val version by lazy { readUInt(0x00, 1) }
    val availableServicesLength by lazy { readUInt(0x01, 1) }
    val nodesStart by lazy { readUInt(0x02) }
    val edgesStart by lazy { readUInt(0x06) }
    val penaltyMultiplier by lazy { readFloat(0x0A) }
    val assumedWalkingSecondsPerKilometer by lazy { readUInt(0x0E) }

    override fun toString(): String {
        return "NetworkGraphMetadata(version=$version, availableServicesLength=$availableServicesLength, nodesStart=$nodesStart, edgesStart=$edgesStart, penaltyMultiplier=$penaltyMultiplier, assumedWalkingSecondsPerKilometer=$assumedWalkingSecondsPerKilometer)"
    }

}

enum class NodeType {
    STOP, STOP_ROUTE
}

class NetworkGraphNode(
    position: Int,
    data: RandomByteReader,
    private val availableServicesLength: Int,
    private val edgesStartPosition: Int,
): NetworkGraphEntry(position, data) {

    val stopIndex by lazy { readUInt(0x00) }
    val routeIndex by lazy { readUInt(0x04) }
    val headingIndex by lazy { readUInt(0x08) }

    private val flags by lazy { readByte(0x0C) }
    private val edgePointer by lazy { readUInt(0x0D).toInt() }
    private val edgeCount by lazy { readUInt(0x11).toInt() }

    val type: NodeType get() = when (flags and 0b1) {
        0b0.toByte() -> NodeType.STOP
        else -> NodeType.STOP_ROUTE
    }

    val wheelchairAccessible: Boolean get() = (flags and 0b10) == 0b10.toByte()

    val edges: List<NetworkGraphEdge> by lazy {
        List(edgeCount) { i -> NetworkGraphEdge(
            edgesStartPosition + edgePointer + ((EDGE_BYTE_SIZE + availableServicesLength) * i),
            data,
            availableServicesLength
        ) }
    }

    override fun toString(): String {
        return "NetworkGraphNode(stopIndex=$stopIndex, routeIndex=$routeIndex, headingIndex=$headingIndex, type=$type, wheelchairAccessible=$wheelchairAccessible, edges=$edges)"
    }

}

enum class EdgeType {
    TRAVEL, UNWEIGHTED, TRANSFER, TRANSFER_NON_ADJUSTABLE
}

class NetworkGraphEdge(
    position: Int,
    data: RandomByteReader,
    private val availableServicesLength: Int
): NetworkGraphEntry(position, data) {
    val connectedNodeIndex by lazy { readUInt(0x00) }
    val cost by lazy { readUInt(0x04) }
    val departureTime by lazy { readUInt(0x08) }
    private val flags by lazy { readByte(0x0C + availableServicesLength) }

    val availableServices: List<UInt> by lazy {
        val bytes = readBytes(0x0C, availableServicesLength)
        val active = mutableListOf<UInt>()
        for (byteIndex in bytes.indices) {
            for (bitIndex in 0.until(8)) {
                val bit = (bytes[byteIndex].toInt() shr bitIndex)
                if (bit and 0b1 == 0b1) active.add((bitIndex + (byteIndex * 8)).toUInt())
            }
        }
        active.toList()
    }

    val type: EdgeType get() = when (flags and 0b11) {
        0b00.toByte() -> EdgeType.TRAVEL
        0b10.toByte() -> EdgeType.UNWEIGHTED
        0b01.toByte() -> EdgeType.TRANSFER_NON_ADJUSTABLE
        else -> EdgeType.TRANSFER
    }

    val wheelchairAccessible: Boolean get() = (flags and 0b100) == 0b100.toByte()
    val bikesAllowed: Boolean get() = (flags and 0b1000) == 0b1000.toByte()

    override fun toString(): String {
        return "NetworkGraphEdge(connectedNodeIndex=$connectedNodeIndex, cost=$cost, departureTime=$departureTime, availableServices=$availableServices, type=$type, wheelchairAccessible=$wheelchairAccessible, bikesAllowed=$bikesAllowed)"
    }

}