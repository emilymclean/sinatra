package cl.emilym.sinatra.data.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import cl.emilym.sinatra.data.persistence.PreferencesPersistence
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory

interface PreferencesUnit<T> {

    val flow: Flow<T>
    suspend fun current(): T
    suspend fun save(value: T)

}

internal class SimplePreferencesUnit<T>(
    private val key: Preferences.Key<T>,
    private val default: T,
    private val persistence: PreferencesPersistence
): PreferencesUnit<T> {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val flow: Flow<T> = persistence.get(key).mapLatest { it ?: default }
    override suspend fun current(): T = persistence.get(key).first() ?: default
    override suspend fun save(value: T) { persistence.save(key, value) }
}

@Factory
class PreferencesRepository(
    preferencesPersistence: PreferencesPersistence
) {

    val requiresWheelchair: PreferencesUnit<Boolean> = SimplePreferencesUnit(
        booleanPreferencesKey("ROUTER_REQUIRES_WHEELCHAIR"),
        false,
        preferencesPersistence
    )

    val requiresBikes: PreferencesUnit<Boolean> = SimplePreferencesUnit(
        booleanPreferencesKey("ROUTER_REQUIRES_BIKE"),
        false,
        preferencesPersistence
    )

    val maximumWalkingTime: PreferencesUnit<Float> = SimplePreferencesUnit(
        floatPreferencesKey("ROUTER_MAXIMUM_WALKING_TIME"),
        30f,
        preferencesPersistence
    )

}