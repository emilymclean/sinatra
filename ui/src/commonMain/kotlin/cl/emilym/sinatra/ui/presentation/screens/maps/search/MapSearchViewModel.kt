package cl.emilym.sinatra.ui.presentation.screens.maps.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.handle
import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.repository.AlertRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.search.RouteStopSearchUseCase
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.NEAREST_STOP_RADIUS
import cl.emilym.sinatra.ui.canberraRegion
import cl.emilym.sinatra.ui.presentation.screens.search.SearchScreenViewModel
import cl.emilym.sinatra.ui.presentation.screens.search.searchHandler
import cl.emilym.sinatra.ui.widgets.createRequestStateFlowFlow
import cl.emilym.sinatra.ui.widgets.handleFlow
import cl.emilym.sinatra.ui.widgets.handleFlowProperly
import cl.emilym.sinatra.ui.widgets.presentable
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.KoinApplication.Companion.init
import kotlin.time.Duration.Companion.seconds

const val NEARBY_STOPS_LIMIT = 5

sealed interface MapSearchState {
    data object Browse: MapSearchState
    data class Search(
        val results: RequestState<List<SearchResult>>
    ): MapSearchState
}

@KoinViewModel
class MapSearchViewModel(
    private val stopRepository: StopRepository,
    private val routeStopSearchUseCase: RouteStopSearchUseCase,
    private val recentVisitRepository: RecentVisitRepository,
    private val alertRepository: AlertRepository
): ViewModel(), SearchScreenViewModel {

    private val _state = MutableStateFlow(State.BROWSE)
    override val query = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val state: Flow<MapSearchState> = _state.flatMapLatest {
        when (it) {
            State.BROWSE -> flowOf(MapSearchState.Browse)
            State.SEARCH -> searchHandler(routeStopSearchUseCase) { MapSearchState.Search(it) }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MapSearchState.Browse)

    override val results: Flow<RequestState<List<SearchResult>>> = state.mapLatest {
        when (it) {
            is MapSearchState.Search -> it.results
            else -> RequestState.Initial()
        }
    }

    private val lastLocation = MutableStateFlow<MapLocation?>(null)
    val stops = MutableStateFlow<RequestState<List<Stop>>>(RequestState.Initial())
    val showCurrentLocation = MutableStateFlow(false)
    val zoomToLocation = MutableSharedFlow<Unit>()
    private var hasZoomedToLocation = false

    override val nearbyStops: Flow<List<StopWithDistance>?> = stops.combine(lastLocation) { stops, lastLocation ->
        if (stops !is RequestState.Success || lastLocation == null) return@combine null
        val stops = stops.value.nullIfEmpty() ?: return@combine null
        stops.map { StopWithDistance(it, distance(lastLocation, it.location)) }
            .filter { it.distance < NEAREST_STOP_RADIUS && it.stop.parentStation == null }
            .nullIfEmpty()
            ?.sortedBy { it.distance }
            ?.take(NEARBY_STOPS_LIMIT)
    }

    private val _recentVisits = createRequestStateFlowFlow<List<RecentVisit>>()
    override val recentVisits = _recentVisits.presentable()

    private val _alerts = createRequestStateFlowFlow<List<Alert>>()
    val alerts = _alerts.presentable()

    init {
        retry()
        retryRecentVisits()
        retryAlerts()
    }

    fun retry() {
        viewModelScope.launch {
            stops.handle {
                stopRepository.stops().item
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

    fun retryAlerts() {
        viewModelScope.launch {
            _alerts.handleFlowProperly {
                alertRepository.alerts()
            }
        }
    }

    override fun search(query: String) {
        this.query.value = query
    }

    fun openSearch() {
        query.value = null
        _state.value = State.SEARCH
    }

    fun openBrowse() {
        query.value = null
        _state.value = State.BROWSE
    }

    fun updateLocation(location: MapLocation) {
        lastLocation.value = location

        val isInRegion = canberraRegion.contains(location)
        showCurrentLocation.value = isInRegion

        if (!hasZoomedToLocation && isInRegion) {
            hasZoomedToLocation = true
            viewModelScope.launch {
                zoomToLocation.emit(Unit)
            }
        }
    }

    private enum class State {
        BROWSE, SEARCH
    }

}