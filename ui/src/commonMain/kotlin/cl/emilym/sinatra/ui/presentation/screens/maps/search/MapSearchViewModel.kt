package cl.emilym.sinatra.ui.presentation.screens.maps.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.handle
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.search.RouteStopSearchUseCase
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.ui.widgets.createRequestStateFlowFlow
import cl.emilym.sinatra.ui.widgets.handleFlow
import cl.emilym.sinatra.ui.widgets.handleFlowProperly
import cl.emilym.sinatra.ui.widgets.presentable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

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
    private val recentVisitRepository: RecentVisitRepository
): ViewModel() {

    private val _state = MutableStateFlow(State.BROWSE)
    val query = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val state: Flow<MapSearchState> = _state.flatMapLatest {
        when (it) {
            State.BROWSE -> flowOf(MapSearchState.Browse)
            State.SEARCH -> flow<MapSearchState> {
                emit(MapSearchState.Search(RequestState.Initial()))
                emitAll(
                    query.flatMapLatest {
                        flow {
                            emit(MapSearchState.Search(RequestState.Loading()))
                            emitAll(
                                query.debounce(1.seconds).flatMapLatest { query ->
                                    when (query) {
                                        null, "" -> flowOf(MapSearchState.Search(RequestState.Initial()))
                                        else -> handleFlow {
                                            routeStopSearchUseCase(query)
                                        }.map { MapSearchState.Search(it) }
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }
    }
    val stops = MutableStateFlow<RequestState<List<Stop>>>(RequestState.Initial())
    var hasZoomedToLocation = false

    private val _recentVisits = createRequestStateFlowFlow<List<RecentVisit>>()
    val recentVisits = _recentVisits.presentable()

    init {
        retry()
        retryRecentVisits()
    }

    fun retry() {
        viewModelScope.launch {
            stops.handle {
                stopRepository.stops().item
            }
        }
    }

    fun retryRecentVisits() {
        viewModelScope.launch {
            _recentVisits.handleFlowProperly {
                recentVisitRepository.all()
            }
        }
    }

    fun search(query: String) {
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

    private enum class State {
        BROWSE, SEARCH
    }

}