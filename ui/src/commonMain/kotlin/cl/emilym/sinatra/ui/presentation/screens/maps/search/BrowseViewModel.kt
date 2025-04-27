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
import cl.emilym.sinatra.domain.smart.SpecialAddUseCase
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.presentation.screens.SpecialFavourite
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigationLocation
import cl.emilym.sinatra.ui.retryIfNeeded
import cl.emilym.sinatra.ui.toNavigationLocation
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinApplication.Companion.init
import org.koin.core.annotation.Factory

sealed interface QuickNavigationItem {
    val special: SpecialFavouriteType?

    data class Item(
        val location: NavigationLocation,
        override val special: SpecialFavouriteType?
    ): QuickNavigationItem
    data class ToAdd(
        override val special: SpecialFavouriteType
    ): QuickNavigationItem
}

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
    private val specialAddUseCase: SpecialAddUseCase
): SinatraScreenModel {

    private val _routes = requestStateFlow { displayRoutesUseCase().item }
    val routes = _routes.state(RequestState.Initial())

    private val lastLocation = MutableStateFlow<MapLocation?>(null)

    private val newServices = flatRequestStateFlow {
        withContext(Dispatchers.IO) { newServiceUpdateUseCase() }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private val quickNavigation = lastLocation.flatRequestStateFlow {
        withContext(Dispatchers.IO) {
            quickNavigateUseCase(it)
                .mapLatest {
                    it.mapNotNull {
                        QuickNavigationItem.Item(
                            it.navigation.toNavigationLocation() ?: return@mapNotNull null,
                            it.specialType
                        )
                    }
                }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private val specialAdd = flatRequestStateFlow {
        withContext(Dispatchers.IO) {
            specialAddUseCase().mapLatest { it.map { QuickNavigationItem.ToAdd(it) } }
        }
    }

    val options: StateFlow<List<BrowseOption>> = combine(
        newServices,
        quickNavigation,
        specialAdd
    ) { newServices, quickNavigation, specialAdd ->
        listOfNotNull(
            ((quickNavigation.unwrap().nullIfEmpty() ?: listOf()) +
            (specialAdd.unwrap().nullIfEmpty() ?: listOf())).nullIfEmpty()?.let {
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
            specialAdd.retryIfNeeded()
            quickNavigation.retryIfNeeded()
        }
    }

    fun updateLocation(location: MapLocation?) {
        lastLocation.value = location ?: return
    }

}