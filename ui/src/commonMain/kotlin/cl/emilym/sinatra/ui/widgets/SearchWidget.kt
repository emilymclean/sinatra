package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.NearbyStopsUseCase
import cl.emilym.sinatra.domain.search.RouteStopSearchUseCase
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.domain.search.SearchType
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.presentation.screens.search.SearchScreen
import cl.emilym.sinatra.ui.presentation.screens.search.SearchScreenViewModel
import cl.emilym.sinatra.ui.presentation.screens.search.searchHandler
import cl.emilym.sinatra.ui.text
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.map_search_nearby_stops
import sinatra.ui.generated.resources.no_search_results
import sinatra.ui.generated.resources.search_hint
import sinatra.ui.generated.resources.search_recently_viewed
import sinatra.ui.generated.resources.stop_detail_distance

@Factory
internal class DefaultSearchScreenViewModel(
    private val routeStopSearchUseCase: RouteStopSearchUseCase,
    private val recentVisitRepository: RecentVisitRepository,
    private val nearbyStopsUseCase: NearbyStopsUseCase,
    private val stopRepository: StopRepository
): SinatraScreenModel, SearchScreenViewModel {

    val lastLocation = MutableStateFlow<MapLocation?>(null)
    private val stops = requestStateFlow { stopRepository.stops() }

    override var query by mutableStateOf("")
    private val searchTypes = MutableStateFlow<List<SearchType>>(listOf())
    override val results = combine(
        snapshotFlow { query }, searchTypes
    ) { it, it1 -> it to it1 }.flatMapLatest {
        searchHandler(routeStopSearchUseCase, it.second) { it }
    }.state(RequestState.Initial())

    override val nearbyStops = stops.combine(lastLocation) { stops, lastLocation ->
        if (stops !is RequestState.Success || lastLocation == null) return@combine null
        val stops = stops.value.item.nullIfEmpty() ?: return@combine null
        with(nearbyStopsUseCase) { stops.filter(lastLocation).nullIfEmpty() }
    }.state(null)

    private val _recentVisits = searchTypes.flatRequestStateFlow {
        recentVisitRepository.all(it.mapNotNull { it.toRecentVisitType() })
    }
    override val recentVisits = _recentVisits.state(RequestState.Initial())

    fun retry() {
        screenModelScope.launch {
            stops.retry()
        }
        screenModelScope.launch {
            _recentVisits.retry()
        }
    }

    fun setSearchTypes(searchType: List<SearchType>) {
        searchTypes.value = searchType
    }

}

@Composable
fun Screen.SearchWidget(
    searchTypes: List<SearchType> = listOf(),
    clearSearchOnSelect: Boolean = true,
    onBackPressed: () -> Unit,
    onStopPressed: (Stop) -> Unit,
    onRoutePressed: (Route) -> Unit,
    onPlacePressed: (Place) -> Unit,
    extraPlaceholderContent: LazyListScope.() -> Unit = {}
) {
    val viewModel = koinScreenModel<DefaultSearchScreenViewModel>()

    LaunchedEffect(searchTypes) {
        viewModel.setSearchTypes(searchTypes)
    }

    val currentLocation = currentLocation()
    LaunchedEffect(currentLocation) {
        currentLocation?.let { viewModel.lastLocation.value = it }
    }

    SearchScreen(
        viewModel,
        searchTypes,
        {
            viewModel.query = ""
            onBackPressed()
        },
        {
            if (clearSearchOnSelect) viewModel.query = ""
            onStopPressed(it)
        },
        {
            if (clearSearchOnSelect) viewModel.query = ""
            onRoutePressed(it)
        },
        {
            if (clearSearchOnSelect) viewModel.query = ""
            onPlacePressed(it)
        },
        extraPlaceholderContent
    )
}

fun LazyListScope.BaseSearchWidget(
    query: String?,
    results: RequestState<List<SearchResult>>,
    nearbyStops: List<StopWithDistance>?,
    recentlyViewed: RequestState<List<RecentVisit>>,
    searchTypes: List<SearchType>,
    onSearch: (String) -> Unit,
    onBackPressed: () -> Unit,
    onStopPressed: (Stop) -> Unit,
    onRoutePressed: (Route) -> Unit,
    onPlacePressed: (Place) -> Unit,
    extraPlaceholderContent: LazyListScope.() -> Unit = {}
) {
    item {
        Box(Modifier.height(1.rdp))
    }
    item {
        val focusRequester = remember { FocusRequester() }
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 1.rdp),
            horizontalArrangement = Arrangement.spacedBy(1.rdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IosBackButton { onBackPressed() }
            SinatraTextField(
                query ?: "",
                { onSearch(it) },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                leadingIcon = {
                    SearchIcon(
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                placeholder = {
                    Text(stringResource(Res.string.search_hint))
                },
            )
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
    val hasRecentlyViewed = recentlyViewed is RequestState.Success &&
            !(recentlyViewed as? RequestState.Success)?.value.isNullOrEmpty()
    when {
        query.isNullOrBlank() -> {
            item {
                Box(Modifier.height(1.rdp))
            }
            extraPlaceholderContent()
            nearbyStops?.nullIfEmpty()?.let { nearbyStops ->
                if (!FeatureFlags.MAP_SEARCH_SCREEN_NEARBY_STOPS_SEARCH) return@let
                if (searchTypes.isNotEmpty() && !searchTypes.contains(SearchType.STOP)) return@let
                item {
                    Subheading(stringResource(Res.string.map_search_nearby_stops))
                }
                items(nearbyStops) {
                    StopCard(
                        it.stop,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onStopPressed(it.stop) },
                        subtitle = stringResource(Res.string.stop_detail_distance, it.distance.text),
                        showStopIcon = true
                    )
                }
                if (hasRecentlyViewed) {
                    item {
                        Box(Modifier.height(1.rdp))
                    }
                }
            }
            if (hasRecentlyViewed) {
                item {
                    Subheading(stringResource(Res.string.search_recently_viewed))
                }
                items((recentlyViewed as? RequestState.Success)?.value ?: listOf()) {
                    when (it) {
                        is RecentVisit.Stop -> StopCard(
                            it.stop,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onStopPressed(it.stop) },
                            showStopIcon = true
                        )

                        is RecentVisit.Route -> RouteCard(
                            it.route,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onRoutePressed(it.route) }
                        )

                        is RecentVisit.Place -> PlaceCard(
                            it.place,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onPlacePressed(it.place) },
                            showPlaceIcon = true
                        )
                    }
                }
            }
        }

        results is RequestState.Success -> {
            run {
                val results = (results as? RequestState.Success) ?: return@run
                item {
                    Box(Modifier.padding(0.5.rdp))
                }
                if (results.value.isEmpty()) {
                    item {
                        Box(Modifier.padding(0.5.rdp))
                    }
                    item {
                        ListHint(
                            stringResource(Res.string.no_search_results, query ?: "")
                        ) {
                            NoResultsIcon(
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                items(results.value) { result ->
                    when (result) {
                        is SearchResult.RouteResult -> {
                            RouteCard(
                                result.route,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onRoutePressed(result.route) }
                            )
                        }

                        is SearchResult.StopResult -> {
                            StopCard(
                                result.stop,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onStopPressed(result.stop) },
                                showStopIcon = true
                            )
                        }

                        is SearchResult.PlaceResult -> {
                            PlaceCard(
                                result.place,
                                modifier = Modifier.fillMaxWidth(),
                                showPlaceIcon = true,
                                onClick = { onPlacePressed(result.place) }
                            )
                        }
                    }
                }
            }
        }

        else -> {
            item {
                Box(Modifier.padding(1.rdp))
            }
            item {
                if (query.isNullOrBlank()) return@item
                Box(
                    Modifier.fillMaxSize().padding(horizontal = 1.rdp),
                    contentAlignment = Alignment.Center
                ) {
                    RequestStateWidget(results) {}
                }
            }
        }
    }
    item {
        Box(Modifier.height(imePadding()))
        Box(Modifier.height(1.rdp))
    }
}