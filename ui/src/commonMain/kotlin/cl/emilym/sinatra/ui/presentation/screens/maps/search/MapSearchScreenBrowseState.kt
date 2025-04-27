package cl.emilym.sinatra.ui.presentation.screens.maps.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.presentation.screens.ServiceAlertScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.widgets.AlertScaffold
import cl.emilym.sinatra.ui.widgets.ListCard
import cl.emilym.sinatra.ui.widgets.RouteCard
import cl.emilym.sinatra.ui.widgets.ServiceAlertCard
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.browse_option_see_all_service_alerts

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun Screen.MapSearchScreenBrowseState(
    viewModel: BrowseViewModel,
    mainViewModel: MapSearchViewModel
) {
    val bottomSheetState = LocalBottomSheetState.current?.bottomSheetState
    val scope = rememberCoroutineScope()

    LifecycleEffectOnce {
        scope.launch {
            bottomSheetState?.halfExpand()
        }
    }

    Scaffold { innerPadding ->
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val navigator = LocalNavigator.currentOrThrow
            val routes by viewModel.routes.collectAsStateWithLifecycle()
            val options by viewModel.options.collectAsStateWithLifecycle()
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
                    items(options) {
                        when (it) {
                            is BrowseOption.NewServiceUpdate -> {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 1.rdp)
                                        .clickable { navigator.push(ServiceAlertScreen()) },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        contentColor = MaterialTheme.colorScheme.onSurface,
                                    )
                                ) {
                                    ServiceAlertCard(
                                        it.serviceAlert,
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            contentColor = MaterialTheme.colorScheme.onSurface,
                                        )
                                    )
                                    ListCard(
                                        icon = null,
                                        onClick = { navigator.push(ServiceAlertScreen()) }
                                    ) {
                                        Text(stringResource(Res.string.browse_option_see_all_service_alerts))
                                    }
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