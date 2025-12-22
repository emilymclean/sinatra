// From https://github.com/arkivanov/Decompose/discussions/816
package cl.emilym.sinatra.ui.presentation.decompose.base

import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun <T : Any> Value<T>.asStateFlow(): StateFlow<T> =
    ValueStateFlow(this)

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
private class ValueStateFlow<out T : Any>(
    private val v: Value<T>,
) : StateFlow<T> {

    override val value: T get() = v.value
    override val replayCache: List<T> get() = listOf(v.value)

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        val stateFlow = MutableStateFlow(v.value)
        val cancellation = v.subscribe { stateFlow.value = it }

        try {
            stateFlow.collect(collector)
        } finally {
            cancellation.cancel()
        }
    }
}