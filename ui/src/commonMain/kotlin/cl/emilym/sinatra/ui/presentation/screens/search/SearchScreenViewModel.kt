package cl.emilym.sinatra.ui.presentation.screens.search

import cl.emilym.compose.requeststate.RequestState
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.domain.search.RouteStopSearchUseCase
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.domain.search.SearchType
import cl.emilym.sinatra.ui.widgets.handleFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
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

    fun search(query: String) {
        this.query.value = query
    }
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
fun <T> SearchScreenViewModel.searchHandler(
    routeStopSearchUseCase: RouteStopSearchUseCase,
    filters: List<SearchType> = listOf(),
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
                                    routeStopSearchUseCase(query, filters)
                                }.map { toState(it) }
                            }
                        }
                    )
                }
            }
        )
    }
}