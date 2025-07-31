package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import cl.emilym.sinatra.data.repository.Preference
import cl.emilym.sinatra.data.repository.PreferencesRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.reflect.KProperty

interface PreferenceState<T> {
    var value: T
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}

private class MutableStatePreferenceState<T>(
    private val state: MutableState<T>,
    private val onChange: (T) -> Unit
): PreferenceState<T> {
    override var value: T
        get() = state.value
        set(value) {
            state.value = value
            onChange(value)
        }
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = state.value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        state.value = value
        onChange(value)
    }
}

@Composable
fun <T> rememberPreferenceState(
    preference: Preference<T>,
    preferencesRepository: PreferencesRepository? = null
): PreferenceState<T> {
    val preferencesRepository = preferencesRepository ?: koinInject<PreferencesRepository>()
    val unit = remember(preferencesRepository) { preferencesRepository.preference(preference) }
    val current = remember(unit) { mutableStateOf(unit.default) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(unit) {
        unit.flow.collect {
            current.value = it
        }
    }

    return MutableStatePreferenceState(current) {
        scope.launch {
            unit.save(it)
        }
    }
}