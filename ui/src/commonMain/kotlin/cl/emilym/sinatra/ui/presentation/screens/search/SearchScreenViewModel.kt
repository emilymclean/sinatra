package cl.emilym.sinatra.ui.presentation.screens.search

import androidx.lifecycle.viewModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.domain.search.RouteStopSearchUseCase
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.ui.presentation.screens.maps.search.MapSearchState
import cl.emilym.sinatra.ui.widgets.handleFlow
import cl.emilym.sinatra.ui.widgets.handleFlowProperly
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.seconds

interface SearchScreenViewModel {
    val query: MutableStateFlow<String?>
    val results: StateFlow<RequestState<List<SearchResult>>>
    val nearbyStops: StateFlow<List<StopWithDistance>?>
    val recentVisits: StateFlow<RequestState<List<RecentVisit>>>

    fun retryRecentVisits()

    fun search(query: String) {
        this.query.value = query
    }
}

fun <T> SearchScreenViewModel.searchHandler(
    routeStopSearchUseCase: RouteStopSearchUseCase,
    toState: (RequestState<List<SearchResult>>) -> T
): Flow<T> {
    return flow {
        emit(toState(RequestState.Initial()))
        emitAll(
            query.flatMapLatest {
                flow {
                    emit(toState(RequestState.Loading()))
                    emitAll(
                        query.debounce(1.seconds).flatMapLatest { query ->
                            when (query) {
                                null, "" -> flowOf(toState(RequestState.Initial()))
                                else -> handleFlow {
                                    routeStopSearchUseCase(query)
                                }.map { toState(it) }
                            }
                        }
                    )
                }
            }
        )
    }
}