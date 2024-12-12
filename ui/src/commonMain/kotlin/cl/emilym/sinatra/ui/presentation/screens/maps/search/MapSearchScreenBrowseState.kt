package cl.emilym.sinatra.ui.presentation.screens.maps.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteListViewModel
import cl.emilym.sinatra.ui.widgets.RouteCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapSearchScreenBrowseState() {
    val viewModel = koinViewModel<RouteListViewModel>()
    val bottomSheetState = LocalBottomSheetState.current.bottomSheetState

    LaunchedEffect(Unit) {
        bottomSheetState.halfExpand()
    }

    Box(
        Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val routes by viewModel.routes.collectAsState(RequestState.Initial())
        RequestStateWidget(routes, { viewModel.retry() }) { routes ->
            LazyColumn {
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
            }
        }
    }
}