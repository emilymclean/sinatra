@file:OptIn(pbandk.PublicForGeneratedCode::class)

package cl.emilym.gtfs.networkgraph

@pbandk.Export
public sealed class EdgeType(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
    override fun equals(other: kotlin.Any?): Boolean = other is cl.emilym.gtfs.networkgraph.EdgeType && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "EdgeType.${name ?: "UNRECOGNIZED"}(value=$value)"

    public object STOP_ROUTE : EdgeType(0, "EDGE_TYPE_STOP_ROUTE")
    public object TRAVEL : EdgeType(1, "EDGE_TYPE_TRAVEL")
    public object TRANSFER : EdgeType(2, "EDGE_TYPE_TRANSFER")
    public object TRANSFER_NON_ADJUSTABLE : EdgeType(3, "EDGE_TYPE_TRANSFER_NON_ADJUSTABLE")
    public class UNRECOGNIZED(value: Int) : EdgeType(value)

    public companion object : pbandk.Message.Enum.Companion<cl.emilym.gtfs.networkgraph.EdgeType> {
        public val values: List<cl.emilym.gtfs.networkgraph.EdgeType> by lazy { listOf(STOP_ROUTE, TRAVEL, TRANSFER, TRANSFER_NON_ADJUSTABLE) }
        override fun fromValue(value: Int): cl.emilym.gtfs.networkgraph.EdgeType = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
        override fun fromName(name: String): cl.emilym.gtfs.networkgraph.EdgeType = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No EdgeType with name: $name")
    }
}

@pbandk.Export
public sealed class NodeType(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
    override fun equals(other: kotlin.Any?): Boolean = other is cl.emilym.gtfs.networkgraph.NodeType && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "NodeType.${name ?: "UNRECOGNIZED"}(value=$value)"

    public object STOP : NodeType(0, "NODE_TYPE_STOP")
    public object STOP_ROUTE : NodeType(1, "NODE_TYPE_STOP_ROUTE")
    public class UNRECOGNIZED(value: Int) : NodeType(value)

    public companion object : pbandk.Message.Enum.Companion<cl.emilym.gtfs.networkgraph.NodeType> {
        public val values: List<cl.emilym.gtfs.networkgraph.NodeType> by lazy { listOf(STOP, STOP_ROUTE) }
        override fun fromValue(value: Int): cl.emilym.gtfs.networkgraph.NodeType = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
        override fun fromName(name: String): cl.emilym.gtfs.networkgraph.NodeType = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No NodeType with name: $name")
    }
}

@pbandk.Export
public data class Graph(
    val nodes: List<cl.emilym.gtfs.networkgraph.Node> = emptyList(),
    val config: cl.emilym.gtfs.networkgraph.GraphConfiguration,
    val mappings: cl.emilym.gtfs.networkgraph.GraphMappings,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.networkgraph.Graph = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.networkgraph.Graph> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.networkgraph.Graph> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.networkgraph.Graph = cl.emilym.gtfs.networkgraph.Graph.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.networkgraph.Graph> = pbandk.MessageDescriptor(
            fullName = "networkgraph.Graph",
            messageClass = cl.emilym.gtfs.networkgraph.Graph::class,
            messageCompanion = this,
            fields = buildList(3) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "nodes",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.networkgraph.Node>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.networkgraph.Node.Companion)),
                        jsonName = "nodes",
                        value = cl.emilym.gtfs.networkgraph.Graph::nodes
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "config",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.networkgraph.GraphConfiguration.Companion),
                        jsonName = "config",
                        value = cl.emilym.gtfs.networkgraph.Graph::config
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "mappings",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.networkgraph.GraphMappings.Companion),
                        jsonName = "mappings",
                        value = cl.emilym.gtfs.networkgraph.Graph::mappings
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class GraphMappings(
    val stopNodes: Map<String?, Int?> = emptyMap(),
    val stopIds: List<String> = emptyList(),
    val routeIds: List<String> = emptyList(),
    val headings: List<String> = emptyList(),
    val serviceIds: List<String> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.networkgraph.GraphMappings = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.networkgraph.GraphMappings> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.networkgraph.GraphMappings> {
        public val defaultInstance: cl.emilym.gtfs.networkgraph.GraphMappings by lazy { cl.emilym.gtfs.networkgraph.GraphMappings() }
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.networkgraph.GraphMappings = cl.emilym.gtfs.networkgraph.GraphMappings.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.networkgraph.GraphMappings> = pbandk.MessageDescriptor(
            fullName = "networkgraph.GraphMappings",
            messageClass = cl.emilym.gtfs.networkgraph.GraphMappings::class,
            messageCompanion = this,
            fields = buildList(5) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stopNodes",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Map<String?, Int?>(keyType = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true), valueType = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true)),
                        jsonName = "stopNodes",
                        value = cl.emilym.gtfs.networkgraph.GraphMappings::stopNodes
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stopIds",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Repeated<String>(valueType = pbandk.FieldDescriptor.Type.Primitive.String()),
                        jsonName = "stopIds",
                        value = cl.emilym.gtfs.networkgraph.GraphMappings::stopIds
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "routeIds",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Repeated<String>(valueType = pbandk.FieldDescriptor.Type.Primitive.String()),
                        jsonName = "routeIds",
                        value = cl.emilym.gtfs.networkgraph.GraphMappings::routeIds
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "serviceIds",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Repeated<String>(valueType = pbandk.FieldDescriptor.Type.Primitive.String()),
                        jsonName = "serviceIds",
                        value = cl.emilym.gtfs.networkgraph.GraphMappings::serviceIds
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "headings",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Repeated<String>(valueType = pbandk.FieldDescriptor.Type.Primitive.String()),
                        jsonName = "headings",
                        value = cl.emilym.gtfs.networkgraph.GraphMappings::headings
                    )
                )
            }
        )
    }

    public data class StopNodesEntry(
        override val key: String? = null,
        override val value: Int? = null,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
    ) : pbandk.Message, Map.Entry<String?, Int?> {
        override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.networkgraph.GraphMappings.StopNodesEntry = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.networkgraph.GraphMappings.StopNodesEntry> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<cl.emilym.gtfs.networkgraph.GraphMappings.StopNodesEntry> {
            public val defaultInstance: cl.emilym.gtfs.networkgraph.GraphMappings.StopNodesEntry by lazy { cl.emilym.gtfs.networkgraph.GraphMappings.StopNodesEntry() }
            override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.networkgraph.GraphMappings.StopNodesEntry = cl.emilym.gtfs.networkgraph.GraphMappings.StopNodesEntry.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.networkgraph.GraphMappings.StopNodesEntry> = pbandk.MessageDescriptor(
                fullName = "networkgraph.GraphMappings.StopNodesEntry",
                messageClass = cl.emilym.gtfs.networkgraph.GraphMappings.StopNodesEntry::class,
                messageCompanion = this,
                fields = buildList(2) {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "key",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "key",
                            value = cl.emilym.gtfs.networkgraph.GraphMappings.StopNodesEntry::key
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "value",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                            jsonName = "value",
                            value = cl.emilym.gtfs.networkgraph.GraphMappings.StopNodesEntry::value
                        )
                    )
                }
            )
        }
    }
}

@pbandk.Export
public data class GraphConfiguration(
    val penaltyMultiplier: Double,
    val assumedWalkingSecondsPerKilometer: Long,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.networkgraph.GraphConfiguration = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.networkgraph.GraphConfiguration> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.networkgraph.GraphConfiguration> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.networkgraph.GraphConfiguration = cl.emilym.gtfs.networkgraph.GraphConfiguration.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.networkgraph.GraphConfiguration> = pbandk.MessageDescriptor(
            fullName = "networkgraph.GraphConfiguration",
            messageClass = cl.emilym.gtfs.networkgraph.GraphConfiguration::class,
            messageCompanion = this,
            fields = buildList(2) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "penaltyMultiplier",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        jsonName = "penaltyMultiplier",
                        value = cl.emilym.gtfs.networkgraph.GraphConfiguration::penaltyMultiplier
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "assumedWalkingSecondsPerKilometer",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt64(hasPresence = true),
                        jsonName = "assumedWalkingSecondsPerKilometer",
                        value = cl.emilym.gtfs.networkgraph.GraphConfiguration::assumedWalkingSecondsPerKilometer
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class Edge(
    val toNodeId: Int,
    val departureTime: Long? = null,
    val penalty: Long,
    val type: cl.emilym.gtfs.networkgraph.EdgeType,
    val availableServices: List<Int> = emptyList(),
    val accessibilityFlags: Int? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.networkgraph.Edge = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.networkgraph.Edge> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.networkgraph.Edge> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.networkgraph.Edge = cl.emilym.gtfs.networkgraph.Edge.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.networkgraph.Edge> = pbandk.MessageDescriptor(
            fullName = "networkgraph.Edge",
            messageClass = cl.emilym.gtfs.networkgraph.Edge::class,
            messageCompanion = this,
            fields = buildList(6) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "toNodeId",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "toNodeId",
                        value = cl.emilym.gtfs.networkgraph.Edge::toNodeId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "departureTime",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt64(hasPresence = true),
                        jsonName = "departureTime",
                        value = cl.emilym.gtfs.networkgraph.Edge::departureTime
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "penalty",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt64(hasPresence = true),
                        jsonName = "penalty",
                        value = cl.emilym.gtfs.networkgraph.Edge::penalty
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "accessibilityFlags",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "accessibilityFlags",
                        value = cl.emilym.gtfs.networkgraph.Edge::accessibilityFlags
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "type",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = cl.emilym.gtfs.networkgraph.EdgeType.Companion, hasPresence = true),
                        jsonName = "type",
                        value = cl.emilym.gtfs.networkgraph.Edge::type
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "availableServices",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Repeated<Int>(valueType = pbandk.FieldDescriptor.Type.Primitive.UInt32()),
                        jsonName = "availableServices",
                        value = cl.emilym.gtfs.networkgraph.Edge::availableServices
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class Node(
    val type: cl.emilym.gtfs.networkgraph.NodeType,
    val stopId: Int,
    val routeId: Int? = null,
    val headingId: Int? = null,
    val edges: List<cl.emilym.gtfs.networkgraph.Edge> = emptyList(),
    val accessibilityFlags: Int? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.networkgraph.Node = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.networkgraph.Node> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.networkgraph.Node> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.networkgraph.Node = cl.emilym.gtfs.networkgraph.Node.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.networkgraph.Node> = pbandk.MessageDescriptor(
            fullName = "networkgraph.Node",
            messageClass = cl.emilym.gtfs.networkgraph.Node::class,
            messageCompanion = this,
            fields = buildList(6) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "type",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = cl.emilym.gtfs.networkgraph.NodeType.Companion, hasPresence = true),
                        jsonName = "type",
                        value = cl.emilym.gtfs.networkgraph.Node::type
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stopId",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "stopId",
                        value = cl.emilym.gtfs.networkgraph.Node::stopId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "routeId",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "routeId",
                        value = cl.emilym.gtfs.networkgraph.Node::routeId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "edges",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.networkgraph.Edge>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.networkgraph.Edge.Companion)),
                        jsonName = "edges",
                        value = cl.emilym.gtfs.networkgraph.Node::edges
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "accessibilityFlags",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "accessibilityFlags",
                        value = cl.emilym.gtfs.networkgraph.Node::accessibilityFlags
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "headingId",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "headingId",
                        value = cl.emilym.gtfs.networkgraph.Node::headingId
                    )
                )
            }
        )
    }
}

private fun Graph.protoMergeImpl(plus: pbandk.Message?): Graph = (plus as? Graph)?.let {
    it.copy(
        nodes = nodes + plus.nodes,
        config = config.plus(plus.config),
        mappings = mappings.plus(plus.mappings),
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Graph.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Graph {
    var nodes: pbandk.ListWithSize.Builder<cl.emilym.gtfs.networkgraph.Node>? = null
    var config: cl.emilym.gtfs.networkgraph.GraphConfiguration? = null
    var mappings: cl.emilym.gtfs.networkgraph.GraphMappings? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> nodes = (nodes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.networkgraph.Node> }
            2 -> config = _fieldValue as cl.emilym.gtfs.networkgraph.GraphConfiguration
            3 -> mappings = _fieldValue as cl.emilym.gtfs.networkgraph.GraphMappings
        }
    }

    if (config == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("config")
    }
    if (mappings == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("mappings")
    }
    return Graph(pbandk.ListWithSize.Builder.fixed(nodes), config!!, mappings!!, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForGraphMappings")
public fun GraphMappings?.orDefault(): cl.emilym.gtfs.networkgraph.GraphMappings = this ?: GraphMappings.defaultInstance

private fun GraphMappings.protoMergeImpl(plus: pbandk.Message?): GraphMappings = (plus as? GraphMappings)?.let {
    it.copy(
        stopNodes = stopNodes + plus.stopNodes,
        stopIds = stopIds + plus.stopIds,
        routeIds = routeIds + plus.routeIds,
        headings = headings + plus.headings,
        serviceIds = serviceIds + plus.serviceIds,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun GraphMappings.Companion.decodeWithImpl(u: pbandk.MessageDecoder): GraphMappings {
    var stopNodes: pbandk.MessageMap.Builder<String?, Int?>? = null
    var stopIds: pbandk.ListWithSize.Builder<String>? = null
    var routeIds: pbandk.ListWithSize.Builder<String>? = null
    var headings: pbandk.ListWithSize.Builder<String>? = null
    var serviceIds: pbandk.ListWithSize.Builder<String>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> stopNodes = (stopNodes ?: pbandk.MessageMap.Builder()).apply { this.entries += _fieldValue as kotlin.sequences.Sequence<pbandk.MessageMap.Entry<String?, Int?>> }
            2 -> stopIds = (stopIds ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<String> }
            3 -> routeIds = (routeIds ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<String> }
            4 -> serviceIds = (serviceIds ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<String> }
            5 -> headings = (headings ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<String> }
        }
    }

    return GraphMappings(pbandk.MessageMap.Builder.fixed(stopNodes), pbandk.ListWithSize.Builder.fixed(stopIds), pbandk.ListWithSize.Builder.fixed(routeIds), pbandk.ListWithSize.Builder.fixed(headings),
        pbandk.ListWithSize.Builder.fixed(serviceIds), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForGraphMappingsStopNodesEntry")
public fun GraphMappings.StopNodesEntry?.orDefault(): cl.emilym.gtfs.networkgraph.GraphMappings.StopNodesEntry = this ?: GraphMappings.StopNodesEntry.defaultInstance

private fun GraphMappings.StopNodesEntry.protoMergeImpl(plus: pbandk.Message?): GraphMappings.StopNodesEntry = (plus as? GraphMappings.StopNodesEntry)?.let {
    it.copy(
        key = plus.key ?: key,
        value = plus.value ?: value,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun GraphMappings.StopNodesEntry.Companion.decodeWithImpl(u: pbandk.MessageDecoder): GraphMappings.StopNodesEntry {
    var key: String? = null
    var value: Int? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> key = _fieldValue as String
            2 -> value = _fieldValue as Int
        }
    }

    return GraphMappings.StopNodesEntry(key, value, unknownFields)
}

private fun GraphConfiguration.protoMergeImpl(plus: pbandk.Message?): GraphConfiguration = (plus as? GraphConfiguration)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun GraphConfiguration.Companion.decodeWithImpl(u: pbandk.MessageDecoder): GraphConfiguration {
    var penaltyMultiplier: Double? = null
    var assumedWalkingSecondsPerKilometer: Long? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            2 -> penaltyMultiplier = _fieldValue as Double
            3 -> assumedWalkingSecondsPerKilometer = _fieldValue as Long
        }
    }

    if (penaltyMultiplier == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("penaltyMultiplier")
    }
    if (assumedWalkingSecondsPerKilometer == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("assumedWalkingSecondsPerKilometer")
    }
    return GraphConfiguration(penaltyMultiplier!!, assumedWalkingSecondsPerKilometer!!, unknownFields)
}

private fun Edge.protoMergeImpl(plus: pbandk.Message?): Edge = (plus as? Edge)?.let {
    it.copy(
        departureTime = plus.departureTime ?: departureTime,
        availableServices = availableServices + plus.availableServices,
        accessibilityFlags = plus.accessibilityFlags ?: accessibilityFlags,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Edge.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Edge {
    var toNodeId: Int? = null
    var departureTime: Long? = null
    var penalty: Long? = null
    var type: cl.emilym.gtfs.networkgraph.EdgeType? = null
    var availableServices: pbandk.ListWithSize.Builder<Int>? = null
    var accessibilityFlags: Int? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            2 -> toNodeId = _fieldValue as Int
            3 -> departureTime = _fieldValue as Long
            4 -> penalty = _fieldValue as Long
            6 -> accessibilityFlags = _fieldValue as Int
            7 -> type = _fieldValue as cl.emilym.gtfs.networkgraph.EdgeType
            8 -> availableServices = (availableServices ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<Int> }
        }
    }

    if (toNodeId == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("toNodeId")
    }
    if (penalty == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("penalty")
    }
    if (type == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("type")
    }
    return Edge(toNodeId!!, departureTime, penalty!!, type!!,
        pbandk.ListWithSize.Builder.fixed(availableServices), accessibilityFlags, unknownFields)
}

private fun Node.protoMergeImpl(plus: pbandk.Message?): Node = (plus as? Node)?.let {
    it.copy(
        routeId = plus.routeId ?: routeId,
        headingId = plus.headingId ?: headingId,
        edges = edges + plus.edges,
        accessibilityFlags = plus.accessibilityFlags ?: accessibilityFlags,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Node.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Node {
    var type: cl.emilym.gtfs.networkgraph.NodeType? = null
    var stopId: Int? = null
    var routeId: Int? = null
    var headingId: Int? = null
    var edges: pbandk.ListWithSize.Builder<cl.emilym.gtfs.networkgraph.Edge>? = null
    var accessibilityFlags: Int? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            2 -> type = _fieldValue as cl.emilym.gtfs.networkgraph.NodeType
            3 -> stopId = _fieldValue as Int
            4 -> routeId = _fieldValue as Int
            5 -> edges = (edges ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.networkgraph.Edge> }
            6 -> accessibilityFlags = _fieldValue as Int
            7 -> headingId = _fieldValue as Int
        }
    }

    if (type == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("type")
    }
    if (stopId == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("stopId")
    }
    return Node(type!!, stopId!!, routeId, headingId,
        pbandk.ListWithSize.Builder.fixed(edges), accessibilityFlags, unknownFields)
}
