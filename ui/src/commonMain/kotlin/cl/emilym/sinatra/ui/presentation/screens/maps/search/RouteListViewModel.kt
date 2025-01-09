package cl.emilym.sinatra.ui.presentation.screens.maps.search

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.handle
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.domain.DisplayRoutesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class RouteListViewModel(
    val displayRoutesUseCase: DisplayRoutesUseCase
): ScreenModel {

    val routes = MutableStateFlow<RequestState<List<Route>>>(RequestState.Initial())

    init {
        retry()
    }

    fun retry() {
        screenModelScope.launch {
            routes.handle {
                displayRoutesUseCase().item
            }
        }
    }

}