package cl.emilym.sinatra.ui.presentation.screens.search

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.placeCardDefaultNavigation
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.search.MapSearchState
import cl.emilym.sinatra.ui.presentation.screens.maps.search.MapSearchViewModel
import cl.emilym.sinatra.ui.text
import cl.emilym.sinatra.ui.widgets.IosBackButton
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.NoResultsIcon
import cl.emilym.sinatra.ui.widgets.PlaceCard
import cl.emilym.sinatra.ui.widgets.RouteCard
import cl.emilym.sinatra.ui.widgets.SearchIcon
import cl.emilym.sinatra.ui.widgets.SinatraBackHandler
import cl.emilym.sinatra.ui.widgets.SinatraTextField
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.Subheading
import cl.emilym.sinatra.ui.widgets.imePadding
import cl.emilym.sinatra.ui.widgets.viewportHeight
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.map_search_nearby_stops
import sinatra.ui.generated.resources.no_search_results
import sinatra.ui.generated.resources.search_hint
import sinatra.ui.generated.resources.search_recently_viewed
import sinatra.ui.generated.resources.stop_detail_distance

@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel,
    onBackPressed: () -> Unit,
    onStopPressed: (Stop) -> Unit,
    onRoutePressed: (Route) -> Unit,
    onPlacePressed: (Place) -> Unit,
    extraPlaceholderContent: LazyListScope.() -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }

    val results by viewModel.results.collectAsState(RequestState.Initial())

    val recentlyViewed by viewModel.recentVisits.collectAsState(RequestState.Initial())
    val nearbyStops by viewModel.nearbyStops.collectAsState(null)

    val bottomSheetState = LocalBottomSheetState.current?.bottomSheetState
    LaunchedEffect(Unit) {
        bottomSheetState?.expand()
    }

    SinatraBackHandler(true) {
        onBackPressed()
    }

    val query by viewModel.query.collectAsState()
    Scaffold { innerPadding ->
        LazyColumn(
            Modifier.fillMaxWidth().heightIn(min = viewportHeight()),
            contentPadding = innerPadding
        ) {
            item {
                Box(Modifier.height(1.rdp))
            }
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 1.rdp),
                    horizontalArrangement = Arrangement.spacedBy(1.rdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IosBackButton { onBackPressed() }
                    SinatraTextField(
                        query ?: "",
                        { viewModel.search(it) },
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
    }
}