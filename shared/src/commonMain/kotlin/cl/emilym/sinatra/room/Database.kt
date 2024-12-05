package cl.emilym.sinatra.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import cl.emilym.sinatra.room.dao.ShaDao
import cl.emilym.sinatra.room.dao.StopDao
import cl.emilym.sinatra.room.dao.StopTimetableTimeEntityDao
import cl.emilym.sinatra.room.entities.ShaEntity
import cl.emilym.sinatra.room.entities.StopEntity
import cl.emilym.sinatra.room.entities.StopTimetableTimeEntity
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
    StopTimetableTimeEntity::class
], version = 3)
@ConstructedBy(CacheDatabaseConstructor::class)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun sha(): ShaDao
    abstract fun stop(): StopDao
    abstract fun stopTimetableTime(): StopTimetableTimeEntityDao
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