package cl.emilym.sinatra.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import cl.emilym.sinatra.data.models.ColorPair
import cl.emilym.sinatra.data.models.OnColor
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteServiceAccessibility
import cl.emilym.sinatra.data.models.RouteTripInformation
import cl.emilym.sinatra.data.models.RouteTripStop
import cl.emilym.sinatra.data.models.RouteType
import cl.emilym.sinatra.data.models.RouteVisibility
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.room.referenced
import cl.emilym.sinatra.room.time
import cl.emilym.sinatra.room.toLong
import kotlinx.datetime.Instant

@Entity
data class RouteEntity(
    @PrimaryKey val id: String,
    val code: String,
    val displayCode: String,
    val color: String?,
    val onColor: String?,
    val name: String,
    val realTimeUrl: String?,
    val type: String,
    val designation: String?,
    @ColumnInfo(defaultValue = "0")
    val hidden: Boolean = RouteVisibility.HIDDEN_DEFAULT,
    @ColumnInfo(defaultValue = "NULL")
    val searchWeight: Double? = RouteVisibility.SEARCH_WEIGHT_DEFAULT
) {

    fun toModel(): Route {
        return Route(
            id,
            code,
            displayCode,
            if (color != null && onColor != null)
                ColorPair(color, OnColor.valueOf(onColor))
            else null,
            name,
            realTimeUrl != null,
            realTimeUrl,
            RouteType.valueOf(type),
            designation,
            RouteVisibility(
                hidden,
                searchWeight
            )
        )
    }

    companion object {
        fun fromModel(m: Route): RouteEntity {
            return RouteEntity(
                m.id,
                m.code,
                m.displayCode,
                m.colors?.color,
                m.colors?.onColor?.name,
                m.name,
                m.realTimeUrl ?: if (m.hasRealtime) "" else null,
                m.type.name,
                m.designation,
                m.routeVisibility.hidden,
                m.routeVisibility.searchWeight
            )
        }
    }

}

@Entity
data class RouteServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val resource: String,
    val serviceId: String
)

@Entity
data class RouteTripInformationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val resource: String,
    val startTime: Long?,
    val endTime: Long?,
    val bikesAllowed: String,
    val wheelchairAccessible: String,
    val heading: String?
) {

    fun toModel(stops: List<RouteTripStop>, startOfDay: Instant? = null): RouteTripInformation {
        return RouteTripInformation(
            startTime?.time?.referenced(startOfDay),
            endTime?.time?.referenced(startOfDay),
            RouteServiceAccessibility(
                ServiceBikesAllowed.valueOf(bikesAllowed),
                ServiceWheelchairAccessible.valueOf(wheelchairAccessible)
            ),
            heading,
            stops,
        )
    }

    companion object {
        fun fromModel(m: RouteTripInformation, resource: ResourceKey): RouteTripInformationEntity {
            return RouteTripInformationEntity(
                0,
                resource,
                m.startTime?.toLong(),
                m.endTime?.toLong(),
                m.accessibility.bikesAllowed.name,
                m.accessibility.wheelchairAccessible.name,
                m.heading
            )
        }
    }

}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RouteTripInformationEntity::class,
            parentColumns = ["id"],
            childColumns = ["routeTripInformationEntityId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RouteTripStopEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true)
    val routeTripInformationEntityId: Long,
    val resource: ResourceKey,
    val stopId: StopId,
    val arrivalTime: Long?,
    val departureTime: Long?,
    val sequence: Int
) {

    fun toModel(startOfDay: Instant? = null): RouteTripStop {
        return RouteTripStop(
            stopId,
            arrivalTime?.time?.referenced(startOfDay),
            departureTime?.time?.referenced(startOfDay),
            sequence,
            null
        )
    }

    companion object {
        fun fromModel(m: RouteTripStop, parentId: Long, resource: ResourceKey): RouteTripStopEntity {
            return RouteTripStopEntity(
                0,
                parentId,
                resource,
                m.stopId,
                m.arrivalTime?.toLong(),
                m.departureTime?.toLong(),
                m.sequence
            )
        }
    }
    
}

data class RouteTripStopEntityWithStop(
    @Embedded val routeTripStop: RouteTripStopEntity,
    @Relation(
        parentColumn = "stopId",
        entityColumn = "id"
    )
    val stop: StopEntity?
) {

    fun toModel(startOfDay: Instant? = null): RouteTripStop {
        return routeTripStop.toModel(startOfDay).copy(
            stop = stop?.toModel()
        )
    }

}
