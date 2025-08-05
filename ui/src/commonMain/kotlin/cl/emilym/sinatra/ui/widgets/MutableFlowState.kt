package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T> Flow<T>.collectAsMutableStateWithLifecycle(
    initialValue: T,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    isEqual: (a: T, b: T) -> Boolean = { a, b -> a == b },
    update: suspend (T) -> Unit
): MutableState<T> {
    val state = remember { mutableStateOf(initialValue) }
    var lastFlowValue by remember { mutableStateOf<T?>(null) }

    fun CoroutineScope.task() {
        launch {
            this@collectAsMutableStateWithLifecycle.collect {
                lastFlowValue = it
                state.value = it
            }
        }

        launch {
            snapshotFlow { state.value }
                .distinctUntilChanged()
                .collect {
                    val lastFlowValue = lastFlowValue
                    if (lastFlowValue != null && !isEqual(it, lastFlowValue)) {
                        update(it)
                    }
                }
        }
    }

    LaunchedEffect(this, lifecycle, minActiveState, context) {
        lifecycle.repeatOnLifecycleCompat(minActiveState) {
            if (context == EmptyCoroutineContext) {
                task()
            } else withContext(context) {
                task()
            }
        }
    }

    return state
}

@Composable
fun <T> StateFlow<T>.collectAsMutableStateWithLifecycle(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    isEqual: (a: T, b: T) -> Boolean = { a, b -> a == b },
    update: suspend (T) -> Unit
) = collectAsMutableStateWithLifecycle(
    initialValue = this.value,
    lifecycle = lifecycle,
    minActiveState = minActiveState,
    context = context,
    isEqual = isEqual,
    update = update
)