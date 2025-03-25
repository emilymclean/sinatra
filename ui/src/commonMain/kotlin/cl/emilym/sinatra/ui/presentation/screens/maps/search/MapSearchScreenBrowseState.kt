package cl.emilym.sinatra.ui.presentation.screens.maps.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.widgets.AlertScaffold
import cl.emilym.sinatra.ui.widgets.RouteCard
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle

@Composable
fun MapSearchScreenBrowseState(
    viewModel: RouteListViewModel,
    mainViewModel: MapSearchViewModel
) {
    val bottomSheetState = LocalBottomSheetState.current?.bottomSheetState

    LaunchedEffect(Unit) {
        bottomSheetState?.halfExpand()
    }

    Scaffold { innerPadding ->
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val navigator = LocalNavigator.currentOrThrow
            val routes by viewModel.routes.collectAsStateWithLifecycle()
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
                        Modifier.height(1.rdp)
                    }
                    item {
                        AlertScaffold((alerts as? RequestState.Success)?.value)
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