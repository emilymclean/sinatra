package cl.emilym.sinatra.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.emilym.sinatra.data.persistence.AndroidCacheFileWriter
import cl.emilym.sinatra.data.persistence.CacheFileWriter
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.binds
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
    single { createDatabaseBuilder<AppDatabase>(androidContext(), appDatabaseName) }
    factory { AndroidCacheFileWriter(androidContext()) } binds arrayOf(CacheFileWriter::class)
    single(StringQualifier(PREFERENCES_DATASTORE_QUALIFIER)) { createDataStore(androidContext(), PREFERENCES_DATASTORE_NAME) }
}