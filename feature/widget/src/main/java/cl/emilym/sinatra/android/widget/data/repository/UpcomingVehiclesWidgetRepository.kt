package cl.emilym.sinatra.android.widget.data.repository

import cl.emilym.sinatra.android.widget.data.models.UpcomingVehiclesWidgetConfiguration
import cl.emilym.sinatra.android.widget.data.persistence.UpcomingVehiclesWidgetPersistence
import org.koin.core.annotation.Factory

@Factory
class UpcomingVehiclesWidgetRepository(
    private val upcomingVehiclesWidgetPersistence: UpcomingVehiclesWidgetPersistence
) {

    suspend fun save(config: UpcomingVehiclesWidgetConfiguration) {
        upcomingVehiclesWidgetPersistence.save(config)
    }

    suspend fun get(appWidgetId: Int): UpcomingVehiclesWidgetConfiguration? {
        return upcomingVehiclesWidgetPersistence.get(appWidgetId)
    }

    suspend fun delete(appWidgetId: Int) {
        upcomingVehiclesWidgetPersistence.delete(appWidgetId)
    }

}