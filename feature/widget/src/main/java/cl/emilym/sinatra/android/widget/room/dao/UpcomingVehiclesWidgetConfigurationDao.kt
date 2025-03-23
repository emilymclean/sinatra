package cl.emilym.sinatra.android.widget.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.android.widget.room.entities.UpcomingVehiclesWidgetConfiguration

interface UpcomingVehiclesWidgetConfigurationDao {

    @Insert
    suspend fun insert(config: UpcomingVehiclesWidgetConfiguration)

    @Delete
    suspend fun delete(config: UpcomingVehiclesWidgetConfiguration)

    @Query("DELETE FROM UpcomingVehiclesWidgetConfiguration WHERE widgetId = :widgetId")
    suspend fun delete(widgetId: Int)

    @Query("SELECT * FROM UpcomingVehiclesWidgetConfiguration WHERE widgetId = :widgetId")
    suspend fun get(widgetId: Int): UpcomingVehiclesWidgetConfiguration?

}