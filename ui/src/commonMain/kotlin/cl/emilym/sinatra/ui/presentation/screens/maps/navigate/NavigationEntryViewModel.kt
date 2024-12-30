package cl.emilym.sinatra.ui.presentation.screens.maps.navigate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.handle
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.repository.NetworkGraphRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.CalculateJourneyUseCase
import cl.emilym.sinatra.domain.JourneyLocation
import cl.emilym.sinatra.domain.search.RouteStopSearchUseCase
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.NEAREST_STOP_RADIUS
import cl.emilym.sinatra.ui.presentation.screens.maps.search.NEARBY_STOPS_LIMIT
import cl.emilym.sinatra.ui.presentation.screens.search.SearchScreenViewModel
import cl.emilym.sinatra.ui.presentation.screens.search.searchHandler
import cl.emilym.sinatra.ui.widgets.createRequestStateFlowFlow
import cl.emilym.sinatra.ui.widgets.handleFlowProperly
import cl.emilym.sinatra.ui.widgets.presentable
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel

private sealed interface State {
    data object Journey: State
    data class Search(
        val targetIsOrigin: Boolean
    ): State
}

sealed interface NavigationEntryState {
    data class Journey(
        val state: NavigationState
    ): NavigationEntryState
    data class Search(
        val results: RequestState<List<SearchResult>>
    ): NavigationEntryState
}

@KoinViewModel
class NavigationEntryViewModel(
    private val calculateJourneyUseCase: CalculateJourneyUseCase,
    private val routeStopSearchUseCase: RouteStopSearchUseCase,
    private val networkGraphRepository: NetworkGraphRepository,
    private val recentVisitRepository: RecentVisitRepository,
    private val stopRepository: StopRepository
): ViewModel(), SearchScreenViewModel {

    override val query = MutableStateFlow<String?>(null)

    private var loadedGraph: Boolean = false
        set(value) {
            field = value
            if (value) calculate()
        }
    private var calculationJob: Job? = null

    private var currentLocation: MapLocation? = null
    private val lastLocation = MutableStateFlow<MapLocation?>(null)

    private var destinationCurrentLocation = false
    private var _destination: MapLocation? = null
        set(value) {
            field = value
            if (value != null) calculate()
        }
    private var originCurrentLocation = false
    private var _origin: MapLocation? = null
        set(value) {
            field = value
            if (value != null) calculate()
        }

    val destination = MutableStateFlow<NavigationLocation?>(null)
    val origin = MutableStateFlow<NavigationLocation?>(null)

    private val stops = MutableStateFlow<RequestState<List<Stop>>>(RequestState.Initial())
    private val _recentVisits = createRequestStateFlowFlow<List<RecentVisit>>()
    override val recentVisits = _recentVisits.presentable().mapLatest {
        when (it) {
            is RequestState.Success -> RequestState.Success(it.value.filter { it !is RecentVisit.Route })
            else -> it
        }
    }

    private val navigationState = MutableStateFlow<NavigationState>(NavigationState.GraphLoading)
    private val _state = MutableStateFlow<State>(State.Journey)

    override val nearbyStops: Flow<List<StopWithDistance>?> = stops.combine(lastLocation) { stops, lastLocation ->
        if (stops !is RequestState.Success || lastLocation == null) return@combine null
        val stops = stops.value.nullIfEmpty() ?: return@combine null
        stops.map { StopWithDistance(it, distance(lastLocation, it.location)) }
            .filter { it.distance < NEAREST_STOP_RADIUS && it.stop.parentStation == null }
            .nullIfEmpty()
            ?.sortedBy { it.distance }
            ?.take(NEARBY_STOPS_LIMIT)
    }

    val state = _state.flatMapLatest {
        when (it) {
            is State.Journey -> navigationState.map { NavigationEntryState.Journey(it) }
            is State.Search -> searchHandler(routeStopSearchUseCase) { NavigationEntryState.Search(
                when (it) {
                    is RequestState.Success -> RequestState.Success(it.value.filter { it !is SearchResult.RouteResult })
                    else -> it
                }
            ) }
        }
    }

    override val results = state.mapLatest {
        when (it) {
            is NavigationEntryState.Search -> it.results
            else -> RequestState.Initial()
        }
    }

    fun init(destination: NavigationLocation, origin: NavigationLocation) {
        retryLoadingGraph()
        retryRecentVisits()
        retryStops()
        setDestination(destination)
        setOrigin(origin)
    }

    fun retryLoadingGraph() {
        viewModelScope.launch {
            navigationState.value = NavigationState.GraphLoading
            try {
                withContext(Dispatchers.IO) {
                    networkGraphRepository.networkGraph()
                }
                navigationState.value = NavigationState.GraphReady
                loadedGraph = true
            } catch (e: Exception) {
                navigationState.value = NavigationState.GraphFailed(e)
            }
        }
    }

    override fun retryRecentVisits() {
        viewModelScope.launch {
            _recentVisits.handleFlowProperly {
                recentVisitRepository.all()
            }
        }
    }

    fun retryStops() {
        viewModelScope.launch {
            stops.handle {
                stopRepository.stops().item
            }
        }
    }

    private fun setDestination(navigationLocation: NavigationLocation) {
        destination.value = navigationLocation
        unpackLocation(
            navigationLocation
        ) { location, current ->
            _destination = location
            destinationCurrentLocation = current
        }
    }

    private fun setOrigin(navigationLocation: NavigationLocation) {
        origin.value = navigationLocation
        unpackLocation(
            navigationLocation
        ) { location, current ->
            _origin = location
            originCurrentLocation = current
        }
    }

    fun onOriginClick() {
        onOpenSearch()
        _state.value = State.Search(true)
    }

    fun onDestinationClick() {
        onOpenSearch()
        _state.value = State.Search(false)
    }

    private fun onOpenSearch() {
        calculationJob?.cancel()
        query.value = null
    }

    fun onSearchItemClicked(item: NavigationLocation) {
        val s = _state.value as? State.Search ?: return
        when (s.targetIsOrigin) {
            true -> setOrigin(item)
            false -> setDestination(item)
        }
        _state.value = State.Journey
    }

    fun openJourney() {
        _state.value = State.Journey
        calculate()
    }

    fun updateCurrentLocation(location: MapLocation) {
        lastLocation.value = location
        val currentLocation = currentLocation
        if (currentLocation != null && distance(location, currentLocation) < 1.0) return
        this.currentLocation = location
        if (originCurrentLocation) _origin = location
        if (destinationCurrentLocation) _destination = location
    }

    private fun unpackLocation(
        navigationLocation: NavigationLocation,
        save: (MapLocation?, Boolean) -> Unit
    ) {
        when (navigationLocation) {
            is NavigationLocation.LocatableNavigationLocation -> {
                save(navigationLocation.location, false)
            }
            is NavigationLocation.CurrentLocation -> {
                save(currentLocation, true)
            }
        }
        navigationLocation.recentVisit?.let {
            viewModelScope.launch {
                recentVisitRepository.add(it)
            }
        }
    }

    private fun calculate() {
        calculationJob?.cancel()
        if (!loadedGraph) return
        val destination = _destination ?: return
        val origin = _origin ?: return

        viewModelScope.launch {
            navigationState.value = NavigationState.JourneyCalculating
            try {
                navigationState.value = NavigationState.JourneyFound(
                    calculateJourneyUseCase(
                        JourneyLocation(
                            origin,
                            exact = this@NavigationEntryViewModel.origin.value is NavigationLocation.Stop
                        ),
                        JourneyLocation(
                            destination,
                            exact = this@NavigationEntryViewModel.destination.value is NavigationLocation.Stop
                        )
                    ).also {
                        Napier.d("Journey = $it")
                    }
                )
            } catch (e: Exception) {
                navigationState.value = NavigationState.JourneyFailed(e)
            }
        }
    }

}