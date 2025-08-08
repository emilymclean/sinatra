package cl.emilym.sinatra.ui.widgets

import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface Mutator<T,M> {
    fun apply(current: T, mutation: M): T
    suspend fun commit(mutation: M)
}

interface MutatorState<T,M>: State<T> {
    fun mutate(mutation: M)
}

private data class Value<T>(
    val value: T,
    val timestamp: Instant
) {

    companion object {
        fun <T> create(value: T): Value<T> {
            return Value(
                value,
                Clock.System.now()
            )
        }
    }

}

private abstract class SingleMutatorState<T,M>(
    initialValue: T
): MutatorState<T,M> {
    abstract val mutator: Mutator<T,M>
    abstract val mutations: Channel<M>

    var currentValue: T
        get() = current.value
        set(value) {
            current = Value.create(value)
            if (mutations.isEmpty) { working = Value.create(value) }
        }

    private var working: Value<T> by mutableStateOf(Value.create(initialValue))
    private var current: Value<T> by mutableStateOf(Value.create(initialValue))

    override val value: T by derivedStateOf {
        if (mutations.isEmpty) {
            listOf(working, current).maxBy { it.timestamp }
        } else {
            working
        }.value
    }

    @MainThread
    override fun mutate(mutation: M) {
        working = Value.create(mutator.apply(working.value, mutation))
        mutations.trySend(mutation)
    }
}

private class DefaultSingleMutatorState<T,M>(
    initialValue: T,
    override val mutator: Mutator<T, M>,
    override val mutations: Channel<M>
): SingleMutatorState<T,M>(initialValue)

@Composable
fun <T,M> rememberMutatorState(
    value: T,
    mutator: Mutator<T,M>,
    context: CoroutineContext = Dispatchers.IO
): MutatorState<T,M> {
    val mutations = remember { Channel<M>(Channel.UNLIMITED) }
    val mutatorState = remember(mutator) {
        DefaultSingleMutatorState(value, mutator, mutations)
    }

    LaunchedEffect(value) {
        mutatorState.currentValue = value
    }

    LaunchedEffect(mutations) {
        withContext(context) {
            mutations.consumeAsFlow().collect {
                mutator.commit(it)
            }
        }
    }

    return mutatorState
}

@Composable
fun <T,M> StateFlow<T>.collectAsMutatorStateWithLifecycle(
    mutator: Mutator<T,M>,
    context: CoroutineContext = EmptyCoroutineContext
): MutatorState<T,M> {
    val flowState by collectAsStateWithLifecycle(context)
    return rememberMutatorState(flowState, mutator)
}
