package cl.emilym.sinatra.android.widget.data.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.android.widget.data.room.entities.UpcomingVehiclesWidgetConfigurationEntity

interface UpcomingVehiclesWidgetConfigurationDao {

    @Insert
    suspend fun insert(config: UpcomingVehiclesWidgetConfigurationEntity)

    @Delete
    suspend fun delete(config: UpcomingVehiclesWidgetConfigurationEntity)

    @Query("DELETE FROM UpcomingVehiclesWidgetConfigurationEntity WHERE widgetId = :widgetId")
    suspend fun delete(widgetId: Int)

    @Query("SELECT * FROM UpcomingVehiclesWidgetConfigurationEntity WHERE widgetId = :widgetId")
    suspend fun get(widgetId: Int): UpcomingVehiclesWidgetConfigurationEntity?

}