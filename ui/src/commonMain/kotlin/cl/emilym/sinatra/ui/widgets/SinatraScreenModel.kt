package cl.emilym.sinatra.ui.widgets

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cl.emilym.compose.requeststate.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

interface SinatraScreenModel: ScreenModel {
    fun <T> Flow<T>.state(initial: T): StateFlow<T> =
        stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initial)
    fun <T> Flow<RequestState<T>>.state(): StateFlow<RequestState<T>> = state(RequestState.Initial())

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> MutableStateFlow<Flow<RequestState<T>>>.presentable(): StateFlow<RequestState<T>> {
        return flatMapLatest { it.map { it } }.state(
            RequestState.Initial()
        )
    }
}