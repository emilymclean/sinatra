package cl.emilym.sinatra.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import cl.emilym.sinatra.room.dao.ShaDao
import cl.emilym.sinatra.room.entities.ShaEntity
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

@Database(entities = [ShaEntity::class], version = 1)
@ConstructedBy(CacheDatabaseConstructor::class)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun sha(): ShaDao
}

@Single
fun cacheDatabase(builder: RoomDatabase.Builder<CacheDatabase>): CacheDatabase {
    return builder
        .fallbackToDestructiveMigration(true)
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

@Factory
fun shaDao(db: CacheDatabase): ShaDao {
    return db.sha()
}