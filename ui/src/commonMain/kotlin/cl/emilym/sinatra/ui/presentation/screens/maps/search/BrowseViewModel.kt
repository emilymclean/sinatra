package cl.emilym.sinatra.ui.presentation.screens.maps.search

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.NavigationObject
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.domain.DisplayRoutesUseCase
import cl.emilym.sinatra.domain.smart.NewServiceUpdateUseCase
import cl.emilym.sinatra.domain.smart.QuickNavigateUseCase
import cl.emilym.sinatra.domain.smart.QuickNavigation
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.presentation.screens.SpecialFavourite
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigationLocation
import cl.emilym.sinatra.ui.retryIfNeeded
import cl.emilym.sinatra.ui.toNavigationLocation
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication.Companion.init
import org.koin.core.annotation.Factory

data class QuickNavigationItem(
    val location: NavigationLocation,
    val special: SpecialFavouriteType?
)

sealed interface BrowseOption {
    data class QuickNavigateGroup(
        val items: List<QuickNavigationItem>
    ): BrowseOption
    data class AddSpecialFavourite(
        val type: SpecialFavouriteType
    ): BrowseOption
    data class NewServiceUpdate(
        val serviceAlert: ServiceAlert
    ): BrowseOption
}

@Factory
class BrowseViewModel(
    private val displayRoutesUseCase: DisplayRoutesUseCase,
    private val newServiceUpdateUseCase: NewServiceUpdateUseCase,
    private val quickNavigateUseCase: QuickNavigateUseCase,
): SinatraScreenModel {

    private val _routes = requestStateFlow { displayRoutesUseCase().item }
    val routes = _routes.state(RequestState.Initial())

    private val lastLocation = MutableStateFlow<MapLocation?>(null)

    private val newServices = flatRequestStateFlow { newServiceUpdateUseCase() }
    @OptIn(ExperimentalCoroutinesApi::class)
    private val quickNavigation = lastLocation.flatRequestStateFlow { quickNavigateUseCase(it)
        .mapLatest {
            Napier.d("SSS $it")
            it.mapNotNull { QuickNavigationItem(
            it.navigation.toNavigationLocation() ?: return@mapNotNull null,
            it.specialType
        ) } }
    }

    val options: StateFlow<List<BrowseOption>> = combine(
        newServices,
        quickNavigation
    ) { newServices, quickNavigation ->
        listOfNotNull(
            quickNavigation.unwrap().nullIfEmpty()?.let {
                BrowseOption.QuickNavigateGroup(it)
            },
            newServices.unwrap().nullIfEmpty()?.let {
                BrowseOption.NewServiceUpdate(it.first())
            }
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

    fun updateLocation(location: MapLocation?) {
        lastLocation.value = location ?: return
    }

}