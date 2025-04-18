package cl.emilym.sinatra.android.widget.data.repository

import cl.emilym.sinatra.android.widget.data.models.UpcomingVehiclesWidgetConfiguration
import cl.emilym.sinatra.android.widget.data.persistence.UpcomingVehiclesWidgetPersistence
import cl.emilym.sinatra.android.widget.data.room.entities.UpcomingVehiclesWidgetConfigurationEntity

class UpcomingVehicleWidgetRepository(
    private val upcomingVehiclesWidgetPersistence: UpcomingVehiclesWidgetPersistence
) {

    suspend fun save(config: UpcomingVehiclesWidgetConfiguration) {
        upcomingVehiclesWidgetPersistence.save(config)
    }

    suspend fun get(appWidgetId: Int): UpcomingVehiclesWidgetConfiguration {
        return upcomingVehiclesWidgetPersistence.get(appWidgetId) ?:
                throw Exception("Unable to find configuration for widget")
    }

}