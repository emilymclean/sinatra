package cl.emilym.sinatra.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import cl.emilym.sinatra.data.models.ColorPair
import cl.emilym.sinatra.data.models.OnColor
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteServiceAccessibility
import cl.emilym.sinatra.data.models.RouteTripInformation
import cl.emilym.sinatra.data.models.RouteTripStop
import cl.emilym.sinatra.data.models.RouteType
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.models.time
import cl.emilym.sinatra.data.models.toLong
import kotlin.time.Duration.Companion.milliseconds

@Entity
data class RouteEntity(
    @PrimaryKey val id: String,
    val code: String,
    val displayCode: String,
    val color: String?,
    val onColor: String?,
    val name: String,
    val realTimeUrl: String?,
    val type: String
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
            realTimeUrl,
            RouteType.valueOf(type)
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
                m.realTimeUrl,
                m.type.name
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
    val startTime: Long,
    val endTime: Long,
    val bikesAllowed: String,
    val wheelchairAccessible: String
) {

    fun toModel(stops: List<RouteTripStop>): RouteTripInformation {
        return RouteTripInformation(
            startTime.milliseconds,
            endTime.milliseconds,
            RouteServiceAccessibility(
                ServiceBikesAllowed.valueOf(bikesAllowed),
                ServiceWheelchairAccessible.valueOf(wheelchairAccessible)
            ),
            stops
        )
    }

    companion object {
        fun fromModel(m: RouteTripInformation, resource: ResourceKey): RouteTripInformationEntity {
            return RouteTripInformationEntity(
                0,
                resource,
                m.startTime.toLong(),
                m.endTime.toLong(),
                m.accessibility.bikesAllowed.name,
                m.accessibility.wheelchairAccessible.name
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
    val routeTripInformationEntityId: Long,
    val resource: ResourceKey,
    val stopId: StopId,
    val arrivalTime: Long,
    val departureTime: Long,
    val sequence: Int
) {

    fun toModel(): RouteTripStop {
        return RouteTripStop(
            stopId,
            arrivalTime.time,
            departureTime.time,
            sequence
        )
    }

    companion object {
        fun fromModel(m: RouteTripStop, parentId: Long, resource: ResourceKey): RouteTripStopEntity {
            return RouteTripStopEntity(
                0,
                parentId,
                resource,
                m.stopId,
                m.arrivalTime.toLong(),
                m.departureTime.toLong(),
                m.sequence
            )
        }
    }
    
}