@file:OptIn(pbandk.PublicForGeneratedCode::class)

package cl.emilym.gtfs

@pbandk.Export
public sealed class WheelchairStopAccessibility(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
    override fun equals(other: kotlin.Any?): Boolean = other is cl.emilym.gtfs.WheelchairStopAccessibility && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "WheelchairStopAccessibility.${name ?: "UNRECOGNIZED"}(value=$value)"

    public object UNKNOWN : WheelchairStopAccessibility(0, "WHEELCHAIR_STOP_ACCESSIBILITY_UNKNOWN")
    public object NONE : WheelchairStopAccessibility(1, "WHEELCHAIR_STOP_ACCESSIBILITY_NONE")
    public object PARTIAL : WheelchairStopAccessibility(2, "WHEELCHAIR_STOP_ACCESSIBILITY_PARTIAL")
    public object FULL : WheelchairStopAccessibility(3, "WHEELCHAIR_STOP_ACCESSIBILITY_FULL")
    public class UNRECOGNIZED(value: Int) : WheelchairStopAccessibility(value)

    public companion object : pbandk.Message.Enum.Companion<cl.emilym.gtfs.WheelchairStopAccessibility> {
        public val values: List<cl.emilym.gtfs.WheelchairStopAccessibility> by lazy { listOf(UNKNOWN, NONE, PARTIAL, FULL) }
        override fun fromValue(value: Int): cl.emilym.gtfs.WheelchairStopAccessibility = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
        override fun fromName(name: String): cl.emilym.gtfs.WheelchairStopAccessibility = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No WheelchairStopAccessibility with name: $name")
    }
}

@pbandk.Export
public sealed class RouteType(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
    override fun equals(other: kotlin.Any?): Boolean = other is cl.emilym.gtfs.RouteType && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "RouteType.${name ?: "UNRECOGNIZED"}(value=$value)"

    public object UNKNOWN : RouteType(0, "ROUTE_TYPE_UNKNOWN")
    public object TRAM : RouteType(1, "ROUTE_TYPE_TRAM")
    public object METRO : RouteType(2, "ROUTE_TYPE_METRO")
    public object RAIL : RouteType(3, "ROUTE_TYPE_RAIL")
    public object BUS : RouteType(4, "ROUTE_TYPE_BUS")
    public object FERRY : RouteType(5, "ROUTE_TYPE_FERRY")
    public class UNRECOGNIZED(value: Int) : RouteType(value)

    public companion object : pbandk.Message.Enum.Companion<cl.emilym.gtfs.RouteType> {
        public val values: List<cl.emilym.gtfs.RouteType> by lazy { listOf(UNKNOWN, TRAM, METRO, RAIL, BUS, FERRY) }
        override fun fromValue(value: Int): cl.emilym.gtfs.RouteType = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
        override fun fromName(name: String): cl.emilym.gtfs.RouteType = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No RouteType with name: $name")
    }
}

@pbandk.Export
public sealed class ServiceBikesAllowed(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
    override fun equals(other: kotlin.Any?): Boolean = other is cl.emilym.gtfs.ServiceBikesAllowed && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "ServiceBikesAllowed.${name ?: "UNRECOGNIZED"}(value=$value)"

    public object UNKNOWN : ServiceBikesAllowed(0, "SERVICE_BIKES_ALLOWED_UNKNOWN")
    public object ALLOWED : ServiceBikesAllowed(1, "SERVICE_BIKES_ALLOWED_ALLOWED")
    public object DISALLOWED : ServiceBikesAllowed(2, "SERVICE_BIKES_ALLOWED_DISALLOWED")
    public class UNRECOGNIZED(value: Int) : ServiceBikesAllowed(value)

    public companion object : pbandk.Message.Enum.Companion<cl.emilym.gtfs.ServiceBikesAllowed> {
        public val values: List<cl.emilym.gtfs.ServiceBikesAllowed> by lazy { listOf(UNKNOWN, ALLOWED, DISALLOWED) }
        override fun fromValue(value: Int): cl.emilym.gtfs.ServiceBikesAllowed = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
        override fun fromName(name: String): cl.emilym.gtfs.ServiceBikesAllowed = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No ServiceBikesAllowed with name: $name")
    }
}

@pbandk.Export
public sealed class ServiceWheelchairAccessible(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
    override fun equals(other: kotlin.Any?): Boolean = other is cl.emilym.gtfs.ServiceWheelchairAccessible && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "ServiceWheelchairAccessible.${name ?: "UNRECOGNIZED"}(value=$value)"

    public object UNKNOWN : ServiceWheelchairAccessible(0, "SERVICE_WHEELCHAIR_ACCESSIBLE_UNKNOWN")
    public object ACCESSIBLE : ServiceWheelchairAccessible(1, "SERVICE_WHEELCHAIR_ACCESSIBLE_ACCESSIBLE")
    public object INACCESSIBLE : ServiceWheelchairAccessible(2, "SERVICE_WHEELCHAIR_ACCESSIBLE_INACCESSIBLE")
    public class UNRECOGNIZED(value: Int) : ServiceWheelchairAccessible(value)

    public companion object : pbandk.Message.Enum.Companion<cl.emilym.gtfs.ServiceWheelchairAccessible> {
        public val values: List<cl.emilym.gtfs.ServiceWheelchairAccessible> by lazy { listOf(UNKNOWN, ACCESSIBLE, INACCESSIBLE) }
        override fun fromValue(value: Int): cl.emilym.gtfs.ServiceWheelchairAccessible = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
        override fun fromName(name: String): cl.emilym.gtfs.ServiceWheelchairAccessible = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No ServiceWheelchairAccessible with name: $name")
    }
}

@pbandk.Export
public sealed class MultipleQualifier(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
    override fun equals(other: kotlin.Any?): Boolean = other is cl.emilym.gtfs.MultipleQualifier && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "MultipleQualifier.${name ?: "UNRECOGNIZED"}(value=$value)"

    public object SOME : MultipleQualifier(0, "MULTIPLE_QUALIFIER_SOME")
    public object ALL : MultipleQualifier(1, "MULTIPLE_QUALIFIER_ALL")
    public class UNRECOGNIZED(value: Int) : MultipleQualifier(value)

    public companion object : pbandk.Message.Enum.Companion<cl.emilym.gtfs.MultipleQualifier> {
        public val values: List<cl.emilym.gtfs.MultipleQualifier> by lazy { listOf(SOME, ALL) }
        override fun fromValue(value: Int): cl.emilym.gtfs.MultipleQualifier = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
        override fun fromName(name: String): cl.emilym.gtfs.MultipleQualifier = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No MultipleQualifier with name: $name")
    }
}

@pbandk.Export
public sealed class TimetableServiceExceptionType(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
    override fun equals(other: kotlin.Any?): Boolean = other is cl.emilym.gtfs.TimetableServiceExceptionType && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "TimetableServiceExceptionType.${name ?: "UNRECOGNIZED"}(value=$value)"

    public object ADDED : TimetableServiceExceptionType(1, "TIMETABLE_SERVICE_EXCEPTION_TYPE_ADDED")
    public object REMOVED : TimetableServiceExceptionType(2, "TIMETABLE_SERVICE_EXCEPTION_TYPE_REMOVED")
    public class UNRECOGNIZED(value: Int) : TimetableServiceExceptionType(value)

    public companion object : pbandk.Message.Enum.Companion<cl.emilym.gtfs.TimetableServiceExceptionType> {
        public val values: List<cl.emilym.gtfs.TimetableServiceExceptionType> by lazy { listOf(ADDED, REMOVED) }
        override fun fromValue(value: Int): cl.emilym.gtfs.TimetableServiceExceptionType = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
        override fun fromName(name: String): cl.emilym.gtfs.TimetableServiceExceptionType = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No TimetableServiceExceptionType with name: $name")
    }
}

@pbandk.Export
public data class StopEndpoint(
    val stop: List<cl.emilym.gtfs.Stop> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.StopEndpoint = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.StopEndpoint> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.StopEndpoint> {
        public val defaultInstance: cl.emilym.gtfs.StopEndpoint by lazy { cl.emilym.gtfs.StopEndpoint() }
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.StopEndpoint = cl.emilym.gtfs.StopEndpoint.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.StopEndpoint> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.StopEndpoint",
            messageClass = cl.emilym.gtfs.StopEndpoint::class,
            messageCompanion = this,
            fields = buildList(1) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.Stop>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.Stop.Companion)),
                        jsonName = "stop",
                        value = cl.emilym.gtfs.StopEndpoint::stop
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class RouteEndpoint(
    val route: List<cl.emilym.gtfs.Route> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.RouteEndpoint = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.RouteEndpoint> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.RouteEndpoint> {
        public val defaultInstance: cl.emilym.gtfs.RouteEndpoint by lazy { cl.emilym.gtfs.RouteEndpoint() }
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.RouteEndpoint = cl.emilym.gtfs.RouteEndpoint.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.RouteEndpoint> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.RouteEndpoint",
            messageClass = cl.emilym.gtfs.RouteEndpoint::class,
            messageCompanion = this,
            fields = buildList(1) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "route",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.Route>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.Route.Companion)),
                        jsonName = "route",
                        value = cl.emilym.gtfs.RouteEndpoint::route
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class StopDetailEndpoint(
    val stop: cl.emilym.gtfs.Stop,
    val children: List<cl.emilym.gtfs.Stop> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.StopDetailEndpoint = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.StopDetailEndpoint> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.StopDetailEndpoint> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.StopDetailEndpoint = cl.emilym.gtfs.StopDetailEndpoint.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.StopDetailEndpoint> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.StopDetailEndpoint",
            messageClass = cl.emilym.gtfs.StopDetailEndpoint::class,
            messageCompanion = this,
            fields = buildList(2) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.Stop.Companion),
                        jsonName = "stop",
                        value = cl.emilym.gtfs.StopDetailEndpoint::stop
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "children",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.Stop>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.Stop.Companion)),
                        jsonName = "children",
                        value = cl.emilym.gtfs.StopDetailEndpoint::children
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class RouteDetailEndpoint(
    val route: cl.emilym.gtfs.Route,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.RouteDetailEndpoint = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.RouteDetailEndpoint> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.RouteDetailEndpoint> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.RouteDetailEndpoint = cl.emilym.gtfs.RouteDetailEndpoint.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.RouteDetailEndpoint> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.RouteDetailEndpoint",
            messageClass = cl.emilym.gtfs.RouteDetailEndpoint::class,
            messageCompanion = this,
            fields = buildList(1) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "route",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.Route.Companion),
                        jsonName = "route",
                        value = cl.emilym.gtfs.RouteDetailEndpoint::route
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class ServiceEndpoint(
    val service: List<cl.emilym.gtfs.Service> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.ServiceEndpoint = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.ServiceEndpoint> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.ServiceEndpoint> {
        public val defaultInstance: cl.emilym.gtfs.ServiceEndpoint by lazy { cl.emilym.gtfs.ServiceEndpoint() }
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.ServiceEndpoint = cl.emilym.gtfs.ServiceEndpoint.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.ServiceEndpoint> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.ServiceEndpoint",
            messageClass = cl.emilym.gtfs.ServiceEndpoint::class,
            messageCompanion = this,
            fields = buildList(1) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "service",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.Service>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.Service.Companion)),
                        jsonName = "service",
                        value = cl.emilym.gtfs.ServiceEndpoint::service
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class RouteTimetableEndpoint(
    val routeId: String,
    val serviceId: String,
    val trips: List<cl.emilym.gtfs.RouteTripInformation> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.RouteTimetableEndpoint = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.RouteTimetableEndpoint> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.RouteTimetableEndpoint> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.RouteTimetableEndpoint = cl.emilym.gtfs.RouteTimetableEndpoint.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.RouteTimetableEndpoint> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.RouteTimetableEndpoint",
            messageClass = cl.emilym.gtfs.RouteTimetableEndpoint::class,
            messageCompanion = this,
            fields = buildList(3) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "routeId",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "routeId",
                        value = cl.emilym.gtfs.RouteTimetableEndpoint::routeId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "serviceId",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "serviceId",
                        value = cl.emilym.gtfs.RouteTimetableEndpoint::serviceId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "trips",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.RouteTripInformation>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.RouteTripInformation.Companion)),
                        jsonName = "trips",
                        value = cl.emilym.gtfs.RouteTimetableEndpoint::trips
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class RouteServicesEndpoint(
    val serviceIds: List<String> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.RouteServicesEndpoint = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.RouteServicesEndpoint> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.RouteServicesEndpoint> {
        public val defaultInstance: cl.emilym.gtfs.RouteServicesEndpoint by lazy { cl.emilym.gtfs.RouteServicesEndpoint() }
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.RouteServicesEndpoint = cl.emilym.gtfs.RouteServicesEndpoint.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.RouteServicesEndpoint> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.RouteServicesEndpoint",
            messageClass = cl.emilym.gtfs.RouteServicesEndpoint::class,
            messageCompanion = this,
            fields = buildList(1) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "serviceIds",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<String>(valueType = pbandk.FieldDescriptor.Type.Primitive.String()),
                        jsonName = "serviceIds",
                        value = cl.emilym.gtfs.RouteServicesEndpoint::serviceIds
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class Stop(
    val id: String,
    val parentStation: String? = null,
    val name: String,
    val location: cl.emilym.gtfs.Location,
    val accessibility: cl.emilym.gtfs.StopAccessibility,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.Stop = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.Stop> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.Stop> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.Stop = cl.emilym.gtfs.Stop.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.Stop> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.Stop",
            messageClass = cl.emilym.gtfs.Stop::class,
            messageCompanion = this,
            fields = buildList(5) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "id",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "id",
                        value = cl.emilym.gtfs.Stop::id
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "name",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "name",
                        value = cl.emilym.gtfs.Stop::name
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "location",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.Location.Companion),
                        jsonName = "location",
                        value = cl.emilym.gtfs.Stop::location
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "accessibility",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.StopAccessibility.Companion),
                        jsonName = "accessibility",
                        value = cl.emilym.gtfs.Stop::accessibility
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "parentStation",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "parentStation",
                        value = cl.emilym.gtfs.Stop::parentStation
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class StopAccessibility(
    val stopWheelchairAccessible: cl.emilym.gtfs.WheelchairStopAccessibility,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.StopAccessibility = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.StopAccessibility> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.StopAccessibility> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.StopAccessibility = cl.emilym.gtfs.StopAccessibility.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.StopAccessibility> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.StopAccessibility",
            messageClass = cl.emilym.gtfs.StopAccessibility::class,
            messageCompanion = this,
            fields = buildList(1) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stopWheelchairAccessible",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = cl.emilym.gtfs.WheelchairStopAccessibility.Companion, hasPresence = true),
                        jsonName = "stopWheelchairAccessible",
                        value = cl.emilym.gtfs.StopAccessibility::stopWheelchairAccessible
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class Route(
    val id: String,
    val code: String,
    val displayCode: String? = null,
    val colors: cl.emilym.gtfs.ColorPair? = null,
    val name: String,
    val designation: String? = null,
    val type: cl.emilym.gtfs.RouteType,
    val realTimeUrl: String? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.Route = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.Route> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.Route> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.Route = cl.emilym.gtfs.Route.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.Route> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.Route",
            messageClass = cl.emilym.gtfs.Route::class,
            messageCompanion = this,
            fields = buildList(8) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "id",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "id",
                        value = cl.emilym.gtfs.Route::id
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "code",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "code",
                        value = cl.emilym.gtfs.Route::code
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "name",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "name",
                        value = cl.emilym.gtfs.Route::name
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "type",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = cl.emilym.gtfs.RouteType.Companion, hasPresence = true),
                        jsonName = "type",
                        value = cl.emilym.gtfs.Route::type
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "displayCode",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "displayCode",
                        value = cl.emilym.gtfs.Route::displayCode
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "colors",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.ColorPair.Companion),
                        jsonName = "colors",
                        value = cl.emilym.gtfs.Route::colors
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "designation",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "designation",
                        value = cl.emilym.gtfs.Route::designation
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "realTimeUrl",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "realTimeUrl",
                        value = cl.emilym.gtfs.Route::realTimeUrl
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class ColorPair(
    val color: String,
    val onColor: String,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.ColorPair = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.ColorPair> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.ColorPair> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.ColorPair = cl.emilym.gtfs.ColorPair.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.ColorPair> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.ColorPair",
            messageClass = cl.emilym.gtfs.ColorPair::class,
            messageCompanion = this,
            fields = buildList(2) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "color",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "color",
                        value = cl.emilym.gtfs.ColorPair::color
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "onColor",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "onColor",
                        value = cl.emilym.gtfs.ColorPair::onColor
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class StopTimetable(
    val times: List<cl.emilym.gtfs.StopTimetableTime> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.StopTimetable = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.StopTimetable> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.StopTimetable> {
        public val defaultInstance: cl.emilym.gtfs.StopTimetable by lazy { cl.emilym.gtfs.StopTimetable() }
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.StopTimetable = cl.emilym.gtfs.StopTimetable.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.StopTimetable> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.StopTimetable",
            messageClass = cl.emilym.gtfs.StopTimetable::class,
            messageCompanion = this,
            fields = buildList(1) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "times",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.StopTimetableTime>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.StopTimetableTime.Companion)),
                        jsonName = "times",
                        value = cl.emilym.gtfs.StopTimetable::times
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class StopTimetableTime(
    val childStopId: String? = null,
    val routeId: String,
    val routeCode: String,
    val serviceId: String,
    val arrivalTime: String,
    val departureTime: String,
    val heading: String,
    val sequence: Int,
    val accessibility: cl.emilym.gtfs.ServiceAccessibility,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.StopTimetableTime = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.StopTimetableTime> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.StopTimetableTime> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.StopTimetableTime = cl.emilym.gtfs.StopTimetableTime.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.StopTimetableTime> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.StopTimetableTime",
            messageClass = cl.emilym.gtfs.StopTimetableTime::class,
            messageCompanion = this,
            fields = buildList(9) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "routeId",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "routeId",
                        value = cl.emilym.gtfs.StopTimetableTime::routeId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "routeCode",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "routeCode",
                        value = cl.emilym.gtfs.StopTimetableTime::routeCode
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "serviceId",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "serviceId",
                        value = cl.emilym.gtfs.StopTimetableTime::serviceId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "arrivalTime",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "arrivalTime",
                        value = cl.emilym.gtfs.StopTimetableTime::arrivalTime
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "departureTime",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "departureTime",
                        value = cl.emilym.gtfs.StopTimetableTime::departureTime
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "heading",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "heading",
                        value = cl.emilym.gtfs.StopTimetableTime::heading
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "sequence",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "sequence",
                        value = cl.emilym.gtfs.StopTimetableTime::sequence
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "accessibility",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.ServiceAccessibility.Companion),
                        jsonName = "accessibility",
                        value = cl.emilym.gtfs.StopTimetableTime::accessibility
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "childStopId",
                        number = 9,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "childStopId",
                        value = cl.emilym.gtfs.StopTimetableTime::childStopId
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class Service(
    val id: String,
    val regular: List<cl.emilym.gtfs.TimetableServiceRegular> = emptyList(),
    val exception: List<cl.emilym.gtfs.TimetableServiceException> = emptyList(),
    val accessibility: cl.emilym.gtfs.ServiceAccessibility? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.Service = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.Service> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.Service> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.Service = cl.emilym.gtfs.Service.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.Service> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.Service",
            messageClass = cl.emilym.gtfs.Service::class,
            messageCompanion = this,
            fields = buildList(4) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "id",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "id",
                        value = cl.emilym.gtfs.Service::id
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "regular",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.TimetableServiceRegular>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.TimetableServiceRegular.Companion)),
                        jsonName = "regular",
                        value = cl.emilym.gtfs.Service::regular
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "exception",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.TimetableServiceException>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.TimetableServiceException.Companion)),
                        jsonName = "exception",
                        value = cl.emilym.gtfs.Service::exception
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "accessibility",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.ServiceAccessibility.Companion),
                        jsonName = "accessibility",
                        value = cl.emilym.gtfs.Service::accessibility
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class ServiceAccessibility(
    val bikesAllowed: cl.emilym.gtfs.ServiceBikesAllowed,
    val bikesAllowedAppliesToAllTrips: cl.emilym.gtfs.MultipleQualifier? = null,
    val wheelchairAccessible: cl.emilym.gtfs.ServiceWheelchairAccessible,
    val wheelchairAccessibleAppliesToAllTrips: cl.emilym.gtfs.MultipleQualifier? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.ServiceAccessibility = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.ServiceAccessibility> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.ServiceAccessibility> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.ServiceAccessibility = cl.emilym.gtfs.ServiceAccessibility.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.ServiceAccessibility> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.ServiceAccessibility",
            messageClass = cl.emilym.gtfs.ServiceAccessibility::class,
            messageCompanion = this,
            fields = buildList(4) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "bikesAllowed",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = cl.emilym.gtfs.ServiceBikesAllowed.Companion, hasPresence = true),
                        jsonName = "bikesAllowed",
                        value = cl.emilym.gtfs.ServiceAccessibility::bikesAllowed
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "bikesAllowedAppliesToAllTrips",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = cl.emilym.gtfs.MultipleQualifier.Companion, hasPresence = true),
                        jsonName = "bikesAllowedAppliesToAllTrips",
                        value = cl.emilym.gtfs.ServiceAccessibility::bikesAllowedAppliesToAllTrips
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "wheelchairAccessible",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = cl.emilym.gtfs.ServiceWheelchairAccessible.Companion, hasPresence = true),
                        jsonName = "wheelchairAccessible",
                        value = cl.emilym.gtfs.ServiceAccessibility::wheelchairAccessible
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "wheelchairAccessibleAppliesToAllTrips",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = cl.emilym.gtfs.MultipleQualifier.Companion, hasPresence = true),
                        jsonName = "wheelchairAccessibleAppliesToAllTrips",
                        value = cl.emilym.gtfs.ServiceAccessibility::wheelchairAccessibleAppliesToAllTrips
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class TimetableServiceRegular(
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean,
    val startDate: String,
    val endDate: String,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.TimetableServiceRegular = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.TimetableServiceRegular> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.TimetableServiceRegular> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.TimetableServiceRegular = cl.emilym.gtfs.TimetableServiceRegular.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.TimetableServiceRegular> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.TimetableServiceRegular",
            messageClass = cl.emilym.gtfs.TimetableServiceRegular::class,
            messageCompanion = this,
            fields = buildList(9) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "monday",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bool(hasPresence = true),
                        jsonName = "monday",
                        value = cl.emilym.gtfs.TimetableServiceRegular::monday
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "tuesday",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bool(hasPresence = true),
                        jsonName = "tuesday",
                        value = cl.emilym.gtfs.TimetableServiceRegular::tuesday
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "wednesday",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bool(hasPresence = true),
                        jsonName = "wednesday",
                        value = cl.emilym.gtfs.TimetableServiceRegular::wednesday
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "thursday",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bool(hasPresence = true),
                        jsonName = "thursday",
                        value = cl.emilym.gtfs.TimetableServiceRegular::thursday
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "friday",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bool(hasPresence = true),
                        jsonName = "friday",
                        value = cl.emilym.gtfs.TimetableServiceRegular::friday
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "saturday",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bool(hasPresence = true),
                        jsonName = "saturday",
                        value = cl.emilym.gtfs.TimetableServiceRegular::saturday
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "sunday",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bool(hasPresence = true),
                        jsonName = "sunday",
                        value = cl.emilym.gtfs.TimetableServiceRegular::sunday
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "startDate",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "startDate",
                        value = cl.emilym.gtfs.TimetableServiceRegular::startDate
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "endDate",
                        number = 9,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "endDate",
                        value = cl.emilym.gtfs.TimetableServiceRegular::endDate
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class TimetableServiceException(
    val date: String,
    val type: cl.emilym.gtfs.TimetableServiceExceptionType,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.TimetableServiceException = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.TimetableServiceException> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.TimetableServiceException> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.TimetableServiceException = cl.emilym.gtfs.TimetableServiceException.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.TimetableServiceException> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.TimetableServiceException",
            messageClass = cl.emilym.gtfs.TimetableServiceException::class,
            messageCompanion = this,
            fields = buildList(2) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "date",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "date",
                        value = cl.emilym.gtfs.TimetableServiceException::date
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "type",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = cl.emilym.gtfs.TimetableServiceExceptionType.Companion, hasPresence = true),
                        jsonName = "type",
                        value = cl.emilym.gtfs.TimetableServiceException::type
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class RouteTripInformation(
    val startTime: String,
    val endTime: String,
    val accessibility: cl.emilym.gtfs.ServiceAccessibility,
    val stops: List<cl.emilym.gtfs.RouteTripStop> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.RouteTripInformation = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.RouteTripInformation> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.RouteTripInformation> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.RouteTripInformation = cl.emilym.gtfs.RouteTripInformation.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.RouteTripInformation> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.RouteTripInformation",
            messageClass = cl.emilym.gtfs.RouteTripInformation::class,
            messageCompanion = this,
            fields = buildList(4) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "startTime",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "startTime",
                        value = cl.emilym.gtfs.RouteTripInformation::startTime
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "endTime",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "endTime",
                        value = cl.emilym.gtfs.RouteTripInformation::endTime
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "accessibility",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.ServiceAccessibility.Companion),
                        jsonName = "accessibility",
                        value = cl.emilym.gtfs.RouteTripInformation::accessibility
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stops",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.RouteTripStop>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.RouteTripStop.Companion)),
                        jsonName = "stops",
                        value = cl.emilym.gtfs.RouteTripInformation::stops
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class RouteTripStop(
    val stopId: String,
    val arrivalTime: String,
    val departureTime: String,
    val sequence: Int,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.RouteTripStop = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.RouteTripStop> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.RouteTripStop> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.RouteTripStop = cl.emilym.gtfs.RouteTripStop.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.RouteTripStop> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.RouteTripStop",
            messageClass = cl.emilym.gtfs.RouteTripStop::class,
            messageCompanion = this,
            fields = buildList(4) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stopId",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "stopId",
                        value = cl.emilym.gtfs.RouteTripStop::stopId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "arrivalTime",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "arrivalTime",
                        value = cl.emilym.gtfs.RouteTripStop::arrivalTime
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "departureTime",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "departureTime",
                        value = cl.emilym.gtfs.RouteTripStop::departureTime
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "sequence",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "sequence",
                        value = cl.emilym.gtfs.RouteTripStop::sequence
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class Location(
    val lat: Double,
    val lng: Double,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.Location = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.Location> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.Location> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.Location = cl.emilym.gtfs.Location.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.Location> = pbandk.MessageDescriptor(
            fullName = "gtfs_api.Location",
            messageClass = cl.emilym.gtfs.Location::class,
            messageCompanion = this,
            fields = buildList(2) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "lat",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        jsonName = "lat",
                        value = cl.emilym.gtfs.Location::lat
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "lng",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        jsonName = "lng",
                        value = cl.emilym.gtfs.Location::lng
                    )
                )
            }
        )
    }
}

@pbandk.Export
@pbandk.JsName("orDefaultForStopEndpoint")
public fun StopEndpoint?.orDefault(): cl.emilym.gtfs.StopEndpoint = this ?: StopEndpoint.defaultInstance

private fun StopEndpoint.protoMergeImpl(plus: pbandk.Message?): StopEndpoint = (plus as? StopEndpoint)?.let {
    it.copy(
        stop = stop + plus.stop,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun StopEndpoint.Companion.decodeWithImpl(u: pbandk.MessageDecoder): StopEndpoint {
    var stop: pbandk.ListWithSize.Builder<cl.emilym.gtfs.Stop>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> stop = (stop ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.Stop> }
        }
    }

    return StopEndpoint(pbandk.ListWithSize.Builder.fixed(stop), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForRouteEndpoint")
public fun RouteEndpoint?.orDefault(): cl.emilym.gtfs.RouteEndpoint = this ?: RouteEndpoint.defaultInstance

private fun RouteEndpoint.protoMergeImpl(plus: pbandk.Message?): RouteEndpoint = (plus as? RouteEndpoint)?.let {
    it.copy(
        route = route + plus.route,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun RouteEndpoint.Companion.decodeWithImpl(u: pbandk.MessageDecoder): RouteEndpoint {
    var route: pbandk.ListWithSize.Builder<cl.emilym.gtfs.Route>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> route = (route ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.Route> }
        }
    }

    return RouteEndpoint(pbandk.ListWithSize.Builder.fixed(route), unknownFields)
}

private fun StopDetailEndpoint.protoMergeImpl(plus: pbandk.Message?): StopDetailEndpoint = (plus as? StopDetailEndpoint)?.let {
    it.copy(
        stop = stop.plus(plus.stop),
        children = children + plus.children,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun StopDetailEndpoint.Companion.decodeWithImpl(u: pbandk.MessageDecoder): StopDetailEndpoint {
    var stop: cl.emilym.gtfs.Stop? = null
    var children: pbandk.ListWithSize.Builder<cl.emilym.gtfs.Stop>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> stop = _fieldValue as cl.emilym.gtfs.Stop
            2 -> children = (children ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.Stop> }
        }
    }

    if (stop == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("stop")
    }
    return StopDetailEndpoint(stop!!, pbandk.ListWithSize.Builder.fixed(children), unknownFields)
}

private fun RouteDetailEndpoint.protoMergeImpl(plus: pbandk.Message?): RouteDetailEndpoint = (plus as? RouteDetailEndpoint)?.let {
    it.copy(
        route = route.plus(plus.route),
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun RouteDetailEndpoint.Companion.decodeWithImpl(u: pbandk.MessageDecoder): RouteDetailEndpoint {
    var route: cl.emilym.gtfs.Route? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> route = _fieldValue as cl.emilym.gtfs.Route
        }
    }

    if (route == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("route")
    }
    return RouteDetailEndpoint(route!!, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForServiceEndpoint")
public fun ServiceEndpoint?.orDefault(): cl.emilym.gtfs.ServiceEndpoint = this ?: ServiceEndpoint.defaultInstance

private fun ServiceEndpoint.protoMergeImpl(plus: pbandk.Message?): ServiceEndpoint = (plus as? ServiceEndpoint)?.let {
    it.copy(
        service = service + plus.service,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ServiceEndpoint.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ServiceEndpoint {
    var service: pbandk.ListWithSize.Builder<cl.emilym.gtfs.Service>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> service = (service ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.Service> }
        }
    }

    return ServiceEndpoint(pbandk.ListWithSize.Builder.fixed(service), unknownFields)
}

private fun RouteTimetableEndpoint.protoMergeImpl(plus: pbandk.Message?): RouteTimetableEndpoint = (plus as? RouteTimetableEndpoint)?.let {
    it.copy(
        trips = trips + plus.trips,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun RouteTimetableEndpoint.Companion.decodeWithImpl(u: pbandk.MessageDecoder): RouteTimetableEndpoint {
    var routeId: String? = null
    var serviceId: String? = null
    var trips: pbandk.ListWithSize.Builder<cl.emilym.gtfs.RouteTripInformation>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> routeId = _fieldValue as String
            2 -> serviceId = _fieldValue as String
            3 -> trips = (trips ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.RouteTripInformation> }
        }
    }

    if (routeId == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("routeId")
    }
    if (serviceId == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("serviceId")
    }
    return RouteTimetableEndpoint(routeId!!, serviceId!!, pbandk.ListWithSize.Builder.fixed(trips), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForRouteServicesEndpoint")
public fun RouteServicesEndpoint?.orDefault(): cl.emilym.gtfs.RouteServicesEndpoint = this ?: RouteServicesEndpoint.defaultInstance

private fun RouteServicesEndpoint.protoMergeImpl(plus: pbandk.Message?): RouteServicesEndpoint = (plus as? RouteServicesEndpoint)?.let {
    it.copy(
        serviceIds = serviceIds + plus.serviceIds,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun RouteServicesEndpoint.Companion.decodeWithImpl(u: pbandk.MessageDecoder): RouteServicesEndpoint {
    var serviceIds: pbandk.ListWithSize.Builder<String>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> serviceIds = (serviceIds ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<String> }
        }
    }

    return RouteServicesEndpoint(pbandk.ListWithSize.Builder.fixed(serviceIds), unknownFields)
}

private fun Stop.protoMergeImpl(plus: pbandk.Message?): Stop = (plus as? Stop)?.let {
    it.copy(
        parentStation = plus.parentStation ?: parentStation,
        location = location.plus(plus.location),
        accessibility = accessibility.plus(plus.accessibility),
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Stop.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Stop {
    var id: String? = null
    var parentStation: String? = null
    var name: String? = null
    var location: cl.emilym.gtfs.Location? = null
    var accessibility: cl.emilym.gtfs.StopAccessibility? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> id = _fieldValue as String
            2 -> name = _fieldValue as String
            3 -> location = _fieldValue as cl.emilym.gtfs.Location
            4 -> accessibility = _fieldValue as cl.emilym.gtfs.StopAccessibility
            5 -> parentStation = _fieldValue as String
        }
    }

    if (id == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("id")
    }
    if (name == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("name")
    }
    if (location == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("location")
    }
    if (accessibility == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("accessibility")
    }
    return Stop(id!!, parentStation, name!!, location!!,
        accessibility!!, unknownFields)
}

private fun StopAccessibility.protoMergeImpl(plus: pbandk.Message?): StopAccessibility = (plus as? StopAccessibility)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun StopAccessibility.Companion.decodeWithImpl(u: pbandk.MessageDecoder): StopAccessibility {
    var stopWheelchairAccessible: cl.emilym.gtfs.WheelchairStopAccessibility? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> stopWheelchairAccessible = _fieldValue as cl.emilym.gtfs.WheelchairStopAccessibility
        }
    }

    if (stopWheelchairAccessible == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("stopWheelchairAccessible")
    }
    return StopAccessibility(stopWheelchairAccessible!!, unknownFields)
}

private fun Route.protoMergeImpl(plus: pbandk.Message?): Route = (plus as? Route)?.let {
    it.copy(
        displayCode = plus.displayCode ?: displayCode,
        colors = colors?.plus(plus.colors) ?: plus.colors,
        designation = plus.designation ?: designation,
        realTimeUrl = plus.realTimeUrl ?: realTimeUrl,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Route.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Route {
    var id: String? = null
    var code: String? = null
    var displayCode: String? = null
    var colors: cl.emilym.gtfs.ColorPair? = null
    var name: String? = null
    var designation: String? = null
    var type: cl.emilym.gtfs.RouteType? = null
    var realTimeUrl: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> id = _fieldValue as String
            2 -> code = _fieldValue as String
            3 -> name = _fieldValue as String
            4 -> type = _fieldValue as cl.emilym.gtfs.RouteType
            5 -> displayCode = _fieldValue as String
            6 -> colors = _fieldValue as cl.emilym.gtfs.ColorPair
            7 -> designation = _fieldValue as String
            8 -> realTimeUrl = _fieldValue as String
        }
    }

    if (id == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("id")
    }
    if (code == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("code")
    }
    if (name == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("name")
    }
    if (type == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("type")
    }
    return Route(id!!, code!!, displayCode, colors,
        name!!, designation, type!!, realTimeUrl, unknownFields)
}

private fun ColorPair.protoMergeImpl(plus: pbandk.Message?): ColorPair = (plus as? ColorPair)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ColorPair.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ColorPair {
    var color: String? = null
    var onColor: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> color = _fieldValue as String
            2 -> onColor = _fieldValue as String
        }
    }

    if (color == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("color")
    }
    if (onColor == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("onColor")
    }
    return ColorPair(color!!, onColor!!, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForStopTimetable")
public fun StopTimetable?.orDefault(): cl.emilym.gtfs.StopTimetable = this ?: StopTimetable.defaultInstance

private fun StopTimetable.protoMergeImpl(plus: pbandk.Message?): StopTimetable = (plus as? StopTimetable)?.let {
    it.copy(
        times = times + plus.times,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun StopTimetable.Companion.decodeWithImpl(u: pbandk.MessageDecoder): StopTimetable {
    var times: pbandk.ListWithSize.Builder<cl.emilym.gtfs.StopTimetableTime>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> times = (times ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.StopTimetableTime> }
        }
    }

    return StopTimetable(pbandk.ListWithSize.Builder.fixed(times), unknownFields)
}

private fun StopTimetableTime.protoMergeImpl(plus: pbandk.Message?): StopTimetableTime = (plus as? StopTimetableTime)?.let {
    it.copy(
        childStopId = plus.childStopId ?: childStopId,
        accessibility = accessibility.plus(plus.accessibility),
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun StopTimetableTime.Companion.decodeWithImpl(u: pbandk.MessageDecoder): StopTimetableTime {
    var childStopId: String? = null
    var routeId: String? = null
    var routeCode: String? = null
    var serviceId: String? = null
    var arrivalTime: String? = null
    var departureTime: String? = null
    var heading: String? = null
    var sequence: Int? = null
    var accessibility: cl.emilym.gtfs.ServiceAccessibility? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> routeId = _fieldValue as String
            2 -> routeCode = _fieldValue as String
            3 -> serviceId = _fieldValue as String
            4 -> arrivalTime = _fieldValue as String
            5 -> departureTime = _fieldValue as String
            6 -> heading = _fieldValue as String
            7 -> sequence = _fieldValue as Int
            8 -> accessibility = _fieldValue as cl.emilym.gtfs.ServiceAccessibility
            9 -> childStopId = _fieldValue as String
        }
    }

    if (routeId == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("routeId")
    }
    if (routeCode == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("routeCode")
    }
    if (serviceId == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("serviceId")
    }
    if (arrivalTime == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("arrivalTime")
    }
    if (departureTime == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("departureTime")
    }
    if (heading == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("heading")
    }
    if (sequence == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("sequence")
    }
    if (accessibility == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("accessibility")
    }
    return StopTimetableTime(childStopId, routeId!!, routeCode!!, serviceId!!,
        arrivalTime!!, departureTime!!, heading!!, sequence!!,
        accessibility!!, unknownFields)
}

private fun Service.protoMergeImpl(plus: pbandk.Message?): Service = (plus as? Service)?.let {
    it.copy(
        regular = regular + plus.regular,
        exception = exception + plus.exception,
        accessibility = accessibility?.plus(plus.accessibility) ?: plus.accessibility,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Service.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Service {
    var id: String? = null
    var regular: pbandk.ListWithSize.Builder<cl.emilym.gtfs.TimetableServiceRegular>? = null
    var exception: pbandk.ListWithSize.Builder<cl.emilym.gtfs.TimetableServiceException>? = null
    var accessibility: cl.emilym.gtfs.ServiceAccessibility? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> id = _fieldValue as String
            2 -> regular = (regular ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.TimetableServiceRegular> }
            3 -> exception = (exception ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.TimetableServiceException> }
            4 -> accessibility = _fieldValue as cl.emilym.gtfs.ServiceAccessibility
        }
    }

    if (id == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("id")
    }
    return Service(id!!, pbandk.ListWithSize.Builder.fixed(regular), pbandk.ListWithSize.Builder.fixed(exception), accessibility, unknownFields)
}

private fun ServiceAccessibility.protoMergeImpl(plus: pbandk.Message?): ServiceAccessibility = (plus as? ServiceAccessibility)?.let {
    it.copy(
        bikesAllowedAppliesToAllTrips = plus.bikesAllowedAppliesToAllTrips ?: bikesAllowedAppliesToAllTrips,
        wheelchairAccessibleAppliesToAllTrips = plus.wheelchairAccessibleAppliesToAllTrips ?: wheelchairAccessibleAppliesToAllTrips,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ServiceAccessibility.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ServiceAccessibility {
    var bikesAllowed: cl.emilym.gtfs.ServiceBikesAllowed? = null
    var bikesAllowedAppliesToAllTrips: cl.emilym.gtfs.MultipleQualifier? = null
    var wheelchairAccessible: cl.emilym.gtfs.ServiceWheelchairAccessible? = null
    var wheelchairAccessibleAppliesToAllTrips: cl.emilym.gtfs.MultipleQualifier? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> bikesAllowed = _fieldValue as cl.emilym.gtfs.ServiceBikesAllowed
            2 -> bikesAllowedAppliesToAllTrips = _fieldValue as cl.emilym.gtfs.MultipleQualifier
            3 -> wheelchairAccessible = _fieldValue as cl.emilym.gtfs.ServiceWheelchairAccessible
            4 -> wheelchairAccessibleAppliesToAllTrips = _fieldValue as cl.emilym.gtfs.MultipleQualifier
        }
    }

    if (bikesAllowed == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("bikesAllowed")
    }
    if (wheelchairAccessible == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("wheelchairAccessible")
    }
    return ServiceAccessibility(bikesAllowed!!, bikesAllowedAppliesToAllTrips, wheelchairAccessible!!, wheelchairAccessibleAppliesToAllTrips, unknownFields)
}

private fun TimetableServiceRegular.protoMergeImpl(plus: pbandk.Message?): TimetableServiceRegular = (plus as? TimetableServiceRegular)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TimetableServiceRegular.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TimetableServiceRegular {
    var monday: Boolean? = null
    var tuesday: Boolean? = null
    var wednesday: Boolean? = null
    var thursday: Boolean? = null
    var friday: Boolean? = null
    var saturday: Boolean? = null
    var sunday: Boolean? = null
    var startDate: String? = null
    var endDate: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> monday = _fieldValue as Boolean
            2 -> tuesday = _fieldValue as Boolean
            3 -> wednesday = _fieldValue as Boolean
            4 -> thursday = _fieldValue as Boolean
            5 -> friday = _fieldValue as Boolean
            6 -> saturday = _fieldValue as Boolean
            7 -> sunday = _fieldValue as Boolean
            8 -> startDate = _fieldValue as String
            9 -> endDate = _fieldValue as String
        }
    }

    if (monday == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("monday")
    }
    if (tuesday == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("tuesday")
    }
    if (wednesday == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("wednesday")
    }
    if (thursday == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("thursday")
    }
    if (friday == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("friday")
    }
    if (saturday == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("saturday")
    }
    if (sunday == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("sunday")
    }
    if (startDate == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("startDate")
    }
    if (endDate == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("endDate")
    }
    return TimetableServiceRegular(monday!!, tuesday!!, wednesday!!, thursday!!,
        friday!!, saturday!!, sunday!!, startDate!!,
        endDate!!, unknownFields)
}

private fun TimetableServiceException.protoMergeImpl(plus: pbandk.Message?): TimetableServiceException = (plus as? TimetableServiceException)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TimetableServiceException.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TimetableServiceException {
    var date: String? = null
    var type: cl.emilym.gtfs.TimetableServiceExceptionType? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> date = _fieldValue as String
            2 -> type = _fieldValue as cl.emilym.gtfs.TimetableServiceExceptionType
        }
    }

    if (date == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("date")
    }
    if (type == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("type")
    }
    return TimetableServiceException(date!!, type!!, unknownFields)
}

private fun RouteTripInformation.protoMergeImpl(plus: pbandk.Message?): RouteTripInformation = (plus as? RouteTripInformation)?.let {
    it.copy(
        accessibility = accessibility.plus(plus.accessibility),
        stops = stops + plus.stops,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun RouteTripInformation.Companion.decodeWithImpl(u: pbandk.MessageDecoder): RouteTripInformation {
    var startTime: String? = null
    var endTime: String? = null
    var accessibility: cl.emilym.gtfs.ServiceAccessibility? = null
    var stops: pbandk.ListWithSize.Builder<cl.emilym.gtfs.RouteTripStop>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> startTime = _fieldValue as String
            2 -> endTime = _fieldValue as String
            3 -> accessibility = _fieldValue as cl.emilym.gtfs.ServiceAccessibility
            4 -> stops = (stops ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.RouteTripStop> }
        }
    }

    if (startTime == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("startTime")
    }
    if (endTime == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("endTime")
    }
    if (accessibility == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("accessibility")
    }
    return RouteTripInformation(startTime!!, endTime!!, accessibility!!, pbandk.ListWithSize.Builder.fixed(stops), unknownFields)
}

private fun RouteTripStop.protoMergeImpl(plus: pbandk.Message?): RouteTripStop = (plus as? RouteTripStop)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun RouteTripStop.Companion.decodeWithImpl(u: pbandk.MessageDecoder): RouteTripStop {
    var stopId: String? = null
    var arrivalTime: String? = null
    var departureTime: String? = null
    var sequence: Int? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> stopId = _fieldValue as String
            2 -> arrivalTime = _fieldValue as String
            3 -> departureTime = _fieldValue as String
            4 -> sequence = _fieldValue as Int
        }
    }

    if (stopId == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("stopId")
    }
    if (arrivalTime == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("arrivalTime")
    }
    if (departureTime == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("departureTime")
    }
    if (sequence == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("sequence")
    }
    return RouteTripStop(stopId!!, arrivalTime!!, departureTime!!, sequence!!, unknownFields)
}

private fun Location.protoMergeImpl(plus: pbandk.Message?): Location = (plus as? Location)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Location.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Location {
    var lat: Double? = null
    var lng: Double? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> lat = _fieldValue as Double
            2 -> lng = _fieldValue as Double
        }
    }

    if (lat == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("lat")
    }
    if (lng == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("lng")
    }
    return Location(lat!!, lng!!, unknownFields)
}