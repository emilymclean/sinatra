package cl.emilym.sinatra.ui.presentation.screens.maps.search.browse

import cafe.adriel.voyager.core.model.screenModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.models.ServiceAlertId
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.data.repository.ServiceAlertRepository
import cl.emilym.sinatra.domain.DisplayRoutesUseCase
import cl.emilym.sinatra.domain.prompt.FavouriteNearbyStopDeparturesUseCase
import cl.emilym.sinatra.domain.prompt.NewServiceUpdateUseCase
import cl.emilym.sinatra.domain.prompt.QuickNavigateUseCase
import cl.emilym.sinatra.domain.prompt.SpecialAddUseCase
import cl.emilym.sinatra.domain.prompt.StopDepartures
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigationLocation
import cl.emilym.sinatra.ui.retryIfNeeded
import cl.emilym.sinatra.ui.toNavigationLocation
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import cl.emilym.sinatra.ui.widgets.defaultConfig
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory

sealed interface QuickNavigationItem {
    val special: SpecialFavouriteType?
    val key: Any

    data class Item(
        val location: NavigationLocation,
        override val special: SpecialFavouriteType?
    ): QuickNavigationItem {
        override val key: Any
            get() = location.screenKey
    }
    data class ToAdd(
        override val special: SpecialFavouriteType
    ): QuickNavigationItem {
        override val key: Any
            get() = special
    }
}

sealed interface BrowsePrompt {
    data class QuickNavigateGroup(
        val items: List<QuickNavigationItem>
    ): BrowsePrompt
    data class LargeNearbyStopDepartures(
        val stop: StopDepartures
    ): BrowsePrompt
    data class NewServiceUpdate(
        val serviceAlert: ServiceAlert,
        val governmentSourcesUrl: String
    ): BrowsePrompt
}

@Factory
class BrowseViewModel(
    private val displayRoutesUseCase: DisplayRoutesUseCase,
    private val newServiceUpdateUseCase: NewServiceUpdateUseCase,
    private val quickNavigateUseCase: QuickNavigateUseCase,
    private val specialAddUseCase: SpecialAddUseCase,
    private val favouriteNearbyStopDeparturesUseCase: FavouriteNearbyStopDeparturesUseCase,
    private val serviceAlertRepository: ServiceAlertRepository,
    private val remoteConfigRepository: RemoteConfigRepository
): SinatraScreenModel {

    private val _routes = flatRequestStateFlow(defaultConfig) { displayRoutesUseCase().mapLatest { it.item } }
    val routes = _routes.state(RequestState.Initial())

    private val lastLocation = MutableStateFlow<MapLocation?>(null)

    private val newServices = flatRequestStateFlow(defaultConfig) {
        withContext(Dispatchers.IO) { newServiceUpdateUseCase() }
    }

    private val governmentSourcesUrl = requestStateFlow(defaultConfig) {
        remoteConfigRepository.governmentSourcesUrl()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val quickNavigation = lastLocation.flatRequestStateFlow(showLoading = false) {
        withContext(Dispatchers.IO) {
            quickNavigateUseCase(it).mapLatest {
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
    private val specialAdd = flatRequestStateFlow(defaultConfig) {
        withContext(Dispatchers.IO) {
            specialAddUseCase().mapLatest { it.map { QuickNavigationItem.ToAdd(it) } }
        }
    }
    private val nearbyStopDepartures = lastLocation.flatRequestStateFlow(showLoading = false) {
        it ?: return@flatRequestStateFlow flowOf(null)
        withContext(Dispatchers.IO) {
            favouriteNearbyStopDeparturesUseCase(it)
        }
    }

    val prompts: StateFlow<List<BrowsePrompt>> = combine(
        newServices,
        quickNavigation,
        specialAdd,
        nearbyStopDepartures,
        governmentSourcesUrl
    ) { newServices, quickNavigation, specialAdd, nearbyStopDepartures, governmentSourcesUrl ->
        buildList {
            // Add quick navigation
            buildList {
                quickNavigation.unwrap()?.let { addAll(it) }
                specialAdd.unwrap()?.let { addAll(it) }
            }.nullIfEmpty()?.let {
                add(BrowsePrompt.QuickNavigateGroup(it))
            }

            // Add nearby stop information
            nearbyStopDepartures.unwrap()?.let { add(BrowsePrompt.LargeNearbyStopDepartures(it)) }

            // Add new service alerts
            val newServices = newServices.unwrap().nullIfEmpty()
            val governmentSourcesUrl = governmentSourcesUrl.unwrap().nullIfEmpty()

            if (newServices != null && governmentSourcesUrl != null) {
                add(BrowsePrompt.NewServiceUpdate(
                    newServices.first(),
                    governmentSourcesUrl
                ))
            }
        }
    }.state(listOf())

    init {
        retry()
    }

    fun retry() {
        screenModelScope.launch { _routes.retryIfNeeded(routes.value) }
        screenModelScope.launch { newServices.retry() }
        screenModelScope.launch { specialAdd.retry() }
        screenModelScope.launch { quickNavigation.retry() }
        screenModelScope.launch { nearbyStopDepartures.retry() }
        screenModelScope.launch { governmentSourcesUrl.retry() }
    }

    fun refreshNearby() {
        screenModelScope.launch {
            nearbyStopDepartures.retry()
        }
    }

    fun updateLocation(location: MapLocation?) {
        location ?: return
        val ll = lastLocation.value
        if (ll == null) {
            lastLocation.value = location
            return
        }

        if (distance(ll, location) < 0.5) return
        lastLocation.value = location
    }

    fun markAlertViewed(id: ServiceAlertId) {
        screenModelScope.launch {
            withContext(Dispatchers.IO) {
                serviceAlertRepository.markViewed(id)
            }
        }
    }

}