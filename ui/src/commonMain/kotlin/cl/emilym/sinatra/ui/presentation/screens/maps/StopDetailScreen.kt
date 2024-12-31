package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.errorwidget.ErrorWidget
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.UpcomingRoutesForStopUseCase
import cl.emilym.sinatra.ui.maps.DEFAULT_ZOOM
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.stopMarkerIcon
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.open_maps
import cl.emilym.sinatra.ui.presentation.screens.maps.search.currentLocationZoom
import cl.emilym.sinatra.ui.stopJourneyNavigation
import cl.emilym.sinatra.ui.widgets.AccessibilityIconLockup
import cl.emilym.sinatra.ui.widgets.FavouriteButton
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.MapIcon
import cl.emilym.sinatra.ui.widgets.NavigateIcon
import cl.emilym.sinatra.ui.widgets.NoBusIcon
import cl.emilym.sinatra.ui.widgets.SheetIosBackButton
import cl.emilym.sinatra.ui.widgets.UpcomingRouteCard
import cl.emilym.sinatra.ui.widgets.WheelchairAccessibleIcon
import cl.emilym.sinatra.ui.widgets.createRequestStateFlowFlow
import cl.emilym.sinatra.ui.widgets.handleFlowProperly
import cl.emilym.sinatra.ui.widgets.openMaps
import cl.emilym.sinatra.ui.widgets.presentable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.accessibility_not_wheelchair_accessible
import sinatra.ui.generated.resources.accessibility_wheelchair_accessible
import sinatra.ui.generated.resources.no_upcoming_vehicles
import sinatra.ui.generated.resources.stop_not_found
import sinatra.ui.generated.resources.upcoming_vehicles
import sinatra.ui.generated.resources.stop_detail_navigate

@KoinViewModel
class StopDetailViewModel(
    private val stopRepository: StopRepository,
    private val upcomingRoutesForStopUseCase: UpcomingRoutesForStopUseCase,
    private val favouriteRepository: FavouriteRepository,
    private val recentVisitRepository: RecentVisitRepository
): ViewModel() {

    val favourited = MutableStateFlow(false)
    val stop = MutableStateFlow<RequestState<Stop?>>(RequestState.Initial())

    val _upcoming = createRequestStateFlowFlow<List<StopTimetableTime>>()
    val upcoming = _upcoming.presentable()

    fun init(stopId: StopId) {
        retryStop(stopId)
        retryUpcoming(stopId)

        viewModelScope.launch {
            favourited.emitAll(favouriteRepository.stopIsFavourited(stopId))
        }
        viewModelScope.launch {
            recentVisitRepository.addStopVisit(stopId)
        }
    }

    fun retryStop(stopId: StopId) {
        viewModelScope.launch {
            stop.handle {
                stopRepository.stop(stopId).item
            }
        }
    }

    fun retryUpcoming(stopId: StopId) {
        viewModelScope.launch {
            _upcoming.handleFlowProperly {
                upcomingRoutesForStopUseCase(stopId).map { it.item }
            }
        }
    }

    fun favourite(stopId: StopId, favourited: Boolean) {
        this.favourited.value = favourited
        viewModelScope.launch {
            favouriteRepository.setStopFavourite(stopId, favourited)
        }
    }

}

class StopDetailScreen(
    val stopId: StopId
): MapScreen {
    override val key: ScreenKey = "${this::class.qualifiedName!!}/$stopId"

    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinViewModel<StopDetailViewModel>()
        val bottomSheetState = LocalBottomSheetState.current
        val navigator = LocalNavigator.currentOrThrow
        val mapControl = LocalMapControl.current

        LaunchedEffect(bottomSheetState) {
            bottomSheetState.bottomSheetState.halfExpand()
        }

        LaunchedEffect(stopId) {
            viewModel.init(stopId)
        }

        val stop by viewModel.stop.collectAsState(RequestState.Initial())
        val upcoming by viewModel.upcoming.collectAsState(RequestState.Initial())
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            RequestStateWidget(stop, { viewModel.retryStop(stopId) }) { stop ->
                when {
                    stop == null -> {
                        Text(stringResource(Res.string.stop_not_found))
                    }
                    else -> {
                        LaunchedEffect(stop.location) {
                            mapControl.zoomToPoint(stop.location, currentLocationZoom + 2f)
                        }

                        LazyColumn(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(1.rdp)
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
                                    val favourited by viewModel.favourited.collectAsState(false)
                                    FavouriteButton(favourited, { viewModel.favourite(stopId, it) })
                                }
                            }
                            item {
                                Column(Modifier.padding(horizontal = 1.rdp)) {
                                    AccessibilityIconLockup(
                                        {
                                            WheelchairAccessibleIcon(stop.accessibility.wheelchair.isAccessible)
                                        }
                                    ) {
                                        Text(when(stop.accessibility.wheelchair.isAccessible) {
                                            true -> stringResource(Res.string.accessibility_wheelchair_accessible)
                                            false -> stringResource(Res.string.accessibility_not_wheelchair_accessible)
                                        })
                                    }
                                }
                            }
                            if (FeatureFlags.STOP_DETAIL_SHOW_IN_MAPS_BUTTON) {
                                item {
                                    val uriHandler = LocalUriHandler.current
                                    Button(
                                        onClick = { openMaps(uriHandler, stop.location) },
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 1.rdp)
                                    ) {
                                        MapIcon()
                                        Box(Modifier.width(0.5.rdp))
                                        Text(stringResource(Res.string.open_maps))
                                    }
                                }
                            }
                            item {
                                Button(
                                    onClick = { navigator.stopJourneyNavigation(stop) },
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 1.rdp)
                                ) {
                                    NavigateIcon()
                                    Box(Modifier.width(0.5.rdp))
                                    Text(stringResource(Res.string.stop_detail_navigate))
                                }
                            }
                            Upcoming(viewModel, upcoming, navigator)
                            item { Box(Modifier.height(1.rdp)) }
                        }
                    }
                }
            }
        }
    }

    fun LazyListScope.Upcoming(
        viewModel: StopDetailViewModel,
        upcoming: RequestState<List<StopTimetableTime>>,
        navigator: Navigator
    ) {
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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ErrorWidget(
                            upcoming.exception,
                            retry = { viewModel.retryUpcoming(stopId) }
                        )
                    }
                }
            }

            is RequestState.Success -> {
                item {
                    Row(
                        Modifier.padding(horizontal = 1.rdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(1.rdp)
                    ) {
                        Text(
                            stringResource(Res.string.upcoming_vehicles),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                when {
                    upcoming.value.isNotEmpty() -> items(upcoming.value) {
                        UpcomingRouteCard(
                            it,
                            StationTime.Scheduled(it.arrivalTime),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navigator.push(RouteDetailScreen(
                                it.routeId, it.serviceId, it.tripId, stopId
                            )) }
                        )
                    }
                    else -> item {
                        Box(Modifier.height(1.rdp))
                        ListHint(
                            stringResource(Res.string.no_upcoming_vehicles)
                        ) {
                            NoBusIcon(
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    override fun mapItems(): List<MapItem> {
        val viewModel = koinViewModel<StopDetailViewModel>()
        val stopRS by viewModel.stop.collectAsState(RequestState.Initial())
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