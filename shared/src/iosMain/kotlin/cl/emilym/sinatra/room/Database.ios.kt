package cl.emilym.sinatra.room

import androidx.room.Room
import androidx.room.RoomDatabase
import cl.emilym.sinatra.data.persistence.AppleCacheFileWriter
import cl.emilym.sinatra.data.persistence.CacheFileWriter
import cl.emilym.sinatra.documentDirectory
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module

inline fun <reified T: RoomDatabase> createDatabaseBuilder(name: String): RoomDatabase.Builder<T> {
    val dbFilePath = documentDirectory() + "/${name}.db"
    return Room.databaseBuilder<T>(
        name = dbFilePath
    )
}

actual val databaseBuilderModule: Module = module {
    single { createDatabaseBuilder<AppDatabase>(appDatabaseName) }
    factory { AppleCacheFileWriter() } binds arrayOf(CacheFileWriter::class)
}