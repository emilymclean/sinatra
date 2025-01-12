package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.bounds
import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.IRouteTripInformation
import cl.emilym.sinatra.data.models.IRouteTripStop
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.data.repository.AlertRepository
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.domain.CurrentTripForRouteUseCase
import cl.emilym.sinatra.domain.CurrentTripInformation
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.NEAREST_STOP_RADIUS
import cl.emilym.sinatra.ui.asInstants
import cl.emilym.sinatra.ui.color
import cl.emilym.sinatra.ui.current
import cl.emilym.sinatra.ui.maps.LineItem
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.highlightedRouteStopMarkerIcon
import cl.emilym.sinatra.ui.maps.routeStopMarkerIcon
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.past
import cl.emilym.sinatra.ui.text
import cl.emilym.sinatra.ui.widgets.AccessibilityIconLockup
import cl.emilym.sinatra.ui.widgets.AlertScaffold
import cl.emilym.sinatra.ui.widgets.BikeIcon
import cl.emilym.sinatra.ui.widgets.FavouriteButton
import cl.emilym.sinatra.ui.localization.LocalClock
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.localization.LocalScheduleTimeZone
import cl.emilym.sinatra.ui.widgets.RouteLine
import cl.emilym.sinatra.ui.widgets.RouteRandle
import cl.emilym.sinatra.ui.widgets.SheetIosBackButton
import cl.emilym.sinatra.ui.widgets.SpecificRecomposeOnInstants
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.Subheading
import cl.emilym.sinatra.ui.widgets.WheelchairAccessibleIcon
import cl.emilym.sinatra.ui.widgets.createRequestStateFlowFlow
import cl.emilym.sinatra.ui.widgets.currentLocation
import cl.emilym.sinatra.ui.widgets.handleFlowProperly
import cl.emilym.sinatra.ui.widgets.pick
import cl.emilym.sinatra.ui.widgets.presentable
import cl.emilym.sinatra.ui.widgets.toIntPx
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.accessibility_title
import sinatra.ui.generated.resources.current_stops_title
import sinatra.ui.generated.resources.past_stops_title
import sinatra.ui.generated.resources.route_accessibility_bikes_allowed
import sinatra.ui.generated.resources.route_accessibility_no_bikes_allowed
import sinatra.ui.generated.resources.route_accessibility_not_wheelchair_accessible
import sinatra.ui.generated.resources.route_accessibility_wheelchair_accessible
import sinatra.ui.generated.resources.route_heading
import sinatra.ui.generated.resources.route_not_found
import sinatra.ui.generated.resources.stop_detail_distance
import sinatra.ui.generated.resources.stop_detail_nearest_stop
import sinatra.ui.generated.resources.stops_title
import sinatra.ui.generated.resources.trip_not_found

val zoomPadding
    @Composable
    get() = 4.rdp.toIntPx()

@Factory
class RouteDetailViewModel(
    private val currentTripForRouteUseCase: CurrentTripForRouteUseCase,
    private val favouriteRepository: FavouriteRepository,
    private val recentVisitRepository: RecentVisitRepository,
    private val alertRepository: AlertRepository,
    private val clock: Clock
): ScreenModel {

    private var lastLocation = MutableStateFlow<MapLocation?>(null)
    private val _tripInformation = createRequestStateFlowFlow<CurrentTripInformation?>()
    val tripInformation = _tripInformation.presentable()
    val favourited = MutableStateFlow(false)
    val nearestStop: Flow<StopWithDistance?> = tripInformation.combine(lastLocation) { tripInformation, lastLocation ->
        if (tripInformation !is RequestState.Success || lastLocation == null) return@combine null
        val stops = tripInformation.value?.tripInformation?.stops?.mapNotNull { it.stop }?.nullIfEmpty() ?: return@combine null
        stops.map { StopWithDistance(it, distance(lastLocation, it.location)) }
            .filter { it.distance < NEAREST_STOP_RADIUS }
            .nullIfEmpty()
            ?.minBy { it.distance }
    }

    private val _alerts = createRequestStateFlowFlow<List<Alert>>()
    val alerts = _alerts.presentable()

    fun init(
        routeId: RouteId,
        serviceId: ServiceId?,
        tripId: TripId?,
        referenceTime: Instant?
    ) {
        screenModelScope.launch {
            favourited.emitAll(favouriteRepository.routeIsFavourited(routeId))
        }
        screenModelScope.launch {
            recentVisitRepository.addRouteVisit(routeId)
        }
        retry(routeId, serviceId, tripId, referenceTime)
        retryAlerts(routeId, serviceId, tripId)
    }

    fun retry(routeId: RouteId, serviceId: ServiceId?, tripId: TripId?, referenceTime: Instant?) {
        screenModelScope.launch {
            _tripInformation.handleFlowProperly {
                currentTripForRouteUseCase(
                    routeId,
                    serviceId,
                    tripId,
                    referenceTime ?: clock.now()
                ).map { it.item }
            }
        }
    }

    fun retryAlerts(routeId: RouteId, serviceId: ServiceId?, tripId: TripId?) {
        screenModelScope.launch {
            _alerts.handleFlowProperly {
                alertRepository.alerts(routeId = routeId, tripId = tripId)
            }
        }
    }

    fun updateLocation(location: MapLocation) {
        lastLocation.value = location
    }

    fun favourite(routeId: RouteId, favourited: Boolean) {
        this.favourited.value = favourited
        screenModelScope.launch {
            favouriteRepository.setRouteFavourite(routeId, favourited)
        }
    }

}

class RouteDetailScreen(
    private val routeId: RouteId,
    private val serviceId: ServiceId? = null,
    private val tripId: TripId? = null,
    private val stopId: StopId? = null,
    private val startOfDay: Instant? = null
): MapScreen {
    override val key: ScreenKey = "${this::class.qualifiedName!!}/$routeId/$serviceId/$tripId/$stopId"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinScreenModel<RouteDetailViewModel>()
        val bottomSheetState = LocalBottomSheetState.current

        LaunchedEffect(bottomSheetState) {
            bottomSheetState?.bottomSheetState?.halfExpand()
        }

        LaunchedEffect(routeId, serviceId, tripId, startOfDay) {
            viewModel.init(routeId, serviceId, tripId, startOfDay)
        }

        if (FeatureFlags.ROUTE_DETAIL_NEAREST_STOP) {
            val currentLocation = currentLocation()
            LaunchedEffect(currentLocation) {
                viewModel.updateLocation(currentLocation ?: return@LaunchedEffect)
            }
        }

        val tripInformation by viewModel.tripInformation.collectAsState(RequestState.Initial())
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            RequestStateWidget(tripInformation, { viewModel.retry(routeId, serviceId, tripId, startOfDay) }) { tripInformation ->
                when {
                    tripInformation == null -> { Text(stringResource(Res.string.route_not_found)) }
                    tripInformation.tripInformation == null -> { Text(stringResource(Res.string.trip_not_found)) }
                    else -> {
                        val info = tripInformation.tripInformation!!
                        val triggers = info.stationTimes?.asInstants()
                        when {
                            triggers != null -> {
                                SpecificRecomposeOnInstants(triggers) { trigger ->
                                    TripDetails(tripInformation.route, info, trigger)
                                }
                            }
                            else -> {
                                TripDetails(tripInformation.route, info, null)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TripDetails(route: Route, info: IRouteTripInformation, trigger: Int?) {
        val viewModel = koinScreenModel<RouteDetailViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val clock = LocalClock.current
        val timeZone = LocalScheduleTimeZone.current
        val mapControl = LocalMapControl.current

        val nearestStop by viewModel.nearestStop.collectAsState(null)
        val alerts by viewModel.alerts.collectAsState(RequestState.Initial())

        val current = if (trigger != null) {
            remember(trigger) {
                val now = clock.now()
                info.stops.current(now, startOfDay ?: now.startOfDay(timeZone)).nullIfEmpty()
            }
        } else {
            info.stops
        }
        val past = if (trigger != null) {
            remember(trigger) {
                val now = clock.now()
                info.stops.past(now, startOfDay ?: now.startOfDay(timeZone)).nullIfEmpty()?.reversed()
            }
        } else {
            null
        }

        val zoomPadding = zoomPadding
        LaunchedEffect(info.stops) {
            if (FeatureFlags.ROUTE_DETAIL_PREVENT_ZOOM_WHEN_HAVE_SOURCE_STOP && stopId != null)
                return@LaunchedEffect
            mapControl.zoomToArea(info.stops.mapNotNull { it.stop?.location }.bounds(), zoomPadding)
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
                        RouteRandle(route)
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Text(
                                route.name,
                                style = MaterialTheme.typography.titleLarge
                            )
                            info.heading?.let {
                                Text(
                                    stringResource(Res.string.route_heading, it),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        val favourited by viewModel.favourited.collectAsState(false)
                        FavouriteButton(
                            favourited,
                            { viewModel.favourite(routeId, it) },
                            Modifier.semantics {
                                contentDescription = "Favourite route"
                                selected = favourited
                            }
                        )
                    }
                }
                item { Box(Modifier.height(0.5.rdp)) }
                item { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    RouteLine(
                        route,
                        info.stops.mapNotNull { it.stop },
                        info.stops.mapNotNull { it.stationTime }.nullIfEmpty()
                    )
                } }
                item { Box(Modifier.height(2.rdp)) }
                item {
                    AlertScaffold((alerts as? RequestState.Success)?.value)
                }
                item {
                    Subheading(stringResource(Res.string.accessibility_title))
                }
                item { Box(Modifier.height(1.rdp)) }
                item {
                    Column(Modifier.padding(horizontal = 1.rdp)) {
                        AccessibilityIconLockup(
                            {
                                WheelchairAccessibleIcon(info.accessibility.wheelchairAccessible == ServiceWheelchairAccessible.ACCESSIBLE)
                            }
                        ) {
                            Text(when(info.accessibility.wheelchairAccessible == ServiceWheelchairAccessible.ACCESSIBLE) {
                                true -> stringResource(Res.string.route_accessibility_wheelchair_accessible)
                                false -> stringResource(Res.string.route_accessibility_not_wheelchair_accessible)
                            })
                        }
                        AccessibilityIconLockup(
                            { BikeIcon() }
                        ) {
                            Text(when(info.accessibility.bikesAllowed == ServiceBikesAllowed.ALLOWED) {
                                true -> stringResource(Res.string.route_accessibility_bikes_allowed)
                                false -> stringResource(Res.string.route_accessibility_no_bikes_allowed)
                            })
                        }
                    }
                }
                item { Box(Modifier.height(1.rdp)) }
                nearestStop?.let { nearestStop ->
                    if (!FeatureFlags.ROUTE_DETAIL_NEAREST_STOP) return@let
                    item {
                        Column {
                            Subheading(stringResource(Res.string.stop_detail_nearest_stop))
                            StopCard(
                                nearestStop.stop,
                                Modifier.fillMaxWidth(),
                                onClick = {
                                    navigator.push(StopDetailScreen(
                                        nearestStop.stop.id
                                    ))
                                },
                                subtitle = stringResource(Res.string.stop_detail_distance, nearestStop.distance.text)
                            )
                        }
                    }
                }
                when {
                    trigger == null -> {
                        item { Subheading(stringResource(Res.string.stops_title)) }
                        Cards(navigator, current ?: listOf())
                    }
                    else -> {
                        if (current != null) {
                            item { Subheading(stringResource(Res.string.current_stops_title)) }
                            Cards(navigator, current ?: listOf())
                        }
                        if (past != null) {
                            item { Subheading(stringResource(Res.string.past_stops_title)) }
                            Cards(navigator, past ?: listOf())
                        }
                    }
                }
                item { Box(Modifier.height(2.rdp)) }
            }
        }
    }

    private fun LazyListScope.Cards(
        navigator: Navigator,
        stops: List<IRouteTripStop>
    ) {
        items(stops) {
            if (it.stop == null) return@items
            StopCard(
                it.stop!!,
                Modifier.fillMaxWidth(),
                it.stationTime?.pick(),
                onClick = {
                    navigator.push(StopDetailScreen(
                        it.stopId
                    ))
                }
            )
        }
    }

    @Composable
    override fun mapItems(): List<MapItem> {
        val viewModel = koinScreenModel<RouteDetailViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val tripInformationRS by viewModel.tripInformation.collectAsState(RequestState.Initial())
        val info = (tripInformationRS as? RequestState.Success)?.value ?: return listOf()
        val route = info.route
        val icon = routeStopMarkerIcon(route)
        val stops = info.tripInformation?.stops ?: return listOf()
        if (stops.all { it.stop == null }) return listOf()

        return listOf(
            LineItem(
                stops.mapNotNull { it.stop?.location },
                route.color()
            )
        ) + stops.mapNotNull {
            MarkerItem(
                it.stop?.location ?: return@mapNotNull null,
                when (it.stopId == stopId && FeatureFlags.ROUTE_DETAIL_HIGHLIGHT_SOURCE_STOP) {
                    true -> highlightedRouteStopMarkerIcon(route, it.stop)
                    false -> icon
                },
                id = "routeDetail-${it.stopId}",
                onClick = when (FeatureFlags.ROUTE_DETAIL_CLICKABLE_STOPS) {
                    true -> { { navigator.push(StopDetailScreen(it.stopId)) } }
                    false -> null
                }
            )
        }
    }

}