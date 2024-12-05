package cl.emilym.sinatra.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.emilym.sinatra.data.models.ColorPair
import cl.emilym.sinatra.data.models.OnColor
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteType

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