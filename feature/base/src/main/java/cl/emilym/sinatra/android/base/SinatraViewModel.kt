package cl.emilym.sinatra.android.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.emilym.compose.requeststate.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

abstract class SinatraViewModel: ViewModel() {
    fun <T> Flow<T>.state(initial: T): StateFlow<T> =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initial)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> MutableStateFlow<Flow<RequestState<T>>>.presentable(): StateFlow<RequestState<T>> {
        return flatMapLatest { it.map { it } }.state(
            RequestState.Initial()
        )
    }
}