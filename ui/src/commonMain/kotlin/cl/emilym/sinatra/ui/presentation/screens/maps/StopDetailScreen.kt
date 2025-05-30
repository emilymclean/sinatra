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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.errorwidget.ErrorWidget
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.child
import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.requestStateFlow
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.ReferencedTime
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.repository.AlertDisplayContext
import cl.emilym.sinatra.data.repository.AlertRepository
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.UpcomingRoutesForStopUseCase
import cl.emilym.sinatra.lib.naturalComparator
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.stopMarkerIcon
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.open_maps
import cl.emilym.sinatra.ui.presentation.screens.maps.search.zoomThreshold
import cl.emilym.sinatra.ui.retryIfNeeded
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
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.Subheading
import cl.emilym.sinatra.ui.widgets.UpcomingRouteCard
import cl.emilym.sinatra.ui.widgets.WheelchairAccessibleIcon
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.openMaps
import cl.emilym.sinatra.ui.widgets.pick
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.accessibility_not_wheelchair_accessible
import sinatra.ui.generated.resources.accessibility_wheelchair_accessible
import sinatra.ui.generated.resources.no_upcoming_vehicles
import sinatra.ui.generated.resources.semantics_favourite_stop
import sinatra.ui.generated.resources.stop_detail_child_stations
import sinatra.ui.generated.resources.stop_detail_navigate
import sinatra.ui.generated.resources.stop_not_found
import sinatra.ui.generated.resources.upcoming_vehicles

@Factory
class StopDetailViewModel(
    private val stopRepository: StopRepository,
    private val upcomingRoutesForStopUseCase: UpcomingRoutesForStopUseCase,
    private val favouriteRepository: FavouriteRepository,
    private val recentVisitRepository: RecentVisitRepository,
    private val alertRepository: AlertRepository
): SinatraScreenModel {

    private val stopId = MutableStateFlow<StopId?>(null)

    val favourited = MutableStateFlow(false)

    private val _alerts = stopId.filterNotNull().flatRequestStateFlow { stopId ->
        alertRepository.alerts(AlertDisplayContext.Stop(stopId))
    }
    val alerts = _alerts.state()

    private val stopWithChildren = stopId.filterNotNull().requestStateFlow { stopId ->
        stopRepository.stopWithChildren(stopId).item
    }
    val stop = stopWithChildren.child { it?.stop }.state(RequestState.Initial())
    val children = stopWithChildren
        .child {
            it?.children?.sortedWith(compareBy(naturalComparator()) { it.name })
        }
        .state(RequestState.Initial())

    private val _upcoming = stopId.filterNotNull().flatRequestStateFlow { stopId ->
        upcomingRoutesForStopUseCase(stopId).map { it.item }
    }
    val upcoming = _upcoming.state()

    fun init(stopId: StopId) {
        this.stopId.value = stopId

        screenModelScope.launch {
            favourited.emitAll(favouriteRepository.stopIsFavourited(stopId))
        }
        screenModelScope.launch {
            recentVisitRepository.addStopVisit(stopId)
        }
    }

    fun retry() {
        screenModelScope.launch { _alerts.retryIfNeeded(alerts.value) }
        screenModelScope.launch { stopWithChildren.retryIfNeeded(stop.value) }
        screenModelScope.launch { _upcoming.retryIfNeeded(upcoming.value) }
    }

    fun favourite(stopId: StopId, favourited: Boolean) {
        this.favourited.value = favourited
        screenModelScope.launch {
            favouriteRepository.setStopFavourite(stopId, favourited)
        }
    }

}

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
        val children by viewModel.children.collectAsStateWithLifecycle()
        val upcoming by viewModel.upcoming.collectAsStateWithLifecycle()
        val alerts by viewModel.alerts.collectAsStateWithLifecycle()
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
                                    item { Box(Modifier.height(1.rdp)) }
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
                                    item { Box(Modifier.height(1.rdp)) }
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
                                item { Box(Modifier.height(1.rdp)) }
                                (children as? RequestState.Success)?.value?.let { children ->
                                    if (stop.visibility.showChildren && children.isNotEmpty()) {
                                        item {
                                            Subheading(stringResource(Res.string.stop_detail_child_stations))
                                        }
                                        items(children) {
                                            StopCard(
                                                it,
                                                onClick = { navigator.push(StopDetailScreen(it.id)) },
                                                showStopIcon = true
                                            )
                                        }
                                        item { Box(Modifier.height(1.rdp)) }
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
    }

    fun LazyListScope.Upcoming(
        viewModel: StopDetailViewModel,
        upcoming: RequestState<List<IStopTimetableTime>>,
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
                            (upcoming.exception as? Exception) ?: Exception(upcoming.exception),
                            retry = { viewModel.retry() }
                        )
                    }
                }
            }

            is RequestState.Success -> {
                item {
                    Subheading(stringResource(Res.string.upcoming_vehicles))
                }
                when {
                    upcoming.value.isNotEmpty() -> items(upcoming.value) {
                        UpcomingRouteCard(
                            it,
                            it.stationTime.pick(it.route),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navigator.push(RouteDetailScreen(
                                it.routeId,
                                it.serviceId,
                                it.tripId,
                                stopId,
                                (it.stationTime.arrival.time as? ReferencedTime)?.startOfDay
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