@file:OptIn(pbandk.PublicForGeneratedCode::class)

package com.google.transit.realtime

@pbandk.Export
public data class FeedMessage(
    val header: com.google.transit.realtime.FeedHeader,
    val entity: List<com.google.transit.realtime.FeedEntity> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.FeedMessage = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.FeedMessage> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.FeedMessage> {
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.FeedMessage = com.google.transit.realtime.FeedMessage.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.FeedMessage> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.FeedMessage",
            messageClass = com.google.transit.realtime.FeedMessage::class,
            messageCompanion = this,
            fields = buildList(2) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "header",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.FeedHeader.Companion),
                        jsonName = "header",
                        value = com.google.transit.realtime.FeedMessage::header
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "entity",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Repeated<com.google.transit.realtime.FeedEntity>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.FeedEntity.Companion)),
                        jsonName = "entity",
                        value = com.google.transit.realtime.FeedMessage::entity
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class FeedHeader(
    val gtfsRealtimeVersion: String,
    val incrementality: com.google.transit.realtime.FeedHeader.Incrementality? = null,
    val timestamp: Long? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.FeedHeader = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.FeedHeader> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.FeedHeader> {
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.FeedHeader = com.google.transit.realtime.FeedHeader.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.FeedHeader> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.FeedHeader",
            messageClass = com.google.transit.realtime.FeedHeader::class,
            messageCompanion = this,
            fields = buildList(3) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "gtfs_realtime_version",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "gtfsRealtimeVersion",
                        value = com.google.transit.realtime.FeedHeader::gtfsRealtimeVersion
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "incrementality",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.FeedHeader.Incrementality.Companion, hasPresence = true),
                        jsonName = "incrementality",
                        value = com.google.transit.realtime.FeedHeader::incrementality
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "timestamp",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt64(hasPresence = true),
                        jsonName = "timestamp",
                        value = com.google.transit.realtime.FeedHeader::timestamp
                    )
                )
            }
        )
    }

    public sealed class Incrementality(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?): Boolean = other is com.google.transit.realtime.FeedHeader.Incrementality && other.value == value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "FeedHeader.Incrementality.${name ?: "UNRECOGNIZED"}(value=$value)"

        public object FULL_DATASET : Incrementality(0, "FULL_DATASET")
        public object DIFFERENTIAL : Incrementality(1, "DIFFERENTIAL")
        public class UNRECOGNIZED(value: Int) : Incrementality(value)

        public companion object : pbandk.Message.Enum.Companion<com.google.transit.realtime.FeedHeader.Incrementality> {
            public val values: List<com.google.transit.realtime.FeedHeader.Incrementality> by lazy { listOf(FULL_DATASET, DIFFERENTIAL) }
            override fun fromValue(value: Int): com.google.transit.realtime.FeedHeader.Incrementality = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String): com.google.transit.realtime.FeedHeader.Incrementality = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No Incrementality with name: $name")
        }
    }
}

@pbandk.Export
public data class FeedEntity(
    val id: String,
    val isDeleted: Boolean? = null,
    val tripUpdate: com.google.transit.realtime.TripUpdate? = null,
    val vehicle: com.google.transit.realtime.VehiclePosition? = null,
    val alert: com.google.transit.realtime.Alert? = null,
    val shape: com.google.transit.realtime.Shape? = null,
    val stop: com.google.transit.realtime.Stop? = null,
    val tripModifications: com.google.transit.realtime.TripModifications? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.FeedEntity = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.FeedEntity> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.FeedEntity> {
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.FeedEntity = com.google.transit.realtime.FeedEntity.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.FeedEntity> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.FeedEntity",
            messageClass = com.google.transit.realtime.FeedEntity::class,
            messageCompanion = this,
            fields = buildList(8) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "id",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "id",
                        value = com.google.transit.realtime.FeedEntity::id
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "is_deleted",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bool(hasPresence = true),
                        jsonName = "isDeleted",
                        value = com.google.transit.realtime.FeedEntity::isDeleted
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "trip_update",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripUpdate.Companion),
                        jsonName = "tripUpdate",
                        value = com.google.transit.realtime.FeedEntity::tripUpdate
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "vehicle",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.VehiclePosition.Companion),
                        jsonName = "vehicle",
                        value = com.google.transit.realtime.FeedEntity::vehicle
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "alert",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.Alert.Companion),
                        jsonName = "alert",
                        value = com.google.transit.realtime.FeedEntity::alert
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "shape",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.Shape.Companion),
                        jsonName = "shape",
                        value = com.google.transit.realtime.FeedEntity::shape
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.Stop.Companion),
                        jsonName = "stop",
                        value = com.google.transit.realtime.FeedEntity::stop
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "trip_modifications",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripModifications.Companion),
                        jsonName = "tripModifications",
                        value = com.google.transit.realtime.FeedEntity::tripModifications
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class TripUpdate(
    val trip: com.google.transit.realtime.TripDescriptor,
    val vehicle: com.google.transit.realtime.VehicleDescriptor? = null,
    val stopTimeUpdate: List<com.google.transit.realtime.TripUpdate.StopTimeUpdate> = emptyList(),
    val timestamp: Long? = null,
    val delay: Int? = null,
    val tripProperties: com.google.transit.realtime.TripUpdate.TripProperties? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TripUpdate = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripUpdate> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.TripUpdate> {
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TripUpdate = com.google.transit.realtime.TripUpdate.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripUpdate> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.TripUpdate",
            messageClass = com.google.transit.realtime.TripUpdate::class,
            messageCompanion = this,
            fields = buildList(6) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "trip",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripDescriptor.Companion),
                        jsonName = "trip",
                        value = com.google.transit.realtime.TripUpdate::trip
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_time_update",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Repeated<com.google.transit.realtime.TripUpdate.StopTimeUpdate>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripUpdate.StopTimeUpdate.Companion)),
                        jsonName = "stopTimeUpdate",
                        value = com.google.transit.realtime.TripUpdate::stopTimeUpdate
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "vehicle",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.VehicleDescriptor.Companion),
                        jsonName = "vehicle",
                        value = com.google.transit.realtime.TripUpdate::vehicle
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "timestamp",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt64(hasPresence = true),
                        jsonName = "timestamp",
                        value = com.google.transit.realtime.TripUpdate::timestamp
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "delay",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.Int32(hasPresence = true),
                        jsonName = "delay",
                        value = com.google.transit.realtime.TripUpdate::delay
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "trip_properties",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripUpdate.TripProperties.Companion),
                        jsonName = "tripProperties",
                        value = com.google.transit.realtime.TripUpdate::tripProperties
                    )
                )
            }
        )
    }

    public data class StopTimeEvent(
        val delay: Int? = null,
        val time: Long? = null,
        val uncertainty: Int? = null,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
        @property:pbandk.PbandkInternal
        override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
    ) : pbandk.ExtendableMessage {
        override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TripUpdate.StopTimeEvent = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripUpdate.StopTimeEvent> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<com.google.transit.realtime.TripUpdate.StopTimeEvent> {
            public val defaultInstance: com.google.transit.realtime.TripUpdate.StopTimeEvent by lazy { com.google.transit.realtime.TripUpdate.StopTimeEvent() }
            override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TripUpdate.StopTimeEvent = com.google.transit.realtime.TripUpdate.StopTimeEvent.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripUpdate.StopTimeEvent> = pbandk.MessageDescriptor(
                fullName = "transit_realtime.TripUpdate.StopTimeEvent",
                messageClass = com.google.transit.realtime.TripUpdate.StopTimeEvent::class,
                messageCompanion = this,
                fields = buildList(3) {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "delay",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.Int32(hasPresence = true),
                            jsonName = "delay",
                            value = com.google.transit.realtime.TripUpdate.StopTimeEvent::delay
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "time",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Primitive.Int64(hasPresence = true),
                            jsonName = "time",
                            value = com.google.transit.realtime.TripUpdate.StopTimeEvent::time
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "uncertainty",
                            number = 3,
                            type = pbandk.FieldDescriptor.Type.Primitive.Int32(hasPresence = true),
                            jsonName = "uncertainty",
                            value = com.google.transit.realtime.TripUpdate.StopTimeEvent::uncertainty
                        )
                    )
                }
            )
        }
    }

    public data class StopTimeUpdate(
        val stopSequence: Int? = null,
        val stopId: String? = null,
        val arrival: com.google.transit.realtime.TripUpdate.StopTimeEvent? = null,
        val departure: com.google.transit.realtime.TripUpdate.StopTimeEvent? = null,
        val departureOccupancyStatus: com.google.transit.realtime.VehiclePosition.OccupancyStatus? = null,
        val scheduleRelationship: com.google.transit.realtime.TripUpdate.StopTimeUpdate.ScheduleRelationship? = null,
        val stopTimeProperties: com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties? = null,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
        @property:pbandk.PbandkInternal
        override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
    ) : pbandk.ExtendableMessage {
        override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TripUpdate.StopTimeUpdate = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripUpdate.StopTimeUpdate> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<com.google.transit.realtime.TripUpdate.StopTimeUpdate> {
            public val defaultInstance: com.google.transit.realtime.TripUpdate.StopTimeUpdate by lazy { com.google.transit.realtime.TripUpdate.StopTimeUpdate() }
            override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TripUpdate.StopTimeUpdate = com.google.transit.realtime.TripUpdate.StopTimeUpdate.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripUpdate.StopTimeUpdate> = pbandk.MessageDescriptor(
                fullName = "transit_realtime.TripUpdate.StopTimeUpdate",
                messageClass = com.google.transit.realtime.TripUpdate.StopTimeUpdate::class,
                messageCompanion = this,
                fields = buildList(7) {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "stop_sequence",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                            jsonName = "stopSequence",
                            value = com.google.transit.realtime.TripUpdate.StopTimeUpdate::stopSequence
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "arrival",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripUpdate.StopTimeEvent.Companion),
                            jsonName = "arrival",
                            value = com.google.transit.realtime.TripUpdate.StopTimeUpdate::arrival
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "departure",
                            number = 3,
                            type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripUpdate.StopTimeEvent.Companion),
                            jsonName = "departure",
                            value = com.google.transit.realtime.TripUpdate.StopTimeUpdate::departure
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "stop_id",
                            number = 4,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "stopId",
                            value = com.google.transit.realtime.TripUpdate.StopTimeUpdate::stopId
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "schedule_relationship",
                            number = 5,
                            type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.TripUpdate.StopTimeUpdate.ScheduleRelationship.Companion, hasPresence = true),
                            jsonName = "scheduleRelationship",
                            value = com.google.transit.realtime.TripUpdate.StopTimeUpdate::scheduleRelationship
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "stop_time_properties",
                            number = 6,
                            type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties.Companion),
                            jsonName = "stopTimeProperties",
                            value = com.google.transit.realtime.TripUpdate.StopTimeUpdate::stopTimeProperties
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "departure_occupancy_status",
                            number = 7,
                            type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.VehiclePosition.OccupancyStatus.Companion, hasPresence = true),
                            jsonName = "departureOccupancyStatus",
                            value = com.google.transit.realtime.TripUpdate.StopTimeUpdate::departureOccupancyStatus
                        )
                    )
                }
            )
        }

        public sealed class ScheduleRelationship(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
            override fun equals(other: kotlin.Any?): Boolean = other is com.google.transit.realtime.TripUpdate.StopTimeUpdate.ScheduleRelationship && other.value == value
            override fun hashCode(): Int = value.hashCode()
            override fun toString(): String = "TripUpdate.StopTimeUpdate.ScheduleRelationship.${name ?: "UNRECOGNIZED"}(value=$value)"

            public object SCHEDULED : ScheduleRelationship(0, "SCHEDULED")
            public object SKIPPED : ScheduleRelationship(1, "SKIPPED")
            public object NO_DATA : ScheduleRelationship(2, "NO_DATA")
            public object UNSCHEDULED : ScheduleRelationship(3, "UNSCHEDULED")
            public class UNRECOGNIZED(value: Int) : ScheduleRelationship(value)

            public companion object : pbandk.Message.Enum.Companion<com.google.transit.realtime.TripUpdate.StopTimeUpdate.ScheduleRelationship> {
                public val values: List<com.google.transit.realtime.TripUpdate.StopTimeUpdate.ScheduleRelationship> by lazy { listOf(SCHEDULED, SKIPPED, NO_DATA, UNSCHEDULED) }
                override fun fromValue(value: Int): com.google.transit.realtime.TripUpdate.StopTimeUpdate.ScheduleRelationship = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
                override fun fromName(name: String): com.google.transit.realtime.TripUpdate.StopTimeUpdate.ScheduleRelationship = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No ScheduleRelationship with name: $name")
            }
        }

        public data class StopTimeProperties(
            val assignedStopId: String? = null,
            override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
            @property:pbandk.PbandkInternal
            override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
        ) : pbandk.ExtendableMessage {
            override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties = protoMergeImpl(other)
            override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties> get() = Companion.descriptor
            override val protoSize: Int by lazy { super.protoSize }
            public companion object : pbandk.Message.Companion<com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties> {
                public val defaultInstance: com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties by lazy { com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties() }
                override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties = com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties.decodeWithImpl(u)

                override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties> = pbandk.MessageDescriptor(
                    fullName = "transit_realtime.TripUpdate.StopTimeUpdate.StopTimeProperties",
                    messageClass = com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties::class,
                    messageCompanion = this,
                    fields = buildList(1) {
                        add(
                            pbandk.FieldDescriptor(
                                messageDescriptor = this@Companion::descriptor,
                                name = "assigned_stop_id",
                                number = 1,
                                type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                                jsonName = "assignedStopId",
                                value = com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties::assignedStopId
                            )
                        )
                    }
                )
            }
        }
    }

    public data class TripProperties(
        val tripId: String? = null,
        val startDate: String? = null,
        val startTime: String? = null,
        val shapeId: String? = null,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
        @property:pbandk.PbandkInternal
        override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
    ) : pbandk.ExtendableMessage {
        override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TripUpdate.TripProperties = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripUpdate.TripProperties> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<com.google.transit.realtime.TripUpdate.TripProperties> {
            public val defaultInstance: com.google.transit.realtime.TripUpdate.TripProperties by lazy { com.google.transit.realtime.TripUpdate.TripProperties() }
            override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TripUpdate.TripProperties = com.google.transit.realtime.TripUpdate.TripProperties.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripUpdate.TripProperties> = pbandk.MessageDescriptor(
                fullName = "transit_realtime.TripUpdate.TripProperties",
                messageClass = com.google.transit.realtime.TripUpdate.TripProperties::class,
                messageCompanion = this,
                fields = buildList(4) {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "trip_id",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "tripId",
                            value = com.google.transit.realtime.TripUpdate.TripProperties::tripId
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "start_date",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "startDate",
                            value = com.google.transit.realtime.TripUpdate.TripProperties::startDate
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "start_time",
                            number = 3,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "startTime",
                            value = com.google.transit.realtime.TripUpdate.TripProperties::startTime
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "shape_id",
                            number = 4,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "shapeId",
                            value = com.google.transit.realtime.TripUpdate.TripProperties::shapeId
                        )
                    )
                }
            )
        }
    }
}

@pbandk.Export
public data class VehiclePosition(
    val trip: com.google.transit.realtime.TripDescriptor? = null,
    val vehicle: com.google.transit.realtime.VehicleDescriptor? = null,
    val position: com.google.transit.realtime.Position? = null,
    val currentStopSequence: Int? = null,
    val stopId: String? = null,
    val currentStatus: com.google.transit.realtime.VehiclePosition.VehicleStopStatus? = null,
    val timestamp: Long? = null,
    val congestionLevel: com.google.transit.realtime.VehiclePosition.CongestionLevel? = null,
    val occupancyStatus: com.google.transit.realtime.VehiclePosition.OccupancyStatus? = null,
    val occupancyPercentage: Int? = null,
    val multiCarriageDetails: List<com.google.transit.realtime.VehiclePosition.CarriageDetails> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.VehiclePosition = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.VehiclePosition> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.VehiclePosition> {
        public val defaultInstance: com.google.transit.realtime.VehiclePosition by lazy { com.google.transit.realtime.VehiclePosition() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.VehiclePosition = com.google.transit.realtime.VehiclePosition.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.VehiclePosition> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.VehiclePosition",
            messageClass = com.google.transit.realtime.VehiclePosition::class,
            messageCompanion = this,
            fields = buildList(11) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "trip",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripDescriptor.Companion),
                        jsonName = "trip",
                        value = com.google.transit.realtime.VehiclePosition::trip
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "position",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.Position.Companion),
                        jsonName = "position",
                        value = com.google.transit.realtime.VehiclePosition::position
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "current_stop_sequence",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "currentStopSequence",
                        value = com.google.transit.realtime.VehiclePosition::currentStopSequence
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "current_status",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.VehiclePosition.VehicleStopStatus.Companion, hasPresence = true),
                        jsonName = "currentStatus",
                        value = com.google.transit.realtime.VehiclePosition::currentStatus
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "timestamp",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt64(hasPresence = true),
                        jsonName = "timestamp",
                        value = com.google.transit.realtime.VehiclePosition::timestamp
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "congestion_level",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.VehiclePosition.CongestionLevel.Companion, hasPresence = true),
                        jsonName = "congestionLevel",
                        value = com.google.transit.realtime.VehiclePosition::congestionLevel
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_id",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "stopId",
                        value = com.google.transit.realtime.VehiclePosition::stopId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "vehicle",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.VehicleDescriptor.Companion),
                        jsonName = "vehicle",
                        value = com.google.transit.realtime.VehiclePosition::vehicle
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "occupancy_status",
                        number = 9,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.VehiclePosition.OccupancyStatus.Companion, hasPresence = true),
                        jsonName = "occupancyStatus",
                        value = com.google.transit.realtime.VehiclePosition::occupancyStatus
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "occupancy_percentage",
                        number = 10,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "occupancyPercentage",
                        value = com.google.transit.realtime.VehiclePosition::occupancyPercentage
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "multi_carriage_details",
                        number = 11,
                        type = pbandk.FieldDescriptor.Type.Repeated<com.google.transit.realtime.VehiclePosition.CarriageDetails>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.VehiclePosition.CarriageDetails.Companion)),
                        jsonName = "multiCarriageDetails",
                        value = com.google.transit.realtime.VehiclePosition::multiCarriageDetails
                    )
                )
            }
        )
    }

    public sealed class VehicleStopStatus(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?): Boolean = other is com.google.transit.realtime.VehiclePosition.VehicleStopStatus && other.value == value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "VehiclePosition.VehicleStopStatus.${name ?: "UNRECOGNIZED"}(value=$value)"

        public object INCOMING_AT : VehicleStopStatus(0, "INCOMING_AT")
        public object STOPPED_AT : VehicleStopStatus(1, "STOPPED_AT")
        public object IN_TRANSIT_TO : VehicleStopStatus(2, "IN_TRANSIT_TO")
        public class UNRECOGNIZED(value: Int) : VehicleStopStatus(value)

        public companion object : pbandk.Message.Enum.Companion<com.google.transit.realtime.VehiclePosition.VehicleStopStatus> {
            public val values: List<com.google.transit.realtime.VehiclePosition.VehicleStopStatus> by lazy { listOf(INCOMING_AT, STOPPED_AT, IN_TRANSIT_TO) }
            override fun fromValue(value: Int): com.google.transit.realtime.VehiclePosition.VehicleStopStatus = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String): com.google.transit.realtime.VehiclePosition.VehicleStopStatus = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No VehicleStopStatus with name: $name")
        }
    }

    public sealed class CongestionLevel(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?): Boolean = other is com.google.transit.realtime.VehiclePosition.CongestionLevel && other.value == value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "VehiclePosition.CongestionLevel.${name ?: "UNRECOGNIZED"}(value=$value)"

        public object UNKNOWN_CONGESTION_LEVEL : CongestionLevel(0, "UNKNOWN_CONGESTION_LEVEL")
        public object RUNNING_SMOOTHLY : CongestionLevel(1, "RUNNING_SMOOTHLY")
        public object STOP_AND_GO : CongestionLevel(2, "STOP_AND_GO")
        public object CONGESTION : CongestionLevel(3, "CONGESTION")
        public object SEVERE_CONGESTION : CongestionLevel(4, "SEVERE_CONGESTION")
        public class UNRECOGNIZED(value: Int) : CongestionLevel(value)

        public companion object : pbandk.Message.Enum.Companion<com.google.transit.realtime.VehiclePosition.CongestionLevel> {
            public val values: List<com.google.transit.realtime.VehiclePosition.CongestionLevel> by lazy { listOf(UNKNOWN_CONGESTION_LEVEL, RUNNING_SMOOTHLY, STOP_AND_GO, CONGESTION, SEVERE_CONGESTION) }
            override fun fromValue(value: Int): com.google.transit.realtime.VehiclePosition.CongestionLevel = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String): com.google.transit.realtime.VehiclePosition.CongestionLevel = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No CongestionLevel with name: $name")
        }
    }

    public sealed class OccupancyStatus(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?): Boolean = other is com.google.transit.realtime.VehiclePosition.OccupancyStatus && other.value == value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "VehiclePosition.OccupancyStatus.${name ?: "UNRECOGNIZED"}(value=$value)"

        public object EMPTY : OccupancyStatus(0, "EMPTY")
        public object MANY_SEATS_AVAILABLE : OccupancyStatus(1, "MANY_SEATS_AVAILABLE")
        public object FEW_SEATS_AVAILABLE : OccupancyStatus(2, "FEW_SEATS_AVAILABLE")
        public object STANDING_ROOM_ONLY : OccupancyStatus(3, "STANDING_ROOM_ONLY")
        public object CRUSHED_STANDING_ROOM_ONLY : OccupancyStatus(4, "CRUSHED_STANDING_ROOM_ONLY")
        public object FULL : OccupancyStatus(5, "FULL")
        public object NOT_ACCEPTING_PASSENGERS : OccupancyStatus(6, "NOT_ACCEPTING_PASSENGERS")
        public object NO_DATA_AVAILABLE : OccupancyStatus(7, "NO_DATA_AVAILABLE")
        public object NOT_BOARDABLE : OccupancyStatus(8, "NOT_BOARDABLE")
        public class UNRECOGNIZED(value: Int) : OccupancyStatus(value)

        public companion object : pbandk.Message.Enum.Companion<com.google.transit.realtime.VehiclePosition.OccupancyStatus> {
            public val values: List<com.google.transit.realtime.VehiclePosition.OccupancyStatus> by lazy { listOf(EMPTY, MANY_SEATS_AVAILABLE, FEW_SEATS_AVAILABLE, STANDING_ROOM_ONLY, CRUSHED_STANDING_ROOM_ONLY, FULL, NOT_ACCEPTING_PASSENGERS, NO_DATA_AVAILABLE, NOT_BOARDABLE) }
            override fun fromValue(value: Int): com.google.transit.realtime.VehiclePosition.OccupancyStatus = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String): com.google.transit.realtime.VehiclePosition.OccupancyStatus = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No OccupancyStatus with name: $name")
        }
    }

    public data class CarriageDetails(
        val id: String? = null,
        val label: String? = null,
        val occupancyStatus: com.google.transit.realtime.VehiclePosition.OccupancyStatus? = null,
        val occupancyPercentage: Int? = null,
        val carriageSequence: Int? = null,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
        @property:pbandk.PbandkInternal
        override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
    ) : pbandk.ExtendableMessage {
        override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.VehiclePosition.CarriageDetails = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.VehiclePosition.CarriageDetails> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<com.google.transit.realtime.VehiclePosition.CarriageDetails> {
            public val defaultInstance: com.google.transit.realtime.VehiclePosition.CarriageDetails by lazy { com.google.transit.realtime.VehiclePosition.CarriageDetails() }
            override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.VehiclePosition.CarriageDetails = com.google.transit.realtime.VehiclePosition.CarriageDetails.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.VehiclePosition.CarriageDetails> = pbandk.MessageDescriptor(
                fullName = "transit_realtime.VehiclePosition.CarriageDetails",
                messageClass = com.google.transit.realtime.VehiclePosition.CarriageDetails::class,
                messageCompanion = this,
                fields = buildList(5) {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "id",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "id",
                            value = com.google.transit.realtime.VehiclePosition.CarriageDetails::id
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "label",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "label",
                            value = com.google.transit.realtime.VehiclePosition.CarriageDetails::label
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "occupancy_status",
                            number = 3,
                            type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.VehiclePosition.OccupancyStatus.Companion, hasPresence = true),
                            jsonName = "occupancyStatus",
                            value = com.google.transit.realtime.VehiclePosition.CarriageDetails::occupancyStatus
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "occupancy_percentage",
                            number = 4,
                            type = pbandk.FieldDescriptor.Type.Primitive.Int32(hasPresence = true),
                            jsonName = "occupancyPercentage",
                            value = com.google.transit.realtime.VehiclePosition.CarriageDetails::occupancyPercentage
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "carriage_sequence",
                            number = 5,
                            type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                            jsonName = "carriageSequence",
                            value = com.google.transit.realtime.VehiclePosition.CarriageDetails::carriageSequence
                        )
                    )
                }
            )
        }
    }
}

@pbandk.Export
public data class Alert(
    val activePeriod: List<com.google.transit.realtime.TimeRange> = emptyList(),
    val informedEntity: List<com.google.transit.realtime.EntitySelector> = emptyList(),
    val cause: com.google.transit.realtime.Alert.Cause? = null,
    val effect: com.google.transit.realtime.Alert.Effect? = null,
    val url: com.google.transit.realtime.TranslatedString? = null,
    val headerText: com.google.transit.realtime.TranslatedString? = null,
    val descriptionText: com.google.transit.realtime.TranslatedString? = null,
    val ttsHeaderText: com.google.transit.realtime.TranslatedString? = null,
    val ttsDescriptionText: com.google.transit.realtime.TranslatedString? = null,
    val severityLevel: com.google.transit.realtime.Alert.SeverityLevel? = null,
    val image: com.google.transit.realtime.TranslatedImage? = null,
    val imageAlternativeText: com.google.transit.realtime.TranslatedString? = null,
    val causeDetail: com.google.transit.realtime.TranslatedString? = null,
    val effectDetail: com.google.transit.realtime.TranslatedString? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.Alert = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.Alert> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.Alert> {
        public val defaultInstance: com.google.transit.realtime.Alert by lazy { com.google.transit.realtime.Alert() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.Alert = com.google.transit.realtime.Alert.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.Alert> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.Alert",
            messageClass = com.google.transit.realtime.Alert::class,
            messageCompanion = this,
            fields = buildList(14) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "active_period",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<com.google.transit.realtime.TimeRange>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TimeRange.Companion)),
                        jsonName = "activePeriod",
                        value = com.google.transit.realtime.Alert::activePeriod
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "informed_entity",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Repeated<com.google.transit.realtime.EntitySelector>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.EntitySelector.Companion)),
                        jsonName = "informedEntity",
                        value = com.google.transit.realtime.Alert::informedEntity
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "cause",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.Alert.Cause.Companion, hasPresence = true),
                        jsonName = "cause",
                        value = com.google.transit.realtime.Alert::cause
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "effect",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.Alert.Effect.Companion, hasPresence = true),
                        jsonName = "effect",
                        value = com.google.transit.realtime.Alert::effect
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "url",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "url",
                        value = com.google.transit.realtime.Alert::url
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "header_text",
                        number = 10,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "headerText",
                        value = com.google.transit.realtime.Alert::headerText
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "description_text",
                        number = 11,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "descriptionText",
                        value = com.google.transit.realtime.Alert::descriptionText
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "tts_header_text",
                        number = 12,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "ttsHeaderText",
                        value = com.google.transit.realtime.Alert::ttsHeaderText
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "tts_description_text",
                        number = 13,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "ttsDescriptionText",
                        value = com.google.transit.realtime.Alert::ttsDescriptionText
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "severity_level",
                        number = 14,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.Alert.SeverityLevel.Companion, hasPresence = true),
                        jsonName = "severityLevel",
                        value = com.google.transit.realtime.Alert::severityLevel
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "image",
                        number = 15,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedImage.Companion),
                        jsonName = "image",
                        value = com.google.transit.realtime.Alert::image
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "image_alternative_text",
                        number = 16,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "imageAlternativeText",
                        value = com.google.transit.realtime.Alert::imageAlternativeText
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "cause_detail",
                        number = 17,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "causeDetail",
                        value = com.google.transit.realtime.Alert::causeDetail
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "effect_detail",
                        number = 18,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "effectDetail",
                        value = com.google.transit.realtime.Alert::effectDetail
                    )
                )
            }
        )
    }

    public sealed class Cause(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?): Boolean = other is com.google.transit.realtime.Alert.Cause && other.value == value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "Alert.Cause.${name ?: "UNRECOGNIZED"}(value=$value)"

        public object UNKNOWN_CAUSE : Cause(1, "UNKNOWN_CAUSE")
        public object OTHER_CAUSE : Cause(2, "OTHER_CAUSE")
        public object TECHNICAL_PROBLEM : Cause(3, "TECHNICAL_PROBLEM")
        public object STRIKE : Cause(4, "STRIKE")
        public object DEMONSTRATION : Cause(5, "DEMONSTRATION")
        public object ACCIDENT : Cause(6, "ACCIDENT")
        public object HOLIDAY : Cause(7, "HOLIDAY")
        public object WEATHER : Cause(8, "WEATHER")
        public object MAINTENANCE : Cause(9, "MAINTENANCE")
        public object CONSTRUCTION : Cause(10, "CONSTRUCTION")
        public object POLICE_ACTIVITY : Cause(11, "POLICE_ACTIVITY")
        public object MEDICAL_EMERGENCY : Cause(12, "MEDICAL_EMERGENCY")
        public class UNRECOGNIZED(value: Int) : Cause(value)

        public companion object : pbandk.Message.Enum.Companion<com.google.transit.realtime.Alert.Cause> {
            public val values: List<com.google.transit.realtime.Alert.Cause> by lazy { listOf(UNKNOWN_CAUSE, OTHER_CAUSE, TECHNICAL_PROBLEM, STRIKE, DEMONSTRATION, ACCIDENT, HOLIDAY, WEATHER, MAINTENANCE, CONSTRUCTION, POLICE_ACTIVITY, MEDICAL_EMERGENCY) }
            override fun fromValue(value: Int): com.google.transit.realtime.Alert.Cause = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String): com.google.transit.realtime.Alert.Cause = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No Cause with name: $name")
        }
    }

    public sealed class Effect(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?): Boolean = other is com.google.transit.realtime.Alert.Effect && other.value == value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "Alert.Effect.${name ?: "UNRECOGNIZED"}(value=$value)"

        public object NO_SERVICE : Effect(1, "NO_SERVICE")
        public object REDUCED_SERVICE : Effect(2, "REDUCED_SERVICE")
        public object SIGNIFICANT_DELAYS : Effect(3, "SIGNIFICANT_DELAYS")
        public object DETOUR : Effect(4, "DETOUR")
        public object ADDITIONAL_SERVICE : Effect(5, "ADDITIONAL_SERVICE")
        public object MODIFIED_SERVICE : Effect(6, "MODIFIED_SERVICE")
        public object OTHER_EFFECT : Effect(7, "OTHER_EFFECT")
        public object UNKNOWN_EFFECT : Effect(8, "UNKNOWN_EFFECT")
        public object STOP_MOVED : Effect(9, "STOP_MOVED")
        public object NO_EFFECT : Effect(10, "NO_EFFECT")
        public object ACCESSIBILITY_ISSUE : Effect(11, "ACCESSIBILITY_ISSUE")
        public class UNRECOGNIZED(value: Int) : Effect(value)

        public companion object : pbandk.Message.Enum.Companion<com.google.transit.realtime.Alert.Effect> {
            public val values: List<com.google.transit.realtime.Alert.Effect> by lazy { listOf(NO_SERVICE, REDUCED_SERVICE, SIGNIFICANT_DELAYS, DETOUR, ADDITIONAL_SERVICE, MODIFIED_SERVICE, OTHER_EFFECT, UNKNOWN_EFFECT, STOP_MOVED, NO_EFFECT, ACCESSIBILITY_ISSUE) }
            override fun fromValue(value: Int): com.google.transit.realtime.Alert.Effect = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String): com.google.transit.realtime.Alert.Effect = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No Effect with name: $name")
        }
    }

    public sealed class SeverityLevel(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?): Boolean = other is com.google.transit.realtime.Alert.SeverityLevel && other.value == value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "Alert.SeverityLevel.${name ?: "UNRECOGNIZED"}(value=$value)"

        public object UNKNOWN_SEVERITY : SeverityLevel(1, "UNKNOWN_SEVERITY")
        public object INFO : SeverityLevel(2, "INFO")
        public object WARNING : SeverityLevel(3, "WARNING")
        public object SEVERE : SeverityLevel(4, "SEVERE")
        public class UNRECOGNIZED(value: Int) : SeverityLevel(value)

        public companion object : pbandk.Message.Enum.Companion<com.google.transit.realtime.Alert.SeverityLevel> {
            public val values: List<com.google.transit.realtime.Alert.SeverityLevel> by lazy { listOf(UNKNOWN_SEVERITY, INFO, WARNING, SEVERE) }
            override fun fromValue(value: Int): com.google.transit.realtime.Alert.SeverityLevel = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String): com.google.transit.realtime.Alert.SeverityLevel = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No SeverityLevel with name: $name")
        }
    }
}

@pbandk.Export
public data class TimeRange(
    val start: Long? = null,
    val end: Long? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TimeRange = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TimeRange> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.TimeRange> {
        public val defaultInstance: com.google.transit.realtime.TimeRange by lazy { com.google.transit.realtime.TimeRange() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TimeRange = com.google.transit.realtime.TimeRange.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TimeRange> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.TimeRange",
            messageClass = com.google.transit.realtime.TimeRange::class,
            messageCompanion = this,
            fields = buildList(2) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "start",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt64(hasPresence = true),
                        jsonName = "start",
                        value = com.google.transit.realtime.TimeRange::start
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "end",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt64(hasPresence = true),
                        jsonName = "end",
                        value = com.google.transit.realtime.TimeRange::end
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class Position(
    val latitude: Float,
    val longitude: Float,
    val bearing: Float? = null,
    val odometer: Double? = null,
    val speed: Float? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.Position = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.Position> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.Position> {
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.Position = com.google.transit.realtime.Position.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.Position> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.Position",
            messageClass = com.google.transit.realtime.Position::class,
            messageCompanion = this,
            fields = buildList(5) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "latitude",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.Float(hasPresence = true),
                        jsonName = "latitude",
                        value = com.google.transit.realtime.Position::latitude
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "longitude",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Float(hasPresence = true),
                        jsonName = "longitude",
                        value = com.google.transit.realtime.Position::longitude
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "bearing",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.Float(hasPresence = true),
                        jsonName = "bearing",
                        value = com.google.transit.realtime.Position::bearing
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "odometer",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        jsonName = "odometer",
                        value = com.google.transit.realtime.Position::odometer
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "speed",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.Float(hasPresence = true),
                        jsonName = "speed",
                        value = com.google.transit.realtime.Position::speed
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class TripDescriptor(
    val tripId: String? = null,
    val routeId: String? = null,
    val directionId: Int? = null,
    val startTime: String? = null,
    val startDate: String? = null,
    val scheduleRelationship: com.google.transit.realtime.TripDescriptor.ScheduleRelationship? = null,
    val modifiedTrip: com.google.transit.realtime.TripDescriptor.ModifiedTripSelector? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TripDescriptor = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripDescriptor> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.TripDescriptor> {
        public val defaultInstance: com.google.transit.realtime.TripDescriptor by lazy { com.google.transit.realtime.TripDescriptor() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TripDescriptor = com.google.transit.realtime.TripDescriptor.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripDescriptor> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.TripDescriptor",
            messageClass = com.google.transit.realtime.TripDescriptor::class,
            messageCompanion = this,
            fields = buildList(7) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "trip_id",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "tripId",
                        value = com.google.transit.realtime.TripDescriptor::tripId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "start_time",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "startTime",
                        value = com.google.transit.realtime.TripDescriptor::startTime
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "start_date",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "startDate",
                        value = com.google.transit.realtime.TripDescriptor::startDate
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "schedule_relationship",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.TripDescriptor.ScheduleRelationship.Companion, hasPresence = true),
                        jsonName = "scheduleRelationship",
                        value = com.google.transit.realtime.TripDescriptor::scheduleRelationship
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "route_id",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "routeId",
                        value = com.google.transit.realtime.TripDescriptor::routeId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "direction_id",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "directionId",
                        value = com.google.transit.realtime.TripDescriptor::directionId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "modified_trip",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripDescriptor.ModifiedTripSelector.Companion),
                        jsonName = "modifiedTrip",
                        value = com.google.transit.realtime.TripDescriptor::modifiedTrip
                    )
                )
            }
        )
    }

    public sealed class ScheduleRelationship(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?): Boolean = other is com.google.transit.realtime.TripDescriptor.ScheduleRelationship && other.value == value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "TripDescriptor.ScheduleRelationship.${name ?: "UNRECOGNIZED"}(value=$value)"

        public object SCHEDULED : ScheduleRelationship(0, "SCHEDULED")
        public object ADDED : ScheduleRelationship(1, "ADDED")
        public object UNSCHEDULED : ScheduleRelationship(2, "UNSCHEDULED")
        public object CANCELED : ScheduleRelationship(3, "CANCELED")
        public object REPLACEMENT : ScheduleRelationship(5, "REPLACEMENT")
        public object DUPLICATED : ScheduleRelationship(6, "DUPLICATED")
        public object DELETED : ScheduleRelationship(7, "DELETED")
        public class UNRECOGNIZED(value: Int) : ScheduleRelationship(value)

        public companion object : pbandk.Message.Enum.Companion<com.google.transit.realtime.TripDescriptor.ScheduleRelationship> {
            public val values: List<com.google.transit.realtime.TripDescriptor.ScheduleRelationship> by lazy { listOf(SCHEDULED, ADDED, UNSCHEDULED, CANCELED, REPLACEMENT, DUPLICATED, DELETED) }
            override fun fromValue(value: Int): com.google.transit.realtime.TripDescriptor.ScheduleRelationship = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String): com.google.transit.realtime.TripDescriptor.ScheduleRelationship = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No ScheduleRelationship with name: $name")
        }
    }

    public data class ModifiedTripSelector(
        val modificationsId: String? = null,
        val affectedTripId: String? = null,
        val startTime: String? = null,
        val startDate: String? = null,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
        @property:pbandk.PbandkInternal
        override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
    ) : pbandk.ExtendableMessage {
        override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TripDescriptor.ModifiedTripSelector = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripDescriptor.ModifiedTripSelector> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<com.google.transit.realtime.TripDescriptor.ModifiedTripSelector> {
            public val defaultInstance: com.google.transit.realtime.TripDescriptor.ModifiedTripSelector by lazy { com.google.transit.realtime.TripDescriptor.ModifiedTripSelector() }
            override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TripDescriptor.ModifiedTripSelector = com.google.transit.realtime.TripDescriptor.ModifiedTripSelector.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripDescriptor.ModifiedTripSelector> = pbandk.MessageDescriptor(
                fullName = "transit_realtime.TripDescriptor.ModifiedTripSelector",
                messageClass = com.google.transit.realtime.TripDescriptor.ModifiedTripSelector::class,
                messageCompanion = this,
                fields = buildList(4) {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "modifications_id",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "modificationsId",
                            value = com.google.transit.realtime.TripDescriptor.ModifiedTripSelector::modificationsId
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "affected_trip_id",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "affectedTripId",
                            value = com.google.transit.realtime.TripDescriptor.ModifiedTripSelector::affectedTripId
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "start_time",
                            number = 3,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "startTime",
                            value = com.google.transit.realtime.TripDescriptor.ModifiedTripSelector::startTime
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "start_date",
                            number = 4,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "startDate",
                            value = com.google.transit.realtime.TripDescriptor.ModifiedTripSelector::startDate
                        )
                    )
                }
            )
        }
    }
}

@pbandk.Export
public data class VehicleDescriptor(
    val id: String? = null,
    val label: String? = null,
    val licensePlate: String? = null,
    val wheelchairAccessible: com.google.transit.realtime.VehicleDescriptor.WheelchairAccessible? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.VehicleDescriptor = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.VehicleDescriptor> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.VehicleDescriptor> {
        public val defaultInstance: com.google.transit.realtime.VehicleDescriptor by lazy { com.google.transit.realtime.VehicleDescriptor() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.VehicleDescriptor = com.google.transit.realtime.VehicleDescriptor.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.VehicleDescriptor> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.VehicleDescriptor",
            messageClass = com.google.transit.realtime.VehicleDescriptor::class,
            messageCompanion = this,
            fields = buildList(4) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "id",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "id",
                        value = com.google.transit.realtime.VehicleDescriptor::id
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "label",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "label",
                        value = com.google.transit.realtime.VehicleDescriptor::label
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "license_plate",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "licensePlate",
                        value = com.google.transit.realtime.VehicleDescriptor::licensePlate
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "wheelchair_accessible",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.VehicleDescriptor.WheelchairAccessible.Companion, hasPresence = true),
                        jsonName = "wheelchairAccessible",
                        value = com.google.transit.realtime.VehicleDescriptor::wheelchairAccessible
                    )
                )
            }
        )
    }

    public sealed class WheelchairAccessible(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?): Boolean = other is com.google.transit.realtime.VehicleDescriptor.WheelchairAccessible && other.value == value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "VehicleDescriptor.WheelchairAccessible.${name ?: "UNRECOGNIZED"}(value=$value)"

        public object NO_VALUE : WheelchairAccessible(0, "NO_VALUE")
        public object UNKNOWN : WheelchairAccessible(1, "UNKNOWN")
        public object WHEELCHAIR_ACCESSIBLE : WheelchairAccessible(2, "WHEELCHAIR_ACCESSIBLE")
        public object WHEELCHAIR_INACCESSIBLE : WheelchairAccessible(3, "WHEELCHAIR_INACCESSIBLE")
        public class UNRECOGNIZED(value: Int) : WheelchairAccessible(value)

        public companion object : pbandk.Message.Enum.Companion<com.google.transit.realtime.VehicleDescriptor.WheelchairAccessible> {
            public val values: List<com.google.transit.realtime.VehicleDescriptor.WheelchairAccessible> by lazy { listOf(NO_VALUE, UNKNOWN, WHEELCHAIR_ACCESSIBLE, WHEELCHAIR_INACCESSIBLE) }
            override fun fromValue(value: Int): com.google.transit.realtime.VehicleDescriptor.WheelchairAccessible = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String): com.google.transit.realtime.VehicleDescriptor.WheelchairAccessible = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No WheelchairAccessible with name: $name")
        }
    }
}

@pbandk.Export
public data class EntitySelector(
    val agencyId: String? = null,
    val routeId: String? = null,
    val routeType: Int? = null,
    val trip: com.google.transit.realtime.TripDescriptor? = null,
    val stopId: String? = null,
    val directionId: Int? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.EntitySelector = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.EntitySelector> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.EntitySelector> {
        public val defaultInstance: com.google.transit.realtime.EntitySelector by lazy { com.google.transit.realtime.EntitySelector() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.EntitySelector = com.google.transit.realtime.EntitySelector.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.EntitySelector> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.EntitySelector",
            messageClass = com.google.transit.realtime.EntitySelector::class,
            messageCompanion = this,
            fields = buildList(6) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "agency_id",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "agencyId",
                        value = com.google.transit.realtime.EntitySelector::agencyId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "route_id",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "routeId",
                        value = com.google.transit.realtime.EntitySelector::routeId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "route_type",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.Int32(hasPresence = true),
                        jsonName = "routeType",
                        value = com.google.transit.realtime.EntitySelector::routeType
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "trip",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripDescriptor.Companion),
                        jsonName = "trip",
                        value = com.google.transit.realtime.EntitySelector::trip
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_id",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "stopId",
                        value = com.google.transit.realtime.EntitySelector::stopId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "direction_id",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "directionId",
                        value = com.google.transit.realtime.EntitySelector::directionId
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class TranslatedString(
    val translation: List<com.google.transit.realtime.TranslatedString.Translation> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TranslatedString = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TranslatedString> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.TranslatedString> {
        public val defaultInstance: com.google.transit.realtime.TranslatedString by lazy { com.google.transit.realtime.TranslatedString() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TranslatedString = com.google.transit.realtime.TranslatedString.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TranslatedString> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.TranslatedString",
            messageClass = com.google.transit.realtime.TranslatedString::class,
            messageCompanion = this,
            fields = buildList(1) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "translation",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<com.google.transit.realtime.TranslatedString.Translation>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Translation.Companion)),
                        jsonName = "translation",
                        value = com.google.transit.realtime.TranslatedString::translation
                    )
                )
            }
        )
    }

    public data class Translation(
        val text: String,
        val language: String? = null,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
        @property:pbandk.PbandkInternal
        override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
    ) : pbandk.ExtendableMessage {
        override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TranslatedString.Translation = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TranslatedString.Translation> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<com.google.transit.realtime.TranslatedString.Translation> {
            override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TranslatedString.Translation = com.google.transit.realtime.TranslatedString.Translation.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TranslatedString.Translation> = pbandk.MessageDescriptor(
                fullName = "transit_realtime.TranslatedString.Translation",
                messageClass = com.google.transit.realtime.TranslatedString.Translation::class,
                messageCompanion = this,
                fields = buildList(2) {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "text",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "text",
                            value = com.google.transit.realtime.TranslatedString.Translation::text
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "language",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "language",
                            value = com.google.transit.realtime.TranslatedString.Translation::language
                        )
                    )
                }
            )
        }
    }
}

@pbandk.Export
public data class TranslatedImage(
    val localizedImage: List<com.google.transit.realtime.TranslatedImage.LocalizedImage> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TranslatedImage = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TranslatedImage> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.TranslatedImage> {
        public val defaultInstance: com.google.transit.realtime.TranslatedImage by lazy { com.google.transit.realtime.TranslatedImage() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TranslatedImage = com.google.transit.realtime.TranslatedImage.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TranslatedImage> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.TranslatedImage",
            messageClass = com.google.transit.realtime.TranslatedImage::class,
            messageCompanion = this,
            fields = buildList(1) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "localized_image",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<com.google.transit.realtime.TranslatedImage.LocalizedImage>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedImage.LocalizedImage.Companion)),
                        jsonName = "localizedImage",
                        value = com.google.transit.realtime.TranslatedImage::localizedImage
                    )
                )
            }
        )
    }

    public data class LocalizedImage(
        val url: String,
        val mediaType: String,
        val language: String? = null,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
        @property:pbandk.PbandkInternal
        override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
    ) : pbandk.ExtendableMessage {
        override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TranslatedImage.LocalizedImage = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TranslatedImage.LocalizedImage> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<com.google.transit.realtime.TranslatedImage.LocalizedImage> {
            override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TranslatedImage.LocalizedImage = com.google.transit.realtime.TranslatedImage.LocalizedImage.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TranslatedImage.LocalizedImage> = pbandk.MessageDescriptor(
                fullName = "transit_realtime.TranslatedImage.LocalizedImage",
                messageClass = com.google.transit.realtime.TranslatedImage.LocalizedImage::class,
                messageCompanion = this,
                fields = buildList(3) {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "url",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "url",
                            value = com.google.transit.realtime.TranslatedImage.LocalizedImage::url
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "media_type",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "mediaType",
                            value = com.google.transit.realtime.TranslatedImage.LocalizedImage::mediaType
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "language",
                            number = 3,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "language",
                            value = com.google.transit.realtime.TranslatedImage.LocalizedImage::language
                        )
                    )
                }
            )
        }
    }
}

@pbandk.Export
public data class Shape(
    val shapeId: String? = null,
    val encodedPolyline: String? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.Shape = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.Shape> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.Shape> {
        public val defaultInstance: com.google.transit.realtime.Shape by lazy { com.google.transit.realtime.Shape() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.Shape = com.google.transit.realtime.Shape.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.Shape> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.Shape",
            messageClass = com.google.transit.realtime.Shape::class,
            messageCompanion = this,
            fields = buildList(2) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "shape_id",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "shapeId",
                        value = com.google.transit.realtime.Shape::shapeId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "encoded_polyline",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "encodedPolyline",
                        value = com.google.transit.realtime.Shape::encodedPolyline
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class Stop(
    val stopId: String? = null,
    val stopCode: com.google.transit.realtime.TranslatedString? = null,
    val stopName: com.google.transit.realtime.TranslatedString? = null,
    val ttsStopName: com.google.transit.realtime.TranslatedString? = null,
    val stopDesc: com.google.transit.realtime.TranslatedString? = null,
    val stopLat: Float? = null,
    val stopLon: Float? = null,
    val zoneId: String? = null,
    val stopUrl: com.google.transit.realtime.TranslatedString? = null,
    val parentStation: String? = null,
    val stopTimezone: String? = null,
    val wheelchairBoarding: com.google.transit.realtime.Stop.WheelchairBoarding? = null,
    val levelId: String? = null,
    val platformCode: com.google.transit.realtime.TranslatedString? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.Stop = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.Stop> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.Stop> {
        public val defaultInstance: com.google.transit.realtime.Stop by lazy { com.google.transit.realtime.Stop() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.Stop = com.google.transit.realtime.Stop.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.Stop> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.Stop",
            messageClass = com.google.transit.realtime.Stop::class,
            messageCompanion = this,
            fields = buildList(14) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_id",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "stopId",
                        value = com.google.transit.realtime.Stop::stopId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_code",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "stopCode",
                        value = com.google.transit.realtime.Stop::stopCode
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_name",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "stopName",
                        value = com.google.transit.realtime.Stop::stopName
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "tts_stop_name",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "ttsStopName",
                        value = com.google.transit.realtime.Stop::ttsStopName
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_desc",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "stopDesc",
                        value = com.google.transit.realtime.Stop::stopDesc
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_lat",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Primitive.Float(hasPresence = true),
                        jsonName = "stopLat",
                        value = com.google.transit.realtime.Stop::stopLat
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_lon",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Primitive.Float(hasPresence = true),
                        jsonName = "stopLon",
                        value = com.google.transit.realtime.Stop::stopLon
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "zone_id",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "zoneId",
                        value = com.google.transit.realtime.Stop::zoneId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_url",
                        number = 9,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "stopUrl",
                        value = com.google.transit.realtime.Stop::stopUrl
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "parent_station",
                        number = 11,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "parentStation",
                        value = com.google.transit.realtime.Stop::parentStation
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_timezone",
                        number = 12,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "stopTimezone",
                        value = com.google.transit.realtime.Stop::stopTimezone
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "wheelchair_boarding",
                        number = 13,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = com.google.transit.realtime.Stop.WheelchairBoarding.Companion, hasPresence = true),
                        jsonName = "wheelchairBoarding",
                        value = com.google.transit.realtime.Stop::wheelchairBoarding
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "level_id",
                        number = 14,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "levelId",
                        value = com.google.transit.realtime.Stop::levelId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "platform_code",
                        number = 15,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TranslatedString.Companion),
                        jsonName = "platformCode",
                        value = com.google.transit.realtime.Stop::platformCode
                    )
                )
            }
        )
    }

    public sealed class WheelchairBoarding(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?): Boolean = other is com.google.transit.realtime.Stop.WheelchairBoarding && other.value == value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "Stop.WheelchairBoarding.${name ?: "UNRECOGNIZED"}(value=$value)"

        public object UNKNOWN : WheelchairBoarding(0, "UNKNOWN")
        public object AVAILABLE : WheelchairBoarding(1, "AVAILABLE")
        public object NOT_AVAILABLE : WheelchairBoarding(2, "NOT_AVAILABLE")
        public class UNRECOGNIZED(value: Int) : WheelchairBoarding(value)

        public companion object : pbandk.Message.Enum.Companion<com.google.transit.realtime.Stop.WheelchairBoarding> {
            public val values: List<com.google.transit.realtime.Stop.WheelchairBoarding> by lazy { listOf(UNKNOWN, AVAILABLE, NOT_AVAILABLE) }
            override fun fromValue(value: Int): com.google.transit.realtime.Stop.WheelchairBoarding = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String): com.google.transit.realtime.Stop.WheelchairBoarding = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No WheelchairBoarding with name: $name")
        }
    }
}

@pbandk.Export
public data class TripModifications(
    val selectedTrips: List<com.google.transit.realtime.TripModifications.SelectedTrips> = emptyList(),
    val startTimes: List<String> = emptyList(),
    val serviceDates: List<String> = emptyList(),
    val modifications: List<com.google.transit.realtime.TripModifications.Modification> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TripModifications = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripModifications> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.TripModifications> {
        public val defaultInstance: com.google.transit.realtime.TripModifications by lazy { com.google.transit.realtime.TripModifications() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TripModifications = com.google.transit.realtime.TripModifications.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripModifications> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.TripModifications",
            messageClass = com.google.transit.realtime.TripModifications::class,
            messageCompanion = this,
            fields = buildList(4) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "selected_trips",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<com.google.transit.realtime.TripModifications.SelectedTrips>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripModifications.SelectedTrips.Companion)),
                        jsonName = "selectedTrips",
                        value = com.google.transit.realtime.TripModifications::selectedTrips
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "start_times",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Repeated<String>(valueType = pbandk.FieldDescriptor.Type.Primitive.String()),
                        jsonName = "startTimes",
                        value = com.google.transit.realtime.TripModifications::startTimes
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "service_dates",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Repeated<String>(valueType = pbandk.FieldDescriptor.Type.Primitive.String()),
                        jsonName = "serviceDates",
                        value = com.google.transit.realtime.TripModifications::serviceDates
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "modifications",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Repeated<com.google.transit.realtime.TripModifications.Modification>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.TripModifications.Modification.Companion)),
                        jsonName = "modifications",
                        value = com.google.transit.realtime.TripModifications::modifications
                    )
                )
            }
        )
    }

    public data class Modification(
        val startStopSelector: com.google.transit.realtime.StopSelector? = null,
        val endStopSelector: com.google.transit.realtime.StopSelector? = null,
        val propagatedModificationDelay: Int? = null,
        val replacementStops: List<com.google.transit.realtime.ReplacementStop> = emptyList(),
        val serviceAlertId: String? = null,
        val lastModifiedTime: Long? = null,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
        @property:pbandk.PbandkInternal
        override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
    ) : pbandk.ExtendableMessage {
        override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TripModifications.Modification = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripModifications.Modification> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<com.google.transit.realtime.TripModifications.Modification> {
            public val defaultInstance: com.google.transit.realtime.TripModifications.Modification by lazy { com.google.transit.realtime.TripModifications.Modification() }
            override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TripModifications.Modification = com.google.transit.realtime.TripModifications.Modification.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripModifications.Modification> = pbandk.MessageDescriptor(
                fullName = "transit_realtime.TripModifications.Modification",
                messageClass = com.google.transit.realtime.TripModifications.Modification::class,
                messageCompanion = this,
                fields = buildList(6) {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "start_stop_selector",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.StopSelector.Companion),
                            jsonName = "startStopSelector",
                            value = com.google.transit.realtime.TripModifications.Modification::startStopSelector
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "end_stop_selector",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.StopSelector.Companion),
                            jsonName = "endStopSelector",
                            value = com.google.transit.realtime.TripModifications.Modification::endStopSelector
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "propagated_modification_delay",
                            number = 3,
                            type = pbandk.FieldDescriptor.Type.Primitive.Int32(hasPresence = true),
                            jsonName = "propagatedModificationDelay",
                            value = com.google.transit.realtime.TripModifications.Modification::propagatedModificationDelay
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "replacement_stops",
                            number = 4,
                            type = pbandk.FieldDescriptor.Type.Repeated<com.google.transit.realtime.ReplacementStop>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = com.google.transit.realtime.ReplacementStop.Companion)),
                            jsonName = "replacementStops",
                            value = com.google.transit.realtime.TripModifications.Modification::replacementStops
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "service_alert_id",
                            number = 5,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "serviceAlertId",
                            value = com.google.transit.realtime.TripModifications.Modification::serviceAlertId
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "last_modified_time",
                            number = 6,
                            type = pbandk.FieldDescriptor.Type.Primitive.UInt64(hasPresence = true),
                            jsonName = "lastModifiedTime",
                            value = com.google.transit.realtime.TripModifications.Modification::lastModifiedTime
                        )
                    )
                }
            )
        }
    }

    public data class SelectedTrips(
        val tripIds: List<String> = emptyList(),
        val shapeId: String? = null,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
        @property:pbandk.PbandkInternal
        override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
    ) : pbandk.ExtendableMessage {
        override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.TripModifications.SelectedTrips = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripModifications.SelectedTrips> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<com.google.transit.realtime.TripModifications.SelectedTrips> {
            public val defaultInstance: com.google.transit.realtime.TripModifications.SelectedTrips by lazy { com.google.transit.realtime.TripModifications.SelectedTrips() }
            override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.TripModifications.SelectedTrips = com.google.transit.realtime.TripModifications.SelectedTrips.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.TripModifications.SelectedTrips> = pbandk.MessageDescriptor(
                fullName = "transit_realtime.TripModifications.SelectedTrips",
                messageClass = com.google.transit.realtime.TripModifications.SelectedTrips::class,
                messageCompanion = this,
                fields = buildList(2) {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "trip_ids",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Repeated<String>(valueType = pbandk.FieldDescriptor.Type.Primitive.String()),
                            jsonName = "tripIds",
                            value = com.google.transit.realtime.TripModifications.SelectedTrips::tripIds
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "shape_id",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "shapeId",
                            value = com.google.transit.realtime.TripModifications.SelectedTrips::shapeId
                        )
                    )
                }
            )
        }
    }
}

@pbandk.Export
public data class StopSelector(
    val stopSequence: Int? = null,
    val stopId: String? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.StopSelector = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.StopSelector> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.StopSelector> {
        public val defaultInstance: com.google.transit.realtime.StopSelector by lazy { com.google.transit.realtime.StopSelector() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.StopSelector = com.google.transit.realtime.StopSelector.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.StopSelector> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.StopSelector",
            messageClass = com.google.transit.realtime.StopSelector::class,
            messageCompanion = this,
            fields = buildList(2) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_sequence",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "stopSequence",
                        value = com.google.transit.realtime.StopSelector::stopSequence
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_id",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "stopId",
                        value = com.google.transit.realtime.StopSelector::stopId
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class ReplacementStop(
    val travelTimeToStop: Int? = null,
    val stopId: String? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
    @property:pbandk.PbandkInternal
    override val extensionFields: pbandk.ExtensionFieldSet = pbandk.ExtensionFieldSet()
) : pbandk.ExtendableMessage {
    override operator fun plus(other: pbandk.Message?): com.google.transit.realtime.ReplacementStop = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.ReplacementStop> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<com.google.transit.realtime.ReplacementStop> {
        public val defaultInstance: com.google.transit.realtime.ReplacementStop by lazy { com.google.transit.realtime.ReplacementStop() }
        override fun decodeWith(u: pbandk.MessageDecoder): com.google.transit.realtime.ReplacementStop = com.google.transit.realtime.ReplacementStop.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<com.google.transit.realtime.ReplacementStop> = pbandk.MessageDescriptor(
            fullName = "transit_realtime.ReplacementStop",
            messageClass = com.google.transit.realtime.ReplacementStop::class,
            messageCompanion = this,
            fields = buildList(2) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "travel_time_to_stop",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.Int32(hasPresence = true),
                        jsonName = "travelTimeToStop",
                        value = com.google.transit.realtime.ReplacementStop::travelTimeToStop
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "stop_id",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "stopId",
                        value = com.google.transit.realtime.ReplacementStop::stopId
                    )
                )
            }
        )
    }
}

private fun FeedMessage.protoMergeImpl(plus: pbandk.Message?): FeedMessage = (plus as? FeedMessage)?.let {
    it.copy(
        header = header.plus(plus.header),
        entity = entity + plus.entity,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun FeedMessage.Companion.decodeWithImpl(u: pbandk.MessageDecoder): FeedMessage {
    var header: com.google.transit.realtime.FeedHeader? = null
    var entity: pbandk.ListWithSize.Builder<com.google.transit.realtime.FeedEntity>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> header = _fieldValue as com.google.transit.realtime.FeedHeader
            2 -> entity = (entity ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<com.google.transit.realtime.FeedEntity> }
        }
    }

    if (header == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("header")
    }
    return FeedMessage(header!!, pbandk.ListWithSize.Builder.fixed(entity), unknownFields)
}

private fun FeedHeader.protoMergeImpl(plus: pbandk.Message?): FeedHeader = (plus as? FeedHeader)?.let {
    it.copy(
        incrementality = plus.incrementality ?: incrementality,
        timestamp = plus.timestamp ?: timestamp,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun FeedHeader.Companion.decodeWithImpl(u: pbandk.MessageDecoder): FeedHeader {
    var gtfsRealtimeVersion: String? = null
    var incrementality: com.google.transit.realtime.FeedHeader.Incrementality? = null
    var timestamp: Long? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> gtfsRealtimeVersion = _fieldValue as String
            2 -> incrementality = _fieldValue as com.google.transit.realtime.FeedHeader.Incrementality
            3 -> timestamp = _fieldValue as Long
        }
    }

    if (gtfsRealtimeVersion == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("gtfs_realtime_version")
    }
    return FeedHeader(gtfsRealtimeVersion!!, incrementality, timestamp, unknownFields)
}

private fun FeedEntity.protoMergeImpl(plus: pbandk.Message?): FeedEntity = (plus as? FeedEntity)?.let {
    it.copy(
        isDeleted = plus.isDeleted ?: isDeleted,
        tripUpdate = tripUpdate?.plus(plus.tripUpdate) ?: plus.tripUpdate,
        vehicle = vehicle?.plus(plus.vehicle) ?: plus.vehicle,
        alert = alert?.plus(plus.alert) ?: plus.alert,
        shape = shape?.plus(plus.shape) ?: plus.shape,
        stop = stop?.plus(plus.stop) ?: plus.stop,
        tripModifications = tripModifications?.plus(plus.tripModifications) ?: plus.tripModifications,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun FeedEntity.Companion.decodeWithImpl(u: pbandk.MessageDecoder): FeedEntity {
    var id: String? = null
    var isDeleted: Boolean? = null
    var tripUpdate: com.google.transit.realtime.TripUpdate? = null
    var vehicle: com.google.transit.realtime.VehiclePosition? = null
    var alert: com.google.transit.realtime.Alert? = null
    var shape: com.google.transit.realtime.Shape? = null
    var stop: com.google.transit.realtime.Stop? = null
    var tripModifications: com.google.transit.realtime.TripModifications? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> id = _fieldValue as String
            2 -> isDeleted = _fieldValue as Boolean
            3 -> tripUpdate = _fieldValue as com.google.transit.realtime.TripUpdate
            4 -> vehicle = _fieldValue as com.google.transit.realtime.VehiclePosition
            5 -> alert = _fieldValue as com.google.transit.realtime.Alert
            6 -> shape = _fieldValue as com.google.transit.realtime.Shape
            7 -> stop = _fieldValue as com.google.transit.realtime.Stop
            8 -> tripModifications = _fieldValue as com.google.transit.realtime.TripModifications
        }
    }

    if (id == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("id")
    }
    return FeedEntity(id!!, isDeleted, tripUpdate, vehicle,
        alert, shape, stop, tripModifications, unknownFields)
}

private fun TripUpdate.protoMergeImpl(plus: pbandk.Message?): TripUpdate = (plus as? TripUpdate)?.let {
    it.copy(
        trip = trip.plus(plus.trip),
        vehicle = vehicle?.plus(plus.vehicle) ?: plus.vehicle,
        stopTimeUpdate = stopTimeUpdate + plus.stopTimeUpdate,
        timestamp = plus.timestamp ?: timestamp,
        delay = plus.delay ?: delay,
        tripProperties = tripProperties?.plus(plus.tripProperties) ?: plus.tripProperties,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TripUpdate.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TripUpdate {
    var trip: com.google.transit.realtime.TripDescriptor? = null
    var vehicle: com.google.transit.realtime.VehicleDescriptor? = null
    var stopTimeUpdate: pbandk.ListWithSize.Builder<com.google.transit.realtime.TripUpdate.StopTimeUpdate>? = null
    var timestamp: Long? = null
    var delay: Int? = null
    var tripProperties: com.google.transit.realtime.TripUpdate.TripProperties? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> trip = _fieldValue as com.google.transit.realtime.TripDescriptor
            2 -> stopTimeUpdate = (stopTimeUpdate ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<com.google.transit.realtime.TripUpdate.StopTimeUpdate> }
            3 -> vehicle = _fieldValue as com.google.transit.realtime.VehicleDescriptor
            4 -> timestamp = _fieldValue as Long
            5 -> delay = _fieldValue as Int
            6 -> tripProperties = _fieldValue as com.google.transit.realtime.TripUpdate.TripProperties
        }
    }

    if (trip == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("trip")
    }
    return TripUpdate(trip!!, vehicle, pbandk.ListWithSize.Builder.fixed(stopTimeUpdate), timestamp,
        delay, tripProperties, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForTripUpdateStopTimeEvent")
public fun TripUpdate.StopTimeEvent?.orDefault(): com.google.transit.realtime.TripUpdate.StopTimeEvent = this ?: TripUpdate.StopTimeEvent.defaultInstance

private fun TripUpdate.StopTimeEvent.protoMergeImpl(plus: pbandk.Message?): TripUpdate.StopTimeEvent = (plus as? TripUpdate.StopTimeEvent)?.let {
    it.copy(
        delay = plus.delay ?: delay,
        time = plus.time ?: time,
        uncertainty = plus.uncertainty ?: uncertainty,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TripUpdate.StopTimeEvent.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TripUpdate.StopTimeEvent {
    var delay: Int? = null
    var time: Long? = null
    var uncertainty: Int? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> delay = _fieldValue as Int
            2 -> time = _fieldValue as Long
            3 -> uncertainty = _fieldValue as Int
        }
    }

    return TripUpdate.StopTimeEvent(delay, time, uncertainty, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForTripUpdateStopTimeUpdate")
public fun TripUpdate.StopTimeUpdate?.orDefault(): com.google.transit.realtime.TripUpdate.StopTimeUpdate = this ?: TripUpdate.StopTimeUpdate.defaultInstance

private fun TripUpdate.StopTimeUpdate.protoMergeImpl(plus: pbandk.Message?): TripUpdate.StopTimeUpdate = (plus as? TripUpdate.StopTimeUpdate)?.let {
    it.copy(
        stopSequence = plus.stopSequence ?: stopSequence,
        stopId = plus.stopId ?: stopId,
        arrival = arrival?.plus(plus.arrival) ?: plus.arrival,
        departure = departure?.plus(plus.departure) ?: plus.departure,
        departureOccupancyStatus = plus.departureOccupancyStatus ?: departureOccupancyStatus,
        scheduleRelationship = plus.scheduleRelationship ?: scheduleRelationship,
        stopTimeProperties = stopTimeProperties?.plus(plus.stopTimeProperties) ?: plus.stopTimeProperties,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TripUpdate.StopTimeUpdate.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TripUpdate.StopTimeUpdate {
    var stopSequence: Int? = null
    var stopId: String? = null
    var arrival: com.google.transit.realtime.TripUpdate.StopTimeEvent? = null
    var departure: com.google.transit.realtime.TripUpdate.StopTimeEvent? = null
    var departureOccupancyStatus: com.google.transit.realtime.VehiclePosition.OccupancyStatus? = null
    var scheduleRelationship: com.google.transit.realtime.TripUpdate.StopTimeUpdate.ScheduleRelationship? = null
    var stopTimeProperties: com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> stopSequence = _fieldValue as Int
            2 -> arrival = _fieldValue as com.google.transit.realtime.TripUpdate.StopTimeEvent
            3 -> departure = _fieldValue as com.google.transit.realtime.TripUpdate.StopTimeEvent
            4 -> stopId = _fieldValue as String
            5 -> scheduleRelationship = _fieldValue as com.google.transit.realtime.TripUpdate.StopTimeUpdate.ScheduleRelationship
            6 -> stopTimeProperties = _fieldValue as com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties
            7 -> departureOccupancyStatus = _fieldValue as com.google.transit.realtime.VehiclePosition.OccupancyStatus
        }
    }

    return TripUpdate.StopTimeUpdate(stopSequence, stopId, arrival, departure,
        departureOccupancyStatus, scheduleRelationship, stopTimeProperties, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForTripUpdateStopTimeUpdateStopTimeProperties")
public fun TripUpdate.StopTimeUpdate.StopTimeProperties?.orDefault(): com.google.transit.realtime.TripUpdate.StopTimeUpdate.StopTimeProperties = this ?: TripUpdate.StopTimeUpdate.StopTimeProperties.defaultInstance

private fun TripUpdate.StopTimeUpdate.StopTimeProperties.protoMergeImpl(plus: pbandk.Message?): TripUpdate.StopTimeUpdate.StopTimeProperties = (plus as? TripUpdate.StopTimeUpdate.StopTimeProperties)?.let {
    it.copy(
        assignedStopId = plus.assignedStopId ?: assignedStopId,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TripUpdate.StopTimeUpdate.StopTimeProperties.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TripUpdate.StopTimeUpdate.StopTimeProperties {
    var assignedStopId: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> assignedStopId = _fieldValue as String
        }
    }

    return TripUpdate.StopTimeUpdate.StopTimeProperties(assignedStopId, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForTripUpdateTripProperties")
public fun TripUpdate.TripProperties?.orDefault(): com.google.transit.realtime.TripUpdate.TripProperties = this ?: TripUpdate.TripProperties.defaultInstance

private fun TripUpdate.TripProperties.protoMergeImpl(plus: pbandk.Message?): TripUpdate.TripProperties = (plus as? TripUpdate.TripProperties)?.let {
    it.copy(
        tripId = plus.tripId ?: tripId,
        startDate = plus.startDate ?: startDate,
        startTime = plus.startTime ?: startTime,
        shapeId = plus.shapeId ?: shapeId,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TripUpdate.TripProperties.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TripUpdate.TripProperties {
    var tripId: String? = null
    var startDate: String? = null
    var startTime: String? = null
    var shapeId: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> tripId = _fieldValue as String
            2 -> startDate = _fieldValue as String
            3 -> startTime = _fieldValue as String
            4 -> shapeId = _fieldValue as String
        }
    }

    return TripUpdate.TripProperties(tripId, startDate, startTime, shapeId, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForVehiclePosition")
public fun VehiclePosition?.orDefault(): com.google.transit.realtime.VehiclePosition = this ?: VehiclePosition.defaultInstance

private fun VehiclePosition.protoMergeImpl(plus: pbandk.Message?): VehiclePosition = (plus as? VehiclePosition)?.let {
    it.copy(
        trip = trip?.plus(plus.trip) ?: plus.trip,
        vehicle = vehicle?.plus(plus.vehicle) ?: plus.vehicle,
        position = position?.plus(plus.position) ?: plus.position,
        currentStopSequence = plus.currentStopSequence ?: currentStopSequence,
        stopId = plus.stopId ?: stopId,
        currentStatus = plus.currentStatus ?: currentStatus,
        timestamp = plus.timestamp ?: timestamp,
        congestionLevel = plus.congestionLevel ?: congestionLevel,
        occupancyStatus = plus.occupancyStatus ?: occupancyStatus,
        occupancyPercentage = plus.occupancyPercentage ?: occupancyPercentage,
        multiCarriageDetails = multiCarriageDetails + plus.multiCarriageDetails,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun VehiclePosition.Companion.decodeWithImpl(u: pbandk.MessageDecoder): VehiclePosition {
    var trip: com.google.transit.realtime.TripDescriptor? = null
    var vehicle: com.google.transit.realtime.VehicleDescriptor? = null
    var position: com.google.transit.realtime.Position? = null
    var currentStopSequence: Int? = null
    var stopId: String? = null
    var currentStatus: com.google.transit.realtime.VehiclePosition.VehicleStopStatus? = null
    var timestamp: Long? = null
    var congestionLevel: com.google.transit.realtime.VehiclePosition.CongestionLevel? = null
    var occupancyStatus: com.google.transit.realtime.VehiclePosition.OccupancyStatus? = null
    var occupancyPercentage: Int? = null
    var multiCarriageDetails: pbandk.ListWithSize.Builder<com.google.transit.realtime.VehiclePosition.CarriageDetails>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> trip = _fieldValue as com.google.transit.realtime.TripDescriptor
            2 -> position = _fieldValue as com.google.transit.realtime.Position
            3 -> currentStopSequence = _fieldValue as Int
            4 -> currentStatus = _fieldValue as com.google.transit.realtime.VehiclePosition.VehicleStopStatus
            5 -> timestamp = _fieldValue as Long
            6 -> congestionLevel = _fieldValue as com.google.transit.realtime.VehiclePosition.CongestionLevel
            7 -> stopId = _fieldValue as String
            8 -> vehicle = _fieldValue as com.google.transit.realtime.VehicleDescriptor
            9 -> occupancyStatus = _fieldValue as com.google.transit.realtime.VehiclePosition.OccupancyStatus
            10 -> occupancyPercentage = _fieldValue as Int
            11 -> multiCarriageDetails = (multiCarriageDetails ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<com.google.transit.realtime.VehiclePosition.CarriageDetails> }
        }
    }

    return VehiclePosition(trip, vehicle, position, currentStopSequence,
        stopId, currentStatus, timestamp, congestionLevel,
        occupancyStatus, occupancyPercentage, pbandk.ListWithSize.Builder.fixed(multiCarriageDetails), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForVehiclePositionCarriageDetails")
public fun VehiclePosition.CarriageDetails?.orDefault(): com.google.transit.realtime.VehiclePosition.CarriageDetails = this ?: VehiclePosition.CarriageDetails.defaultInstance

private fun VehiclePosition.CarriageDetails.protoMergeImpl(plus: pbandk.Message?): VehiclePosition.CarriageDetails = (plus as? VehiclePosition.CarriageDetails)?.let {
    it.copy(
        id = plus.id ?: id,
        label = plus.label ?: label,
        occupancyStatus = plus.occupancyStatus ?: occupancyStatus,
        occupancyPercentage = plus.occupancyPercentage ?: occupancyPercentage,
        carriageSequence = plus.carriageSequence ?: carriageSequence,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun VehiclePosition.CarriageDetails.Companion.decodeWithImpl(u: pbandk.MessageDecoder): VehiclePosition.CarriageDetails {
    var id: String? = null
    var label: String? = null
    var occupancyStatus: com.google.transit.realtime.VehiclePosition.OccupancyStatus? = null
    var occupancyPercentage: Int? = null
    var carriageSequence: Int? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> id = _fieldValue as String
            2 -> label = _fieldValue as String
            3 -> occupancyStatus = _fieldValue as com.google.transit.realtime.VehiclePosition.OccupancyStatus
            4 -> occupancyPercentage = _fieldValue as Int
            5 -> carriageSequence = _fieldValue as Int
        }
    }

    return VehiclePosition.CarriageDetails(id, label, occupancyStatus, occupancyPercentage,
        carriageSequence, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForAlert")
public fun Alert?.orDefault(): com.google.transit.realtime.Alert = this ?: Alert.defaultInstance

private fun Alert.protoMergeImpl(plus: pbandk.Message?): Alert = (plus as? Alert)?.let {
    it.copy(
        activePeriod = activePeriod + plus.activePeriod,
        informedEntity = informedEntity + plus.informedEntity,
        cause = plus.cause ?: cause,
        effect = plus.effect ?: effect,
        url = url?.plus(plus.url) ?: plus.url,
        headerText = headerText?.plus(plus.headerText) ?: plus.headerText,
        descriptionText = descriptionText?.plus(plus.descriptionText) ?: plus.descriptionText,
        ttsHeaderText = ttsHeaderText?.plus(plus.ttsHeaderText) ?: plus.ttsHeaderText,
        ttsDescriptionText = ttsDescriptionText?.plus(plus.ttsDescriptionText) ?: plus.ttsDescriptionText,
        severityLevel = plus.severityLevel ?: severityLevel,
        image = image?.plus(plus.image) ?: plus.image,
        imageAlternativeText = imageAlternativeText?.plus(plus.imageAlternativeText) ?: plus.imageAlternativeText,
        causeDetail = causeDetail?.plus(plus.causeDetail) ?: plus.causeDetail,
        effectDetail = effectDetail?.plus(plus.effectDetail) ?: plus.effectDetail,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Alert.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Alert {
    var activePeriod: pbandk.ListWithSize.Builder<com.google.transit.realtime.TimeRange>? = null
    var informedEntity: pbandk.ListWithSize.Builder<com.google.transit.realtime.EntitySelector>? = null
    var cause: com.google.transit.realtime.Alert.Cause? = null
    var effect: com.google.transit.realtime.Alert.Effect? = null
    var url: com.google.transit.realtime.TranslatedString? = null
    var headerText: com.google.transit.realtime.TranslatedString? = null
    var descriptionText: com.google.transit.realtime.TranslatedString? = null
    var ttsHeaderText: com.google.transit.realtime.TranslatedString? = null
    var ttsDescriptionText: com.google.transit.realtime.TranslatedString? = null
    var severityLevel: com.google.transit.realtime.Alert.SeverityLevel? = null
    var image: com.google.transit.realtime.TranslatedImage? = null
    var imageAlternativeText: com.google.transit.realtime.TranslatedString? = null
    var causeDetail: com.google.transit.realtime.TranslatedString? = null
    var effectDetail: com.google.transit.realtime.TranslatedString? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> activePeriod = (activePeriod ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<com.google.transit.realtime.TimeRange> }
            5 -> informedEntity = (informedEntity ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<com.google.transit.realtime.EntitySelector> }
            6 -> cause = _fieldValue as com.google.transit.realtime.Alert.Cause
            7 -> effect = _fieldValue as com.google.transit.realtime.Alert.Effect
            8 -> url = _fieldValue as com.google.transit.realtime.TranslatedString
            10 -> headerText = _fieldValue as com.google.transit.realtime.TranslatedString
            11 -> descriptionText = _fieldValue as com.google.transit.realtime.TranslatedString
            12 -> ttsHeaderText = _fieldValue as com.google.transit.realtime.TranslatedString
            13 -> ttsDescriptionText = _fieldValue as com.google.transit.realtime.TranslatedString
            14 -> severityLevel = _fieldValue as com.google.transit.realtime.Alert.SeverityLevel
            15 -> image = _fieldValue as com.google.transit.realtime.TranslatedImage
            16 -> imageAlternativeText = _fieldValue as com.google.transit.realtime.TranslatedString
            17 -> causeDetail = _fieldValue as com.google.transit.realtime.TranslatedString
            18 -> effectDetail = _fieldValue as com.google.transit.realtime.TranslatedString
        }
    }

    return Alert(pbandk.ListWithSize.Builder.fixed(activePeriod), pbandk.ListWithSize.Builder.fixed(informedEntity), cause, effect,
        url, headerText, descriptionText, ttsHeaderText,
        ttsDescriptionText, severityLevel, image, imageAlternativeText,
        causeDetail, effectDetail, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForTimeRange")
public fun TimeRange?.orDefault(): com.google.transit.realtime.TimeRange = this ?: TimeRange.defaultInstance

private fun TimeRange.protoMergeImpl(plus: pbandk.Message?): TimeRange = (plus as? TimeRange)?.let {
    it.copy(
        start = plus.start ?: start,
        end = plus.end ?: end,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TimeRange.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TimeRange {
    var start: Long? = null
    var end: Long? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> start = _fieldValue as Long
            2 -> end = _fieldValue as Long
        }
    }

    return TimeRange(start, end, unknownFields)
}

private fun Position.protoMergeImpl(plus: pbandk.Message?): Position = (plus as? Position)?.let {
    it.copy(
        bearing = plus.bearing ?: bearing,
        odometer = plus.odometer ?: odometer,
        speed = plus.speed ?: speed,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Position.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Position {
    var latitude: Float? = null
    var longitude: Float? = null
    var bearing: Float? = null
    var odometer: Double? = null
    var speed: Float? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> latitude = _fieldValue as Float
            2 -> longitude = _fieldValue as Float
            3 -> bearing = _fieldValue as Float
            4 -> odometer = _fieldValue as Double
            5 -> speed = _fieldValue as Float
        }
    }

    if (latitude == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("latitude")
    }
    if (longitude == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("longitude")
    }
    return Position(latitude!!, longitude!!, bearing, odometer,
        speed, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForTripDescriptor")
public fun TripDescriptor?.orDefault(): com.google.transit.realtime.TripDescriptor = this ?: TripDescriptor.defaultInstance

private fun TripDescriptor.protoMergeImpl(plus: pbandk.Message?): TripDescriptor = (plus as? TripDescriptor)?.let {
    it.copy(
        tripId = plus.tripId ?: tripId,
        routeId = plus.routeId ?: routeId,
        directionId = plus.directionId ?: directionId,
        startTime = plus.startTime ?: startTime,
        startDate = plus.startDate ?: startDate,
        scheduleRelationship = plus.scheduleRelationship ?: scheduleRelationship,
        modifiedTrip = modifiedTrip?.plus(plus.modifiedTrip) ?: plus.modifiedTrip,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TripDescriptor.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TripDescriptor {
    var tripId: String? = null
    var routeId: String? = null
    var directionId: Int? = null
    var startTime: String? = null
    var startDate: String? = null
    var scheduleRelationship: com.google.transit.realtime.TripDescriptor.ScheduleRelationship? = null
    var modifiedTrip: com.google.transit.realtime.TripDescriptor.ModifiedTripSelector? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> tripId = _fieldValue as String
            2 -> startTime = _fieldValue as String
            3 -> startDate = _fieldValue as String
            4 -> scheduleRelationship = _fieldValue as com.google.transit.realtime.TripDescriptor.ScheduleRelationship
            5 -> routeId = _fieldValue as String
            6 -> directionId = _fieldValue as Int
            7 -> modifiedTrip = _fieldValue as com.google.transit.realtime.TripDescriptor.ModifiedTripSelector
        }
    }

    return TripDescriptor(tripId, routeId, directionId, startTime,
        startDate, scheduleRelationship, modifiedTrip, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForTripDescriptorModifiedTripSelector")
public fun TripDescriptor.ModifiedTripSelector?.orDefault(): com.google.transit.realtime.TripDescriptor.ModifiedTripSelector = this ?: TripDescriptor.ModifiedTripSelector.defaultInstance

private fun TripDescriptor.ModifiedTripSelector.protoMergeImpl(plus: pbandk.Message?): TripDescriptor.ModifiedTripSelector = (plus as? TripDescriptor.ModifiedTripSelector)?.let {
    it.copy(
        modificationsId = plus.modificationsId ?: modificationsId,
        affectedTripId = plus.affectedTripId ?: affectedTripId,
        startTime = plus.startTime ?: startTime,
        startDate = plus.startDate ?: startDate,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TripDescriptor.ModifiedTripSelector.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TripDescriptor.ModifiedTripSelector {
    var modificationsId: String? = null
    var affectedTripId: String? = null
    var startTime: String? = null
    var startDate: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> modificationsId = _fieldValue as String
            2 -> affectedTripId = _fieldValue as String
            3 -> startTime = _fieldValue as String
            4 -> startDate = _fieldValue as String
        }
    }

    return TripDescriptor.ModifiedTripSelector(modificationsId, affectedTripId, startTime, startDate, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForVehicleDescriptor")
public fun VehicleDescriptor?.orDefault(): com.google.transit.realtime.VehicleDescriptor = this ?: VehicleDescriptor.defaultInstance

private fun VehicleDescriptor.protoMergeImpl(plus: pbandk.Message?): VehicleDescriptor = (plus as? VehicleDescriptor)?.let {
    it.copy(
        id = plus.id ?: id,
        label = plus.label ?: label,
        licensePlate = plus.licensePlate ?: licensePlate,
        wheelchairAccessible = plus.wheelchairAccessible ?: wheelchairAccessible,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun VehicleDescriptor.Companion.decodeWithImpl(u: pbandk.MessageDecoder): VehicleDescriptor {
    var id: String? = null
    var label: String? = null
    var licensePlate: String? = null
    var wheelchairAccessible: com.google.transit.realtime.VehicleDescriptor.WheelchairAccessible? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> id = _fieldValue as String
            2 -> label = _fieldValue as String
            3 -> licensePlate = _fieldValue as String
            4 -> wheelchairAccessible = _fieldValue as com.google.transit.realtime.VehicleDescriptor.WheelchairAccessible
        }
    }

    return VehicleDescriptor(id, label, licensePlate, wheelchairAccessible, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForEntitySelector")
public fun EntitySelector?.orDefault(): com.google.transit.realtime.EntitySelector = this ?: EntitySelector.defaultInstance

private fun EntitySelector.protoMergeImpl(plus: pbandk.Message?): EntitySelector = (plus as? EntitySelector)?.let {
    it.copy(
        agencyId = plus.agencyId ?: agencyId,
        routeId = plus.routeId ?: routeId,
        routeType = plus.routeType ?: routeType,
        trip = trip?.plus(plus.trip) ?: plus.trip,
        stopId = plus.stopId ?: stopId,
        directionId = plus.directionId ?: directionId,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun EntitySelector.Companion.decodeWithImpl(u: pbandk.MessageDecoder): EntitySelector {
    var agencyId: String? = null
    var routeId: String? = null
    var routeType: Int? = null
    var trip: com.google.transit.realtime.TripDescriptor? = null
    var stopId: String? = null
    var directionId: Int? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> agencyId = _fieldValue as String
            2 -> routeId = _fieldValue as String
            3 -> routeType = _fieldValue as Int
            4 -> trip = _fieldValue as com.google.transit.realtime.TripDescriptor
            5 -> stopId = _fieldValue as String
            6 -> directionId = _fieldValue as Int
        }
    }

    return EntitySelector(agencyId, routeId, routeType, trip,
        stopId, directionId, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForTranslatedString")
public fun TranslatedString?.orDefault(): com.google.transit.realtime.TranslatedString = this ?: TranslatedString.defaultInstance

private fun TranslatedString.protoMergeImpl(plus: pbandk.Message?): TranslatedString = (plus as? TranslatedString)?.let {
    it.copy(
        translation = translation + plus.translation,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TranslatedString.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TranslatedString {
    var translation: pbandk.ListWithSize.Builder<com.google.transit.realtime.TranslatedString.Translation>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> translation = (translation ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<com.google.transit.realtime.TranslatedString.Translation> }
        }
    }

    return TranslatedString(pbandk.ListWithSize.Builder.fixed(translation), unknownFields)
}

private fun TranslatedString.Translation.protoMergeImpl(plus: pbandk.Message?): TranslatedString.Translation = (plus as? TranslatedString.Translation)?.let {
    it.copy(
        language = plus.language ?: language,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TranslatedString.Translation.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TranslatedString.Translation {
    var text: String? = null
    var language: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> text = _fieldValue as String
            2 -> language = _fieldValue as String
        }
    }

    if (text == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("text")
    }
    return TranslatedString.Translation(text!!, language, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForTranslatedImage")
public fun TranslatedImage?.orDefault(): com.google.transit.realtime.TranslatedImage = this ?: TranslatedImage.defaultInstance

private fun TranslatedImage.protoMergeImpl(plus: pbandk.Message?): TranslatedImage = (plus as? TranslatedImage)?.let {
    it.copy(
        localizedImage = localizedImage + plus.localizedImage,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TranslatedImage.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TranslatedImage {
    var localizedImage: pbandk.ListWithSize.Builder<com.google.transit.realtime.TranslatedImage.LocalizedImage>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> localizedImage = (localizedImage ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<com.google.transit.realtime.TranslatedImage.LocalizedImage> }
        }
    }

    return TranslatedImage(pbandk.ListWithSize.Builder.fixed(localizedImage), unknownFields)
}

private fun TranslatedImage.LocalizedImage.protoMergeImpl(plus: pbandk.Message?): TranslatedImage.LocalizedImage = (plus as? TranslatedImage.LocalizedImage)?.let {
    it.copy(
        language = plus.language ?: language,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TranslatedImage.LocalizedImage.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TranslatedImage.LocalizedImage {
    var url: String? = null
    var mediaType: String? = null
    var language: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> url = _fieldValue as String
            2 -> mediaType = _fieldValue as String
            3 -> language = _fieldValue as String
        }
    }

    if (url == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("url")
    }
    if (mediaType == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("media_type")
    }
    return TranslatedImage.LocalizedImage(url!!, mediaType!!, language, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForShape")
public fun Shape?.orDefault(): com.google.transit.realtime.Shape = this ?: Shape.defaultInstance

private fun Shape.protoMergeImpl(plus: pbandk.Message?): Shape = (plus as? Shape)?.let {
    it.copy(
        shapeId = plus.shapeId ?: shapeId,
        encodedPolyline = plus.encodedPolyline ?: encodedPolyline,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Shape.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Shape {
    var shapeId: String? = null
    var encodedPolyline: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> shapeId = _fieldValue as String
            2 -> encodedPolyline = _fieldValue as String
        }
    }

    return Shape(shapeId, encodedPolyline, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForStop")
public fun Stop?.orDefault(): com.google.transit.realtime.Stop = this ?: Stop.defaultInstance

private fun Stop.protoMergeImpl(plus: pbandk.Message?): Stop = (plus as? Stop)?.let {
    it.copy(
        stopId = plus.stopId ?: stopId,
        stopCode = stopCode?.plus(plus.stopCode) ?: plus.stopCode,
        stopName = stopName?.plus(plus.stopName) ?: plus.stopName,
        ttsStopName = ttsStopName?.plus(plus.ttsStopName) ?: plus.ttsStopName,
        stopDesc = stopDesc?.plus(plus.stopDesc) ?: plus.stopDesc,
        stopLat = plus.stopLat ?: stopLat,
        stopLon = plus.stopLon ?: stopLon,
        zoneId = plus.zoneId ?: zoneId,
        stopUrl = stopUrl?.plus(plus.stopUrl) ?: plus.stopUrl,
        parentStation = plus.parentStation ?: parentStation,
        stopTimezone = plus.stopTimezone ?: stopTimezone,
        wheelchairBoarding = plus.wheelchairBoarding ?: wheelchairBoarding,
        levelId = plus.levelId ?: levelId,
        platformCode = platformCode?.plus(plus.platformCode) ?: plus.platformCode,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Stop.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Stop {
    var stopId: String? = null
    var stopCode: com.google.transit.realtime.TranslatedString? = null
    var stopName: com.google.transit.realtime.TranslatedString? = null
    var ttsStopName: com.google.transit.realtime.TranslatedString? = null
    var stopDesc: com.google.transit.realtime.TranslatedString? = null
    var stopLat: Float? = null
    var stopLon: Float? = null
    var zoneId: String? = null
    var stopUrl: com.google.transit.realtime.TranslatedString? = null
    var parentStation: String? = null
    var stopTimezone: String? = null
    var wheelchairBoarding: com.google.transit.realtime.Stop.WheelchairBoarding? = null
    var levelId: String? = null
    var platformCode: com.google.transit.realtime.TranslatedString? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> stopId = _fieldValue as String
            2 -> stopCode = _fieldValue as com.google.transit.realtime.TranslatedString
            3 -> stopName = _fieldValue as com.google.transit.realtime.TranslatedString
            4 -> ttsStopName = _fieldValue as com.google.transit.realtime.TranslatedString
            5 -> stopDesc = _fieldValue as com.google.transit.realtime.TranslatedString
            6 -> stopLat = _fieldValue as Float
            7 -> stopLon = _fieldValue as Float
            8 -> zoneId = _fieldValue as String
            9 -> stopUrl = _fieldValue as com.google.transit.realtime.TranslatedString
            11 -> parentStation = _fieldValue as String
            12 -> stopTimezone = _fieldValue as String
            13 -> wheelchairBoarding = _fieldValue as com.google.transit.realtime.Stop.WheelchairBoarding
            14 -> levelId = _fieldValue as String
            15 -> platformCode = _fieldValue as com.google.transit.realtime.TranslatedString
        }
    }

    return Stop(stopId, stopCode, stopName, ttsStopName,
        stopDesc, stopLat, stopLon, zoneId,
        stopUrl, parentStation, stopTimezone, wheelchairBoarding,
        levelId, platformCode, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForTripModifications")
public fun TripModifications?.orDefault(): com.google.transit.realtime.TripModifications = this ?: TripModifications.defaultInstance

private fun TripModifications.protoMergeImpl(plus: pbandk.Message?): TripModifications = (plus as? TripModifications)?.let {
    it.copy(
        selectedTrips = selectedTrips + plus.selectedTrips,
        startTimes = startTimes + plus.startTimes,
        serviceDates = serviceDates + plus.serviceDates,
        modifications = modifications + plus.modifications,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TripModifications.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TripModifications {
    var selectedTrips: pbandk.ListWithSize.Builder<com.google.transit.realtime.TripModifications.SelectedTrips>? = null
    var startTimes: pbandk.ListWithSize.Builder<String>? = null
    var serviceDates: pbandk.ListWithSize.Builder<String>? = null
    var modifications: pbandk.ListWithSize.Builder<com.google.transit.realtime.TripModifications.Modification>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> selectedTrips = (selectedTrips ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<com.google.transit.realtime.TripModifications.SelectedTrips> }
            2 -> startTimes = (startTimes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<String> }
            3 -> serviceDates = (serviceDates ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<String> }
            4 -> modifications = (modifications ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<com.google.transit.realtime.TripModifications.Modification> }
        }
    }

    return TripModifications(pbandk.ListWithSize.Builder.fixed(selectedTrips), pbandk.ListWithSize.Builder.fixed(startTimes), pbandk.ListWithSize.Builder.fixed(serviceDates), pbandk.ListWithSize.Builder.fixed(modifications), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForTripModificationsModification")
public fun TripModifications.Modification?.orDefault(): com.google.transit.realtime.TripModifications.Modification = this ?: TripModifications.Modification.defaultInstance

private fun TripModifications.Modification.protoMergeImpl(plus: pbandk.Message?): TripModifications.Modification = (plus as? TripModifications.Modification)?.let {
    it.copy(
        startStopSelector = startStopSelector?.plus(plus.startStopSelector) ?: plus.startStopSelector,
        endStopSelector = endStopSelector?.plus(plus.endStopSelector) ?: plus.endStopSelector,
        propagatedModificationDelay = plus.propagatedModificationDelay ?: propagatedModificationDelay,
        replacementStops = replacementStops + plus.replacementStops,
        serviceAlertId = plus.serviceAlertId ?: serviceAlertId,
        lastModifiedTime = plus.lastModifiedTime ?: lastModifiedTime,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TripModifications.Modification.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TripModifications.Modification {
    var startStopSelector: com.google.transit.realtime.StopSelector? = null
    var endStopSelector: com.google.transit.realtime.StopSelector? = null
    var propagatedModificationDelay: Int? = null
    var replacementStops: pbandk.ListWithSize.Builder<com.google.transit.realtime.ReplacementStop>? = null
    var serviceAlertId: String? = null
    var lastModifiedTime: Long? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> startStopSelector = _fieldValue as com.google.transit.realtime.StopSelector
            2 -> endStopSelector = _fieldValue as com.google.transit.realtime.StopSelector
            3 -> propagatedModificationDelay = _fieldValue as Int
            4 -> replacementStops = (replacementStops ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<com.google.transit.realtime.ReplacementStop> }
            5 -> serviceAlertId = _fieldValue as String
            6 -> lastModifiedTime = _fieldValue as Long
        }
    }

    return TripModifications.Modification(startStopSelector, endStopSelector, propagatedModificationDelay, pbandk.ListWithSize.Builder.fixed(replacementStops),
        serviceAlertId, lastModifiedTime, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForTripModificationsSelectedTrips")
public fun TripModifications.SelectedTrips?.orDefault(): com.google.transit.realtime.TripModifications.SelectedTrips = this ?: TripModifications.SelectedTrips.defaultInstance

private fun TripModifications.SelectedTrips.protoMergeImpl(plus: pbandk.Message?): TripModifications.SelectedTrips = (plus as? TripModifications.SelectedTrips)?.let {
    it.copy(
        tripIds = tripIds + plus.tripIds,
        shapeId = plus.shapeId ?: shapeId,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TripModifications.SelectedTrips.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TripModifications.SelectedTrips {
    var tripIds: pbandk.ListWithSize.Builder<String>? = null
    var shapeId: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> tripIds = (tripIds ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<String> }
            2 -> shapeId = _fieldValue as String
        }
    }

    return TripModifications.SelectedTrips(pbandk.ListWithSize.Builder.fixed(tripIds), shapeId, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForStopSelector")
public fun StopSelector?.orDefault(): com.google.transit.realtime.StopSelector = this ?: StopSelector.defaultInstance

private fun StopSelector.protoMergeImpl(plus: pbandk.Message?): StopSelector = (plus as? StopSelector)?.let {
    it.copy(
        stopSequence = plus.stopSequence ?: stopSequence,
        stopId = plus.stopId ?: stopId,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun StopSelector.Companion.decodeWithImpl(u: pbandk.MessageDecoder): StopSelector {
    var stopSequence: Int? = null
    var stopId: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> stopSequence = _fieldValue as Int
            2 -> stopId = _fieldValue as String
        }
    }

    return StopSelector(stopSequence, stopId, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForReplacementStop")
public fun ReplacementStop?.orDefault(): com.google.transit.realtime.ReplacementStop = this ?: ReplacementStop.defaultInstance

private fun ReplacementStop.protoMergeImpl(plus: pbandk.Message?): ReplacementStop = (plus as? ReplacementStop)?.let {
    it.copy(
        travelTimeToStop = plus.travelTimeToStop ?: travelTimeToStop,
        stopId = plus.stopId ?: stopId,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ReplacementStop.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ReplacementStop {
    var travelTimeToStop: Int? = null
    var stopId: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> travelTimeToStop = _fieldValue as Int
            2 -> stopId = _fieldValue as String
        }
    }

    return ReplacementStop(travelTimeToStop, stopId, unknownFields)
}
