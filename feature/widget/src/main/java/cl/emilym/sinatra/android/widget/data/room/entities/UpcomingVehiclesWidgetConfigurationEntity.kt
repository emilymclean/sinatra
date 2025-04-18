package cl.emilym.sinatra.android.widget.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.emilym.sinatra.android.widget.data.models.UpcomingVehiclesWidgetConfiguration

@Entity
data class UpcomingVehiclesWidgetConfigurationEntity(
    @PrimaryKey
    val widgetId: Int,
    val stopId: String,
    val routeId: String?,
    val heading: String?,
) {

    companion object {

        fun fromModel(config: UpcomingVehiclesWidgetConfiguration): UpcomingVehiclesWidgetConfigurationEntity {
            return UpcomingVehiclesWidgetConfigurationEntity(
                config.appWidgetId,
                config.stopId,
                config.routeId,
                config.heading
            )
        }

    }

    fun toModel(): UpcomingVehiclesWidgetConfiguration {
        return UpcomingVehiclesWidgetConfiguration(
            widgetId,
            stopId,
            routeId,
            heading
        )
    }

}