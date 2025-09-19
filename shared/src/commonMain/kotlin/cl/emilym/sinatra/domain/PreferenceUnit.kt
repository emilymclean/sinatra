package cl.emilym.sinatra.data.repository

import androidx.datastore.preferences.core.Preferences
import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.persistence.PreferencesPersistence
import cl.emilym.sinatra.e
import cl.emilym.sinatra.nullIfThrows
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest

interface PreferencesUnit<T> {

    val flow: Flow<T>
    val default: T
    suspend fun current(): T
    suspend fun save(value: T)

}

internal abstract class MappablePreferencesUnit<I,O>: PreferencesUnit<O> {
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

internal class WrapperMappedPreferencesUnit<I,O>(
    private val delegate: PreferencesUnit<I>,
    override val default: O,
    private val fromPersistence: (I) -> O,
    private val toPersistence: (O) -> I
): PreferencesUnit<O> {

    override val flow: Flow<O> = delegate.flow.mapLatest { fromPersistence(it) }

    override suspend fun current(): O = fromPersistence(delegate.current())

    override suspend fun save(value: O) = delegate.save(toPersistence(value))
}

internal class FeatureFlaggedPreferencesUnit(
    private val delegate: PreferencesUnit<Boolean>,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val featureFlag: FeatureFlag
): PreferencesUnit<Boolean> {
    override val flow: Flow<Boolean>
        get() = delegate.flow.mapLatest {
            when (remoteConfigRepository.feature(featureFlag)) {
                true -> it
                else -> false
            }
        }
    override val default: Boolean
        get() = delegate.default

    override suspend fun current(): Boolean {
        return when (remoteConfigRepository.feature(featureFlag)) {
            true -> delegate.current()
            else -> false
        }
    }

    override suspend fun save(value: Boolean) {
        delegate.save(value)
    }
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