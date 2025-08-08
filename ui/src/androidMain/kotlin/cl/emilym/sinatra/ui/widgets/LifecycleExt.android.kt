package cl.emilym.sinatra.ui.widgets

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope

actual suspend fun Lifecycle.repeatOnLifecycleCompat(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
) {
    repeatOnLifecycle(state, block)
}