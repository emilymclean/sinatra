package cl.emilym.sinatra.data.repository

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import cl.emilym.sinatra.data.models.Time24HSetting
import cl.emilym.sinatra.data.persistence.PreferencesPersistence
import org.koin.core.annotation.Factory

sealed interface Preference<T> {
    data object RequiresWheelchair: Preference<Boolean>
    data object RequiresBikes: Preference<Boolean>
    data object MaximumWalkingTime: Preference<Float>
    data object ShowAccessibilityIconsNavigation: Preference<Boolean>
    data object MetricUnits: Preference<Boolean>
    data object Use24HourUnits: Preference<Time24HSetting>
}

@Factory
class PreferencesRepository(
    preferencesPersistence: PreferencesPersistence
) {

    companion object {
        internal val REQUIRES_WHEELCHAIR_KEY = booleanPreferencesKey("ROUTER_REQUIRES_WHEELCHAIR")
        internal val ROUTER_REQUIRES_BIKE_KEY = booleanPreferencesKey("ROUTER_REQUIRES_BIKE")
        internal val ROUTER_MAXIMUM_WALKING_TIME_KEY = floatPreferencesKey("ROUTER_MAXIMUM_WALKING_TIME")
        internal val ROUTER_SHOW_ACCESSIBILITY_ICONS = booleanPreferencesKey("ROUTER_SHOW_ACCESSIBILITY_ICONS")
        internal val DISPLAY_METRIC_UNITS_KEY = booleanPreferencesKey("DISPLAY_METRIC_UNITS")
        internal val TIME_24H_KEY = stringPreferencesKey("TIME_24H")
    }

    private val requiresWheelchair: PreferencesUnit<Boolean> = SimplePreferencesUnit(
        REQUIRES_WHEELCHAIR_KEY,
        false,
        preferencesPersistence
    )

    private val requiresBikes: PreferencesUnit<Boolean> = SimplePreferencesUnit(
        ROUTER_REQUIRES_BIKE_KEY,
        false,
        preferencesPersistence
    )

    private val maximumWalkingTime: PreferencesUnit<Float> = SimplePreferencesUnit(
        ROUTER_MAXIMUM_WALKING_TIME_KEY,
        30f,
        preferencesPersistence
    )

    private val showAccessibilityIconsNavigation: PreferencesUnit<Boolean> = SimplePreferencesUnit(
        ROUTER_SHOW_ACCESSIBILITY_ICONS,
        true,
        preferencesPersistence
    )

    private val metric: PreferencesUnit<Boolean> = SimplePreferencesUnit(
        DISPLAY_METRIC_UNITS_KEY,
        true,
        preferencesPersistence
    )

    private val use24Hour: PreferencesUnit<Time24HSetting> = MappedPreferencesUnit(
        TIME_24H_KEY,
        Time24HSetting.AUTOMATIC,
        preferencesPersistence,
        { Time24HSetting.valueOf(it) },
        { it.name }
    )

    fun <T> preference(preference: Preference<T>): PreferencesUnit<T> = when (preference) {
        is Preference.MaximumWalkingTime -> maximumWalkingTime
        is Preference.MetricUnits -> metric
        is Preference.RequiresBikes -> requiresBikes
        is Preference.RequiresWheelchair -> requiresWheelchair
        is Preference.ShowAccessibilityIconsNavigation -> showAccessibilityIconsNavigation
        is Preference.Use24HourUnits -> use24Hour
    } as PreferencesUnit<T>

}
