package cl.emilym.sinatra.ui.presentation.screens.maps.search

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.NavigationObject
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.domain.DisplayRoutesUseCase
import cl.emilym.sinatra.domain.smart.NewServiceUpdateUseCase
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.retryIfNeeded
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication.Companion.init
import org.koin.core.annotation.Factory

sealed interface BrowseOption {
    data class QuickNavigate(
        val item: NavigationObject
    ): BrowseOption
    data class AddSpecialFavourite(
        val type: SpecialFavouriteType
    ): BrowseOption
    data class NewServiceUpdate(
        val serviceAlert: ServiceAlert
    ): BrowseOption
    data class Row(
        val option: List<BrowseOption>
    ): BrowseOption
}

@Factory
class BrowseViewModel(
    private val displayRoutesUseCase: DisplayRoutesUseCase,
    private val newServiceUpdateUseCase: NewServiceUpdateUseCase,
): SinatraScreenModel {

    private val _routes = requestStateFlow { displayRoutesUseCase().item }
    val routes = _routes.state(RequestState.Initial())

    private val newServices = flatRequestStateFlow { newServiceUpdateUseCase() }
    private val dummyFlow = flow { emit(Unit) }

    val options: StateFlow<List<BrowseOption>> = combine(
        newServices,
        dummyFlow
    ) { newServices, _ ->
        Napier.d("$newServices")
        listOfNotNull<BrowseOption>(
            newServices.unwrap().nullIfEmpty()?.let { BrowseOption.NewServiceUpdate(it.first()) }
        )
    }.state(listOf())

    init {
        retry()
    }

    fun retry() {
        screenModelScope.launch {
            _routes.retryIfNeeded()
            newServices.retryIfNeeded()
        }
    }

}