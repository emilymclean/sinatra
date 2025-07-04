package cl.emilym.sinatra.ui.savedstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import cl.emilym.kmp.serializable.Serializable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface ScreenModelState {

    fun <T> get(key: String): T?
    fun <T> set(key: String, value: T?)

    fun <T> getStateFlow(key: String, initialValue: T): StateFlow<T>

}

private class DefaultScreenModelState(
    initial: Map<String, Any?>
): ScreenModelState {

    val values = mutableMapOf<String, MutableStateFlow<Any?>>()

    init {
        initial.forEach { (key, value) ->
            values[key] = MutableStateFlow(value)
        }
    }

    override fun <T> get(key: String): T? {
        return values[key]?.value as T?
    }

    override fun <T> set(key: String, value: T?) {
        getOrPut(key, value).value = value
    }

    override fun <T> getStateFlow(key: String, initialValue: T): StateFlow<T> {
        return getOrPut(key, initialValue).asStateFlow() as StateFlow<T>
    }

    private fun <T> getOrPut(key: String, value: T?): MutableStateFlow<T?> {
        return values.getOrPut(key) {
            MutableStateFlow(value) as MutableStateFlow<Any?>
        } as MutableStateFlow<T?>
    }
}

private data class ScreenModelStateSaved(
    val values: Map<String, Any?>
): Serializable

private val DefaultScreenModelStateSaver: Saver<DefaultScreenModelState, ScreenModelStateSaved> = Saver(
    save = { ScreenModelStateSaved(it.values.mapValues { (key, value) -> value.value }) },
    restore = { DefaultScreenModelState(it.values) }
)

@Composable
fun rememberScreenModelState(key: String? = null): ScreenModelState {
    return rememberSaveable(key = key, saver = DefaultScreenModelStateSaver) {
        DefaultScreenModelState(mapOf())
    }
}