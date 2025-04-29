package cl.emilym.sinatra.room

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import cl.emilym.sinatra.room.dao.FavouriteDao
import cl.emilym.sinatra.room.dao.PlaceDao
import cl.emilym.sinatra.room.dao.RecentVisitDao
import cl.emilym.sinatra.room.dao.RouteDao
import cl.emilym.sinatra.room.dao.RouteServiceEntityDao
import cl.emilym.sinatra.room.dao.RouteTripInformationEntityDao
import cl.emilym.sinatra.room.dao.RouteTripStopEntityDao
import cl.emilym.sinatra.room.dao.ServiceAlertDao
import cl.emilym.sinatra.room.dao.ShaDao
import cl.emilym.sinatra.room.dao.StopDao
import cl.emilym.sinatra.room.dao.StopTimetableTimeEntityDao
import cl.emilym.sinatra.room.dao.TimetableServiceExceptionEntityDao
import cl.emilym.sinatra.room.dao.TimetableServiceRegularEntityDao
import cl.emilym.sinatra.room.entities.FavouriteEntity
import cl.emilym.sinatra.room.entities.PlaceEntity
import cl.emilym.sinatra.room.entities.RecentVisitEntity
import cl.emilym.sinatra.room.entities.RouteEntity
import cl.emilym.sinatra.room.entities.RouteServiceEntity
import cl.emilym.sinatra.room.entities.RouteTripInformationEntity
import cl.emilym.sinatra.room.entities.RouteTripStopEntity
import cl.emilym.sinatra.room.entities.ServiceAlertEntity
import cl.emilym.sinatra.room.entities.ShaEntity
import cl.emilym.sinatra.room.entities.StopEntity
import cl.emilym.sinatra.room.entities.StopTimetableTimeEntity
import cl.emilym.sinatra.room.entities.TimetableServiceExceptionEntity
import cl.emilym.sinatra.room.entities.TimetableServiceRegularEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single
import org.koin.core.module.Module

const val appDatabaseName = "app"

expect val databaseBuilderModule: Module

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

@Database(entities =
    [
        ShaEntity::class,
        StopEntity::class,
        StopTimetableTimeEntity::class,
        RouteEntity::class,
        RouteServiceEntity::class,
        RouteTripInformationEntity::class,
        RouteTripStopEntity::class,
        TimetableServiceRegularEntity::class,
        TimetableServiceExceptionEntity::class,
        FavouriteEntity::class,
        RecentVisitEntity::class,
        PlaceEntity::class,
        ServiceAlertEntity::class
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
    ],
    exportSchema = true,
    version = 6
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun sha(): ShaDao
    abstract fun stop(): StopDao
    abstract fun stopTimetableTime(): StopTimetableTimeEntityDao
    abstract fun route(): RouteDao
    abstract fun routeService(): RouteServiceEntityDao
    abstract fun timetableServiceRegularDao(): TimetableServiceRegularEntityDao
    abstract fun timetableServiceExceptionDao(): TimetableServiceExceptionEntityDao
    abstract fun routeTripInformationDao(): RouteTripInformationEntityDao
    abstract fun routeTripStopDao(): RouteTripStopEntityDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun recentVisitDao(): RecentVisitDao
    abstract fun placeDao(): PlaceDao
    abstract fun serviceAlertDao(): ServiceAlertDao
}

@Single
fun appDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

@Factory
fun shaDao(db: AppDatabase): ShaDao {
    return db.sha()
}

@Factory
fun stopDao(db: AppDatabase): StopDao {
    return db.stop()
}

@Factory
fun stopTimetableTimeDao(db: AppDatabase): StopTimetableTimeEntityDao {
    return db.stopTimetableTime()
}

@Factory
fun routeDao(db: AppDatabase): RouteDao {
    return db.route()
}

@Factory
fun routeServiceDao(db: AppDatabase): RouteServiceEntityDao {
    return db.routeService()
}

@Factory
fun timetableServiceRegularDao(db: AppDatabase): TimetableServiceRegularEntityDao {
    return db.timetableServiceRegularDao()
}

@Factory
fun timetableServiceExceptionDao(db: AppDatabase): TimetableServiceExceptionEntityDao {
    return db.timetableServiceExceptionDao()
}

@Factory
fun routeTripInformationDao(db: AppDatabase): RouteTripInformationEntityDao {
    return db.routeTripInformationDao()
}

@Factory
fun routeTripStopDao(db: AppDatabase): RouteTripStopEntityDao {
    return db.routeTripStopDao()
}

@Factory
fun favouriteDao(db: AppDatabase): FavouriteDao {
    return db.favouriteDao()
}

@Factory
fun recentVisitDao(db: AppDatabase): RecentVisitDao {
    return db.recentVisitDao()
}

@Factory
fun placeDao(db: AppDatabase): PlaceDao {
    return db.placeDao()
}

@Factory
fun serviceAlertDao(db: AppDatabase): ServiceAlertDao {
    return db.serviceAlertDao()
}