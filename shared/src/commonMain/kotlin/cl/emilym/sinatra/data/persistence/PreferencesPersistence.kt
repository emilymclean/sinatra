package cl.emilym.sinatra.data.persistence

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import cl.emilym.sinatra.room.PREFERENCES_DATASTORE_QUALIFIER
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Qualifier

@Factory
class PreferencesPersistence(
    @Qualifier(name = PREFERENCES_DATASTORE_QUALIFIER) private val datastore: DataStore<Preferences>
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> get(key: Preferences.Key<T>): Flow<T?> {
        return datastore.data.mapLatest {
            it[key]
        }
    }

    suspend fun <T> save(key: Preferences.Key<T>, value: T?) {
        datastore.edit { prefs ->
            value?.let {
                prefs[key] = it
            } ?: let {
                prefs -= key
            }
        }
    }

}