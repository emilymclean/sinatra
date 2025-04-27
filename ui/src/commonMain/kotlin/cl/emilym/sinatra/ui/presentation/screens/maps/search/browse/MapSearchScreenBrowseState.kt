package cl.emilym.sinatra.ui.presentation.screens.maps.search.browse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
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
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.search.MapSearchViewModel
import cl.emilym.sinatra.ui.widgets.AlertScaffold
import cl.emilym.sinatra.ui.widgets.RouteCard
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.rememberBottomSheetPosition

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun Screen.MapSearchScreenBrowseState(
    viewModel: BrowseViewModel,
    mainViewModel: MapSearchViewModel
) {
    val bottomSheetState = LocalBottomSheetState.current?.bottomSheetState
    rememberBottomSheetPosition()

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
                                    NewServiceUpdateBrowseOption(it)
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
                    items(routes.size) {
                        RouteCard(
                            routes[it],
                            onClick = {
                                navigator.push(
                                    RouteDetailScreen(
                                        routes[it].id
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

