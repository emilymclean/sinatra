package cl.emilym.sinatra.ui.presentation.decompose.base

import androidx.lifecycle.coroutineScope
import cl.emilym.compose.requeststate.RequestState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

interface SinatraComponent: SinatraComponentContext {

    val componentScope: CoroutineScope get() = lifecycle.coroutineScope

    fun <T> Flow<T>.state(initial: T): StateFlow<T> =
        stateIn(componentScope, SharingStarted.WhileSubscribed(5000), initial)
    fun <T> Flow<RequestState<T>>.state(): StateFlow<RequestState<T>> = state(RequestState.Initial())

}