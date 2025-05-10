package cl.emilym.sinatra.data.persistence

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import cl.emilym.sinatra.room.APP_DATASTORE_QUALIFIER
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Qualifier

@Factory
class AppPersistence(
    @Qualifier(name = APP_DATASTORE_QUALIFIER) private val appDatastore: DataStore<Preferences>
) {

    companion object {
        private val LAST_APP_CODE_KEY = intPreferencesKey("LAST_APP_CODE")
    }

    suspend fun lastAppCode(): Int {
        return appDatastore.data.mapLatest {
            it[LAST_APP_CODE_KEY]
        }.first() ?: -1
    }

    suspend fun setLastAppCode(value: Int) {
        appDatastore.edit {
            it[LAST_APP_CODE_KEY] = value
        }
    }

}