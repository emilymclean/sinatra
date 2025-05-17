package cl.emilym.sinatra.data.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import cl.emilym.sinatra.data.models.Time24HSetting
import cl.emilym.sinatra.data.persistence.PreferencesPersistence
import cl.emilym.sinatra.data.repository.PreferencesRepository.Companion.DISPLAY_METRIC_UNITS_KEY
import cl.emilym.sinatra.data.repository.PreferencesRepository.Companion.DISPLAY_METRIC_UNITS_QUALIFIER
import cl.emilym.sinatra.data.repository.PreferencesRepository.Companion.TIME_24H_KEY
import cl.emilym.sinatra.data.repository.PreferencesRepository.Companion.TIME_24H_QUALIFIER
import cl.emilym.sinatra.e
import cl.emilym.sinatra.nullIfThrows
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Qualifier

interface PreferencesUnit<T> {

    val flow: Flow<T>
    suspend fun current(): T
    suspend fun save(value: T)

}

interface StatefulPreferencesUnit<T>: PreferencesUnit<T> {
    override val flow: StateFlow<T>
}

internal abstract class BasePreferencesUnit<T>: PreferencesUnit<T> {
    abstract val default: T
}

internal abstract class MappablePreferencesUnit<I,O>: BasePreferencesUnit<O>() {
    protected abstract val key: Preferences.Key<I>
    protected abstract val persistence: PreferencesPersistence

    abstract fun fromPersistence(i: I): O
    abstract fun toPersistence(o: O): I

    @OptIn(ExperimentalCoroutinesApi::class)
    override val flow: Flow<O>
        get() = persistence.get(key).catch {
            Napier.e(it)
            emit(null)
        }.mapLatest { it?.let { nullIfThrows { fromPersistence(it) } } ?: default }
    override suspend fun current(): O =
        persistence.get(key).catch {
            Napier.e(it)
            emit(null)
        }.first()?.let { nullIfThrows { fromPersistence(it) } } ?: default
    override suspend fun save(value: O) {
        try {
            persistence.save(key, toPersistence(value))
        } catch(e: Throwable) {
            Napier.e(e)
        }
    }
}

internal class SimplePreferencesUnit<T>(
    override val key: Preferences.Key<T>,
    override val default: T,
    override val persistence: PreferencesPersistence
): MappablePreferencesUnit<T,T>() {
    override fun fromPersistence(i: T): T = i
    override fun toPersistence(o: T): T = o
}

internal class MappedPreferencesUnit<I,O>(
    override val key: Preferences.Key<I>,
    override val default: O,
    override val persistence: PreferencesPersistence,
    private val fromPersistence: (I) -> O,
    private val toPersistence: (O) -> I
): MappablePreferencesUnit<I,O>() {

    override fun fromPersistence(i: I): O = fromPersistence.invoke(i)
    override fun toPersistence(o: O): I = toPersistence.invoke(o)
}

internal class WrapperStatefulPreferencesUnit<T>(
    private val delegate: BasePreferencesUnit<T>,
    private val scope: CoroutineScope
): StatefulPreferencesUnit<T> {
    override suspend fun current(): T = delegate.current()

    override val flow: StateFlow<T> = delegate.flow.stateIn(
        scope, SharingStarted.WhileSubscribed(5000), delegate.default
    )

    override suspend fun save(value: T) = delegate.save(value)
}

internal class WrapperStatefulNullablePreferencesUnit<T>(
    private val delegate: PreferencesUnit<T>,
    private val scope: CoroutineScope
): StatefulPreferencesUnit<T?> {
    override suspend fun current(): T? = delegate.current()

    override val flow: StateFlow<T?> = delegate.flow.stateIn(
        scope, SharingStarted.WhileSubscribed(5000), null
    )

    override suspend fun save(value: T?) { value?.let { delegate.save(value) } }
}

internal class WrapperMappedPreferencesUnit<I,O>(
    private val delegate: PreferencesUnit<I>,
    override val default: O,
    private val fromPersistence: (I) -> O,
    private val toPersistence: (O) -> I
): BasePreferencesUnit<O>() {

    override val flow: Flow<O> = delegate.flow.mapLatest { fromPersistence(it) }

    override suspend fun current(): O = fromPersistence(delegate.current())

    override suspend fun save(value: O) = delegate.save(toPersistence(value))
}

fun <T> PreferencesUnit<T>.state(scope: CoroutineScope): StatefulPreferencesUnit<T> {
    return WrapperStatefulPreferencesUnit(
        (this as BasePreferencesUnit<T>),
        scope
    )
}

fun <I,O> PreferencesUnit<I>.map(
    default: O,
    from: (I) -> O,
    to: (O) -> I
): PreferencesUnit<O> {
    return WrapperMappedPreferencesUnit(
        this, default, from, to
    )
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

        const val DISPLAY_METRIC_UNITS_QUALIFIER = "DISPLAY_METRIC_UNITS"
        const val TIME_24H_QUALIFIER = "TIME_24H"
    }

    val requiresWheelchair: PreferencesUnit<Boolean> = SimplePreferencesUnit(
        REQUIRES_WHEELCHAIR_KEY,
        false,
        preferencesPersistence
    )

    val requiresBikes: PreferencesUnit<Boolean> = SimplePreferencesUnit(
        ROUTER_REQUIRES_BIKE_KEY,
        false,
        preferencesPersistence
    )

    val maximumWalkingTime: PreferencesUnit<Float> = SimplePreferencesUnit(
        ROUTER_MAXIMUM_WALKING_TIME_KEY,
        30f,
        preferencesPersistence
    )

    val showAccessibilityIconsNavigation: PreferencesUnit<Boolean> = SimplePreferencesUnit(
        ROUTER_SHOW_ACCESSIBILITY_ICONS,
        true,
        preferencesPersistence
    )

    val metric: PreferencesUnit<Boolean> = SimplePreferencesUnit(
        DISPLAY_METRIC_UNITS_KEY,
        true,
        preferencesPersistence
    )

    val use24Hour: PreferencesUnit<Time24HSetting> = MappedPreferencesUnit(
        TIME_24H_KEY,
        Time24HSetting.AUTOMATIC,
        preferencesPersistence,
        { Time24HSetting.valueOf(it) },
        { it.name }
    )

}

@Factory
@Qualifier(name = DISPLAY_METRIC_UNITS_QUALIFIER)
fun metric(
    preferencesPersistence: PreferencesPersistence
): PreferencesUnit<Boolean> {
    return SimplePreferencesUnit(
        DISPLAY_METRIC_UNITS_KEY,
        true,
        preferencesPersistence
    )
}

@Factory
@Qualifier(name = TIME_24H_QUALIFIER)
fun time24H(
    preferencesPersistence: PreferencesPersistence
): PreferencesUnit<Time24HSetting> {
    return MappedPreferencesUnit(
        TIME_24H_KEY,
        Time24HSetting.AUTOMATIC,
        preferencesPersistence,
        { Time24HSetting.valueOf(it) },
        { it.name }
    )
}
