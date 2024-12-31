package cl.emilym.sinatra.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import cl.emilym.sinatra.datastore.SETTINGS_QUALIFIER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named

@Factory
class SettingsPersistence(
    @Named(SETTINGS_QUALIFIER)
    val dataStore: DataStore<Preferences>
) {

    inline fun <reified T> keyForType(key: String): Preferences.Key<T> {
        return when(T::class) {
            String::class -> stringPreferencesKey(key)
            Int::class -> intPreferencesKey(key)
            Long::class -> longPreferencesKey(key)
            Float::class -> floatPreferencesKey(key)
            Double::class -> doublePreferencesKey(key)
            Boolean::class -> booleanPreferencesKey(key)
            ByteArray::class -> byteArrayPreferencesKey(key)
            else -> throw Exception("Invalid type ${T::class} for preferences datastore")
        } as Preferences.Key<T>
    }

    inline fun <reified T> read(key: String): Flow<T?> {
        return dataStore.data.map { it[keyForType(key)] }
    }

    suspend inline fun <reified T> readSingle(key: String): T? {
        return read<T>(key).first()
    }

    suspend inline fun <reified T> save(key: String, value: T?) {
        dataStore.edit {
            it[keyForType(key)] = value
        }
    }

}