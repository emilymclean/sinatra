package cl.emilym.sinatra.ui.presentation.screens.maps.search.browse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.presentation.screens.maps.route.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.search.MapSearchViewModel
import cl.emilym.sinatra.ui.widgets.AlertScaffold
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.NoBusIcon
import cl.emilym.sinatra.ui.widgets.RouteCard
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.currentLocation
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.browse_routes_in_area
import sinatra.ui.generated.resources.browse_routes_in_area_no_routes

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun Screen.MapSearchScreenBrowseState(
    viewModel: BrowseViewModel,
    mainViewModel: MapSearchViewModel
) {
    val bottomSheetState = LocalBottomSheetState.current?.bottomSheetState
    LaunchedEffect(Unit) {
        bottomSheetState?.halfExpand()
    }

    val currentLocation = currentLocation()
    LaunchedEffect(currentLocation) {
        viewModel.updateLocation(currentLocation)
    }

    Scaffold { innerPadding ->
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val navigator = LocalNavigator.currentOrThrow
            val routes by viewModel.routes.collectAsStateWithLifecycle()
            val options by viewModel.prompts.collectAsStateWithLifecycle()
            val alerts by mainViewModel.alerts.collectAsStateWithLifecycle()

            LaunchedEffect(routes) {
                if (routes is RequestState.Failure) {
                    bottomSheetState?.expand()
                }
            }

            RequestStateWidget(routes, { viewModel.retry() }) { routes ->
                LazyColumn(
                    contentPadding = innerPadding
                ) {
                    item {
                        AlertScaffold((alerts as? RequestState.Success)?.value)
                    }
                    if (options.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(1.rdp))
                        }
                    }
                    items(
                        options,
                        { it::class.simpleName ?: "" }
                    ) {
                        when (it) {
                            is BrowsePrompt.NewServiceUpdate -> {
                                Box(Modifier.animateItem()) {
                                    NewServiceUpdateBrowseOption(it, viewModel)
                                }
                            }
                            is BrowsePrompt.QuickNavigateGroup -> {
                                Box(Modifier.animateItem()) {
                                    QuickNavigateGroupBrowseOption(it)
                                }
                            }
                            is BrowsePrompt.LargeNearbyStopDepartures -> {
                                Box(Modifier.animateItem()) {
                                    LargeNearbyStopDeparturesWidget(
                                        it,
                                        { viewModel.refreshNearby() }
                                    )
                                }
                            }
                            else -> {}
                        }
                        Spacer(Modifier.height(1.rdp))
                    }
                    if (routes.isRelevantToRegion) {
                        item {
                            Text(
                                stringResource(Res.string.browse_routes_in_area),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 1.rdp)
                            )
                            Spacer(Modifier.height(0.25.rdp))
                            if (routes.routes.isEmpty()) {
                                ListHint(
                                    stringResource(Res.string.browse_routes_in_area_no_routes),
                                    modifier = Modifier.padding(horizontal = 1.rdp)
                                ) {
                                    NoBusIcon()
                                }
                            }
                        }
                    }
                    items(routes.routes.size) {
                        RouteCard(
                            routes.routes[it],
                            onClick = {
                                navigator.push(
                                    RouteDetailScreen(
                                        routes.routes[it].id
                                    )
                                )
                            }
                        )
                    }
                    item {
                        Modifier.height(1.rdp)
                    }
                }
            }
        }
    }
}

