package cl.emilym.sinatra.ui.presentation.screens.search

import androidx.lifecycle.viewModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.ui.widgets.handleFlowProperly
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

interface SearchScreenViewModel {
    val query: MutableStateFlow<String?>
    val results: Flow<RequestState<List<SearchResult>>>
    val nearbyStops: Flow<List<StopWithDistance>?>
    val recentVisits: Flow<RequestState<List<RecentVisit>>>

    fun retryRecentVisits()

    fun search(query: String) {
        this.query.value = query
    }
}