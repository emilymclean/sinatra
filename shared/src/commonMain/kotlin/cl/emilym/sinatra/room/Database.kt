package cl.emilym.sinatra.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import cl.emilym.sinatra.room.dao.RouteDao
import cl.emilym.sinatra.room.dao.RouteServiceEntityDao
import cl.emilym.sinatra.room.dao.RouteTripInformationEntityDao
import cl.emilym.sinatra.room.dao.RouteTripStopEntityDao
import cl.emilym.sinatra.room.dao.ShaDao
import cl.emilym.sinatra.room.dao.StopDao
import cl.emilym.sinatra.room.dao.StopTimetableTimeEntityDao
import cl.emilym.sinatra.room.dao.TimetableServiceExceptionEntityDao
import cl.emilym.sinatra.room.dao.TimetableServiceRegularEntityDao
import cl.emilym.sinatra.room.entities.RouteEntity
import cl.emilym.sinatra.room.entities.RouteServiceEntity
import cl.emilym.sinatra.room.entities.RouteTripInformationEntity
import cl.emilym.sinatra.room.entities.RouteTripStopEntity
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

const val cacheDatabaseName = "cache"

expect val databaseBuilderModule: Module

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object CacheDatabaseConstructor : RoomDatabaseConstructor<CacheDatabase> {
    override fun initialize(): CacheDatabase
}

@Database(entities = [
    ShaEntity::class,
    StopEntity::class,
    StopTimetableTimeEntity::class,
    RouteEntity::class,
    RouteServiceEntity::class,
    RouteTripInformationEntity::class,
    RouteTripStopEntity::class,
    TimetableServiceRegularEntity::class,
    TimetableServiceExceptionEntity::class,
], version = 7)
@ConstructedBy(CacheDatabaseConstructor::class)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun sha(): ShaDao
    abstract fun stop(): StopDao
    abstract fun stopTimetableTime(): StopTimetableTimeEntityDao
    abstract fun route(): RouteDao
    abstract fun routeService(): RouteServiceEntityDao
    abstract fun timetableServiceRegularDao(): TimetableServiceRegularEntityDao
    abstract fun timetableServiceExceptionDao(): TimetableServiceExceptionEntityDao
    abstract fun routeTripInformationDao(): RouteTripInformationEntityDao
    abstract fun routeTripStopDao(): RouteTripStopEntityDao
}

@Single
fun cacheDatabase(builder: RoomDatabase.Builder<CacheDatabase>): CacheDatabase {
    return builder
        .fallbackToDestructiveMigration(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

@Factory
fun shaDao(db: CacheDatabase): ShaDao {
    return db.sha()
}

@Factory
fun stopDao(db: CacheDatabase): StopDao {
    return db.stop()
}

@Factory
fun stopTimetableTimeDao(db: CacheDatabase): StopTimetableTimeEntityDao {
    return db.stopTimetableTime()
}

@Factory
fun routeDao(db: CacheDatabase): RouteDao {
    return db.route()
}

@Factory
fun routeServiceDao(db: CacheDatabase): RouteServiceEntityDao {
    return db.routeService()
}

@Factory
fun timetableServiceRegularDao(db: CacheDatabase): TimetableServiceRegularEntityDao {
    return db.timetableServiceRegularDao()
}

@Factory
fun timetableServiceExceptionDao(db: CacheDatabase): TimetableServiceExceptionEntityDao {
    return db.timetableServiceExceptionDao()
}

@Factory
fun routeTripInformationDao(db: CacheDatabase): RouteTripInformationEntityDao {
    return db.routeTripInformationDao()
}

@Factory
fun routeTripStopDao(db: CacheDatabase): RouteTripStopEntityDao {
    return db.routeTripStopDao()
}