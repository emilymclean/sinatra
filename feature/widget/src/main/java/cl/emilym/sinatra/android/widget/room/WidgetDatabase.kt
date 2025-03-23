package cl.emilym.sinatra.android.widget.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.emilym.sinatra.android.widget.room.dao.UpcomingVehiclesWidgetConfigurationDao
import cl.emilym.sinatra.android.widget.room.entities.UpcomingVehiclesWidgetConfiguration
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

private const val WIDGET_DATABASE_NAME = "widget_database"

@Database(entities = [UpcomingVehiclesWidgetConfiguration::class], version = 1)
abstract class WidgetDatabase : RoomDatabase() {
    abstract fun upcomingVehiclesWidgetConfigurationDao(): UpcomingVehiclesWidgetConfigurationDao
}

@Single
fun widgetDatabase(context: Context): WidgetDatabase {
    return Room.databaseBuilder(
        context,
        WidgetDatabase::class.java,
        WIDGET_DATABASE_NAME
    )
        .fallbackToDestructiveMigration(true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

@Factory
fun upcomingVehiclesWidgetConfigurationDao(db: WidgetDatabase): UpcomingVehiclesWidgetConfigurationDao {
    return db.upcomingVehiclesWidgetConfigurationDao()
}