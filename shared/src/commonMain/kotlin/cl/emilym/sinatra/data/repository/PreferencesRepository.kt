package cl.emilym.sinatra.data.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import cl.emilym.sinatra.data.persistence.PreferencesPersistence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.Factory

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

    abstract fun toPersistence(i: I): O
    abstract fun fromPersistence(o: O): I

    @OptIn(ExperimentalCoroutinesApi::class)
    override val flow: Flow<O>
        get() = persistence.get(key).mapLatest { it?.let { toPersistence(it) } ?: default }
    override suspend fun current(): O =
        persistence.get(key).first()?.let { toPersistence(it) } ?: default
    override suspend fun save(value: O) { persistence.save(key, fromPersistence(value)) }
}

internal class SimplePreferencesUnit<T>(
    override val key: Preferences.Key<T>,
    override val default: T,
    override val persistence: PreferencesPersistence
): MappablePreferencesUnit<T,T>() {
    override fun toPersistence(i: T): T = i
    override fun fromPersistence(o: T): T = o
}

internal class MappedPreferencesUnit<I,O>(
    override val key: Preferences.Key<I>,
    override val default: O,
    override val persistence: PreferencesPersistence,
    private val toPersistence: (I) -> O,
    private val fromPersistence: (O) -> I
): MappablePreferencesUnit<I,O>() {

    override fun toPersistence(i: I): O = toPersistence.invoke(i)
    override fun fromPersistence(o: O): I = fromPersistence.invoke(o)
}

internal class DefaultStatefulPreferencesUnit<T>(
    private val delegate: BasePreferencesUnit<T>,
    private val scope: CoroutineScope
): StatefulPreferencesUnit<T> {
    override suspend fun current(): T = delegate.current()

    override val flow: StateFlow<T> = delegate.flow.stateIn(
        scope, SharingStarted.WhileSubscribed(5000), delegate.default
    )

    override suspend fun save(value: T) = delegate.save(value)
}

fun <T> PreferencesUnit<T>.state(scope: CoroutineScope): StatefulPreferencesUnit<T> {
    return DefaultStatefulPreferencesUnit(
        (this as BasePreferencesUnit<T>),
        scope
    )
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