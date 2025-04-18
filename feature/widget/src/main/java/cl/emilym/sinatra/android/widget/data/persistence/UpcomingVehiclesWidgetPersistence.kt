package cl.emilym.sinatra.android.widget.data.persistence

import cl.emilym.sinatra.android.widget.data.models.UpcomingVehiclesWidgetConfiguration
import cl.emilym.sinatra.android.widget.data.room.dao.UpcomingVehiclesWidgetConfigurationDao
import cl.emilym.sinatra.android.widget.data.room.entities.UpcomingVehiclesWidgetConfigurationEntity
import org.koin.core.annotation.Factory

@Factory
class UpcomingVehiclesWidgetPersistence(
    private val upcomingVehiclesWidgetConfigurationDao: UpcomingVehiclesWidgetConfigurationDao
) {

    suspend fun save(config: UpcomingVehiclesWidgetConfiguration) {
        upcomingVehiclesWidgetConfigurationDao.insert(
            UpcomingVehiclesWidgetConfigurationEntity.fromModel(config)
        )
    }

    suspend fun get(appWidgetId: Int): UpcomingVehiclesWidgetConfiguration? {
        return upcomingVehiclesWidgetConfigurationDao.get(
            appWidgetId
        )?.toModel()
    }

    suspend fun delete(appWidgetId: Int) {
        upcomingVehiclesWidgetConfigurationDao.delete(appWidgetId)
    }

}