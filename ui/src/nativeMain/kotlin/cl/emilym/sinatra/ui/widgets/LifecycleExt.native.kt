package cl.emilym.sinatra.ui.widgets

import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

actual suspend fun Lifecycle.repeatOnLifecycleCompat(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
) {
    coroutineScope {
        block()
    }
}