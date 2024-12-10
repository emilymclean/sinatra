package cl.emilym.sinatra.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

inline fun <reified T: RoomDatabase> createDatabaseBuilder(
    context: Context, name: String
): RoomDatabase.Builder<T> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("${name}.db")
    return Room.databaseBuilder<T>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

actual val databaseBuilderModule: Module = module {
    single { createDatabaseBuilder<CacheDatabase>(androidContext(), cacheDatabaseName) }
}