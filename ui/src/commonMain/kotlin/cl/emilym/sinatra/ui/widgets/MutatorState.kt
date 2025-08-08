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
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface Mutator<T,M> {
    fun apply(current: T, mutation: M): T
    fun commit(current: T, mutations: List<M>)
}

interface MutatorState<T,M>: State<T> {
    var locked: Boolean
    fun mutate(mutation: M)
}

private abstract class SingleMutatorState<T,M>: MutatorState<T,M> {
    private val mutations = mutableStateListOf<M>()
    override var locked: Boolean = false
        set(value) {
            field = value
            commitIfNeeded()
        }

    abstract val mutator: Mutator<T,M>

    abstract var currentValue: T
    override val value: T by derivedStateOf {
        val size = mutations.size
        mutations.fold(currentValue) { acc, m -> mutator.apply(acc, m) }
    }

    @MainThread
    override fun mutate(mutation: M) {
        mutations.add(mutation)
        commitIfNeeded()
    }

    fun clearMutations() {
        mutations.clear()
    }

    private fun commitIfNeeded() {
        if (locked) return
        if (mutations.isEmpty()) return
        mutator.commit(currentValue, mutations.toList())
    }
}

private class DefaultSingleMutatorState<T,M>(
    initialValue: T,
    override val mutator: Mutator<T, M>
): SingleMutatorState<T,M>() {

    override var currentValue: T by mutableStateOf(initialValue)

}

@Composable
fun <T,M> rememberMutatorState(
    value: T,
    mutator: Mutator<T,M>
): MutatorState<T,M> {
    val mutatorState = remember(mutator) {
        DefaultSingleMutatorState(value, mutator)
    }

    LaunchedEffect(value) {
        mutatorState.currentValue = value
        mutatorState.clearMutations()
    }

    return mutatorState
}

@Composable
fun <T,M> rememberMutator(
    apply: (current: T, mutation: M) -> T,
    vararg keys: Any,
    commit: (current: T, mutations: List<M>) -> Unit,
): Mutator<T,M> {
    return remember(keys) { object : Mutator<T, M> {
        override fun apply(current: T, mutation: M) = apply(current, mutation)
        override fun commit(current: T, mutations: List<M>) = commit(current, mutations)
    } }
}

@Composable
fun <T,M> StateFlow<T>.collectAsMutatorStateWithLifecycle(
    mutator: Mutator<T,M>,
    context: CoroutineContext = EmptyCoroutineContext
): MutatorState<T,M> {
    val flowState by collectAsStateWithLifecycle(context)
    return rememberMutatorState(flowState, mutator)
}
