package cl.emilym.sinatra.ui.presentation.screens.maps.stop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.errorwidget.ErrorWidget
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.ReferencedTime
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.stopMarkerIcon
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.open_maps
import cl.emilym.sinatra.ui.presentation.screens.maps.route.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.search.zoomThreshold
import cl.emilym.sinatra.ui.stopJourneyNavigation
import cl.emilym.sinatra.ui.widgets.AccessibilityIconLockup
import cl.emilym.sinatra.ui.widgets.AlertScaffold
import cl.emilym.sinatra.ui.widgets.FavouriteButton
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.MapIcon
import cl.emilym.sinatra.ui.widgets.NavigateIcon
import cl.emilym.sinatra.ui.widgets.NoBusIcon
import cl.emilym.sinatra.ui.widgets.SheetIosBackButton
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.StopStationTime
import cl.emilym.sinatra.ui.widgets.Subheading
import cl.emilym.sinatra.ui.widgets.UpcomingRouteCard
import cl.emilym.sinatra.ui.widgets.WheelchairAccessibleIcon
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.openMaps
import cl.emilym.sinatra.ui.widgets.pick
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.accessibility_not_wheelchair_accessible
import sinatra.ui.generated.resources.accessibility_wheelchair_accessible
import sinatra.ui.generated.resources.no_upcoming_vehicles
import sinatra.ui.generated.resources.semantics_favourite_stop
import sinatra.ui.generated.resources.stop_detail_child_stations
import sinatra.ui.generated.resources.stop_detail_last_departure
import sinatra.ui.generated.resources.stop_detail_navigate
import sinatra.ui.generated.resources.stop_not_found
import sinatra.ui.generated.resources.upcoming_vehicles

class StopDetailScreen(
    val stopId: StopId
): MapScreen {
    override val key: ScreenKey = "${this::class.qualifiedName!!}/$stopId"

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinScreenModel<StopDetailViewModel>()
        val bottomSheetState = LocalBottomSheetState.current
        val navigator = LocalNavigator.currentOrThrow
        val mapControl = LocalMapControl.current

        LaunchedEffect(bottomSheetState) {
            bottomSheetState?.bottomSheetState?.halfExpand()
        }

        LifecycleEffectOnce {
            viewModel.init(stopId)
        }

        val stop by viewModel.stop.collectAsStateWithLifecycle()
        val alerts by viewModel.alerts.collectAsStateWithLifecycle()
        val state by viewModel.state.collectAsStateWithLifecycle()

        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            RequestStateWidget(stop, { viewModel.retry() }) { stop ->
                when {
                    stop == null -> {
                        Text(stringResource(Res.string.stop_not_found))
                    }
                    else -> {
                        LaunchedEffect(stop.location) {
                            mapControl.moveToPoint(stop.location, minZoom = zoomThreshold)
                        }

                        Scaffold { innerPadding ->
                            LazyColumn(
                                Modifier.fillMaxSize(),
                                contentPadding = innerPadding
                            ) {
                                item { Box(Modifier.height(1.rdp)) }
                                item {
                                    Row(
                                        Modifier.padding(horizontal = 1.rdp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(1.rdp)
                                    ) {
                                        SheetIosBackButton()
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                stop.name,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                        }
                                        val favourited by viewModel.favourited.collectAsStateWithLifecycle()
                                        val favouriteContentDescription = stringResource(Res.string.semantics_favourite_stop)
                                        FavouriteButton(
                                            favourited,
                                            { viewModel.favourite(stopId, it) },
                                            Modifier.semantics {
                                                contentDescription = favouriteContentDescription
                                                selected = favourited
                                            }
                                        )
                                    }
                                }
                                item {
                                    AlertScaffold((alerts as? RequestState.Success)?.value)
                                }
                                item { Box(Modifier.height(1.rdp)) }

                                item {
                                    val actions by viewModel.actions.collectAsStateWithLifecycle()
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 1.rdp),
                                        horizontalArrangement = Arrangement.spacedBy(1.rdp)
                                    ) {
                                        items(actions) { action ->
                                            StopActionButton(
                                                icon = when (action) {
                                                    is StopDetailAction.Navigate -> { { NavigateIcon() } }
                                                    else -> null
                                                },
                                                text = stringResource(
                                                    when (action) {
                                                        is StopDetailAction.Navigate -> Res.string.stop_detail_navigate
                                                        is StopDetailAction.LastDepartures -> Res.string.stop_detail_last_departure
                                                        is StopDetailAction.Children -> Res.string.stop_detail_child_stations
                                                    }
                                                ),
                                                action.highlighted,
                                                onClick = {
                                                    when (action) {
                                                        is StopDetailAction.Navigate -> navigator.stopJourneyNavigation(stop)
                                                        is StopDetailAction.LastDepartures -> viewModel.option(StopDetailPage.LAST_DEPARTURES)
                                                        is StopDetailAction.Children -> viewModel.option(StopDetailPage.CHILDREN)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                                item { Box(Modifier.height(2.rdp)) }

                                val state = state
                                when (state) {
                                    is StopDetailState.Upcoming -> UpcomingPage(stop, state)
                                    is StopDetailState.LastDepartures -> LastDeparturesPage(state)
                                    is StopDetailState.Children -> ChildrenPage(state)
                                }

                                item { Box(Modifier.height(1.rdp)) }
                            }
                        }
                    }
                }
            }
        }
    }

    fun LazyListScope.UpcomingPage(
        stop: Stop,
        state: StopDetailState.Upcoming
    ) {
        if (FeatureFlags.STOP_DETAIL_SHOW_ACCESSIBILITY) {
            item {
                Column(Modifier.padding(horizontal = 1.rdp)) {
                    AccessibilityIconLockup(
                        {
                            WheelchairAccessibleIcon(stop.accessibility.wheelchair.isAccessible)
                        }
                    ) {
                        Text(
                            when (stop.accessibility.wheelchair.isAccessible) {
                                true -> stringResource(Res.string.accessibility_wheelchair_accessible)
                                false -> stringResource(Res.string.accessibility_not_wheelchair_accessible)
                            }
                        )
                    }
                }
            }
            item { Box(Modifier.height(2.rdp)) }
        }

        Departures(
            state.upcoming,
            empty = {
                ListHint(
                    stringResource(Res.string.no_upcoming_vehicles)
                ) {
                    NoBusIcon(
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            Subheading(stringResource(Res.string.upcoming_vehicles))
        }
    }

    fun LazyListScope.LastDeparturesPage(
        state: StopDetailState.LastDepartures
    ) {
        Departures(
            state.lastDepartures,
            forceShowDepartures = true,
            empty = {}
        ) {
            Subheading(stringResource(Res.string.stop_detail_last_departure))
        }
    }

    fun LazyListScope.ChildrenPage(
        state: StopDetailState.Children
    ) {
        item {
            Subheading(stringResource(Res.string.stop_detail_child_stations))
        }
        if (state.children !is RequestState.Success) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RequestStateWidget(state.children) {}
                }
            }
        }
        items(state.children.unwrap(emptyList())) {
            val navigator = LocalNavigator.currentOrThrow
            StopCard(
                it,
                onClick = { navigator.push(StopDetailScreen(it.id)) },
                showStopIcon = true
            )
        }
    }

    fun LazyListScope.Departures(
        upcoming: RequestState<List<IStopTimetableTime>>,
        forceShowDepartures: Boolean = false,
        empty: @Composable () -> Unit,
        title: @Composable () -> Unit,
    ) {
        item {
            title()
        }
        when (upcoming) {
            is RequestState.Initial, is RequestState.Loading -> {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is RequestState.Failure -> {
                item {
                    val viewModel = koinScreenModel<StopDetailViewModel>()
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ErrorWidget(
                            (upcoming.exception as? Exception) ?: Exception(upcoming.exception),
                            retry = { viewModel.retry() }
                        )
                    }
                }
            }

            is RequestState.Success -> {
                when {
                    upcoming.value.isNotEmpty() -> items(upcoming.value) {
                        val navigator = LocalNavigator.currentOrThrow
                        UpcomingRouteCard(
                            it,
                            when {
                                forceShowDepartures -> StopStationTime.Departure(it.stationTime.departure)
                                else -> it.stationTime.pick(it.route, it.sequence <= 1)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navigator.push(
                                RouteDetailScreen(
                                it.routeId,
                                it.serviceId,
                                it.tripId,
                                stopId,
                                (it.stationTime.arrival.time as? ReferencedTime)?.startOfDay
                            )
                            ) }
                        )
                    }
                    else -> item {
                        Box(Modifier.height(1.rdp))
                        empty()
                    }
                }
            }
        }
    }

    @Composable
    override fun mapItems(): List<MapItem> {
        val viewModel = koinScreenModel<StopDetailViewModel>()
        val stopRS by viewModel.stop.collectAsStateWithLifecycle()
        val stop = (stopRS as? RequestState.Success)?.value ?: return listOf()

        return listOf(
            MarkerItem(
                stop.location,
                stopMarkerIcon(stop),
                id = "stopDetail-${stop.id}"
            )
        )
    }
}