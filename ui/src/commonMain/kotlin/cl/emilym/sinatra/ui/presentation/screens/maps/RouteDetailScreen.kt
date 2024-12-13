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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.bounds
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteTripInformation
import cl.emilym.sinatra.data.models.RouteTripStop
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.data.repository.startOfDay
import cl.emilym.sinatra.domain.CurrentTripForRouteUseCase
import cl.emilym.sinatra.domain.CurrentTripInformation
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.asInstants
import cl.emilym.sinatra.ui.color
import cl.emilym.sinatra.ui.current
import cl.emilym.sinatra.ui.maps.LineItem
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MapScope
import cl.emilym.sinatra.ui.maps.MarkerItem
import cl.emilym.sinatra.ui.maps.routeStopMarkerIcon
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.past
import cl.emilym.sinatra.ui.widgets.AccessibilityIconLockup
import cl.emilym.sinatra.ui.widgets.BikeIcon
import cl.emilym.sinatra.ui.widgets.FavouriteButton
import cl.emilym.sinatra.ui.widgets.LocalClock
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.LocalScheduleTimeZone
import cl.emilym.sinatra.ui.widgets.RouteLine
import cl.emilym.sinatra.ui.widgets.RouteRandle
import cl.emilym.sinatra.ui.widgets.SheetIosBackButton
import cl.emilym.sinatra.ui.widgets.SpecificRecomposeOnInstants
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.Subheading
import cl.emilym.sinatra.ui.widgets.WheelchairAccessibleIcon
import cl.emilym.sinatra.ui.widgets.toIntPx
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.route_accessibility_bikes_allowed
import sinatra.ui.generated.resources.route_accessibility_no_bikes_allowed
import sinatra.ui.generated.resources.route_accessibility_not_wheelchair_accessible
import sinatra.ui.generated.resources.route_accessibility_wheelchair_accessible
import sinatra.ui.generated.resources.stops_title
import sinatra.ui.generated.resources.current_stops_title
import sinatra.ui.generated.resources.past_stops_title
import sinatra.ui.generated.resources.accessibility_title
import sinatra.ui.generated.resources.route_not_found
import sinatra.ui.generated.resources.trip_not_found
import sinatra.ui.generated.resources.route_heading

@KoinViewModel
class RouteDetailViewModel(
    private val currentTripForRouteUseCase: CurrentTripForRouteUseCase,
    private val favouriteRepository: FavouriteRepository,
    private val recentVisitRepository: RecentVisitRepository
): ViewModel() {

    val tripInformation = MutableStateFlow<RequestState<CurrentTripInformation?>>(RequestState.Initial())
    val favourited = MutableStateFlow(false)

    fun init(routeId: RouteId) {
        viewModelScope.launch {
            favourited.emitAll(favouriteRepository.routeIsFavourited(routeId))
        }
        viewModelScope.launch {
            recentVisitRepository.addRouteVisit(routeId)
        }
    }

    fun retry(routeId: RouteId, serviceId: ServiceId?, tripId: TripId?) {
        viewModelScope.launch {
            tripInformation.handle {
                currentTripForRouteUseCase(routeId, serviceId, tripId).item
            }
        }
    }

    fun favourite(routeId: RouteId, favourited: Boolean) {
        this.favourited.value = favourited
        viewModelScope.launch {
            favouriteRepository.setRouteFavourite(routeId, favourited)
        }
    }

}

class RouteDetailScreen(
    private val routeId: RouteId,
    private val serviceId: ServiceId? = null,
    private val tripId: TripId? = null
): MapScreen {
    override val key: ScreenKey = "${this::class.qualifiedName!!}/$routeId/$serviceId/$tripId"

    @Composable
    override fun Content() {}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinViewModel<RouteDetailViewModel>()
        val bottomSheetState = LocalBottomSheetState.current

        LaunchedEffect(bottomSheetState) {
            bottomSheetState.bottomSheetState.halfExpand()
        }

        LaunchedEffect(routeId) {
            viewModel.init(routeId)
        }

        LaunchedEffect(routeId, serviceId, tripId) {
            viewModel.retry(routeId, serviceId, tripId)
        }

        val tripInformation by viewModel.tripInformation.collectAsState(RequestState.Initial())
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            RequestStateWidget(tripInformation, { viewModel.retry(routeId, serviceId, tripId) }) { tripInformation ->
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
    fun TripDetails(route: Route, info: RouteTripInformation, trigger: Int?) {
        val viewModel = koinViewModel<RouteDetailViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val clock = LocalClock.current
        val timeZone = LocalScheduleTimeZone.current
        val mapControl = LocalMapControl.current

        val current = if (trigger != null) {
            remember(trigger) {
                val now = clock.now()
                info.stops.current(now, now.startOfDay(timeZone)).nullIfEmpty()
            }
        } else {
            info.stops
        }
        val past = if (trigger != null) {
            remember(trigger) {
                val now = clock.now()
                info.stops.past(now, now.startOfDay(timeZone)).nullIfEmpty()?.reversed()
            }
        } else {
            null
        }

        val zoomPadding = 4.rdp.toIntPx()
        LaunchedEffect(info.stops) {
            mapControl.zoomToArea(info.stops.mapNotNull { it.stop?.location }.bounds(), zoomPadding)
        }

        LazyColumn(
            Modifier.fillMaxSize()
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
                    Column(Modifier.weight(1f)) {
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
                    FavouriteButton(favourited, { viewModel.favourite(routeId, it) })
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

    private fun LazyListScope.Cards(
        navigator: Navigator,
        stops: List<RouteTripStop>
    ) {
        items(stops) {
            if (it.stop == null) return@items
            StopCard(
                it.stop!!,
                Modifier.fillMaxWidth(),
                it.arrivalTime?.let { StationTime.Scheduled(it) },
                onClick = {
                    navigator.push(StopDetailScreen(
                        it.stopId
                    ))
                }
            )
        }
    }

    @Composable
    override fun MapScope.mapItems(): List<MapItem> {
        val viewModel = koinViewModel<RouteDetailViewModel>()
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
                icon,
                id = "routeDetail-${it.stopId}",
            )
        }
    }

}