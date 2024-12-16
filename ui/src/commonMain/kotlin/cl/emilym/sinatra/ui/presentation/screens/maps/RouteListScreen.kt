package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.handle
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.domain.DisplayRoutesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class RouteListViewModel(
    val displayRoutesUseCase: DisplayRoutesUseCase
): ViewModel() {

    val routes = MutableStateFlow<RequestState<List<Route>>>(RequestState.Initial())

    init {
        retry()
    }

    fun retry() {
        viewModelScope.launch {
            routes.handle {
                displayRoutesUseCase().item
            }
        }
    }

}