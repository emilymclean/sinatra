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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.bounds
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteTripInformation
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.domain.CurrentTripForRouteUseCase
import cl.emilym.sinatra.domain.CurrentTripInformation
import cl.emilym.sinatra.ui.color
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScope
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.presentation.theme.defaultLineColor
import cl.emilym.sinatra.ui.widgets.AccessibilityIconLockup
import cl.emilym.sinatra.ui.widgets.BikeIcon
import cl.emilym.sinatra.ui.widgets.RouteLine
import cl.emilym.sinatra.ui.widgets.RouteRandle
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.WheelchairAccessibleIcon
import cl.emilym.sinatra.ui.widgets.toIntPx
import kotlinx.coroutines.flow.MutableStateFlow
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
import sinatra.ui.generated.resources.accessibility_title
import sinatra.ui.generated.resources.route_not_found
import sinatra.ui.generated.resources.trip_not_found

@KoinViewModel
class RouteDetailViewModel(
    private val currentTripForRouteUseCase: CurrentTripForRouteUseCase
): ViewModel() {
    val tripInformation = MutableStateFlow<RequestState<CurrentTripInformation?>>(RequestState.Initial())

    fun retry(routeId: RouteId, serviceId: ServiceId?, tripId: TripId?) {
        viewModelScope.launch {
            tripInformation.handle {
                currentTripForRouteUseCase(routeId, serviceId, tripId).item
            }
        }
    }

}

class RouteDetailScreen(
    private val routeId: RouteId,
    private val serviceId: ServiceId? = null,
    private val tripId: TripId? = null
): MapScreen {

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
                    else -> TripDetails(tripInformation.route, tripInformation.tripInformation!!)
                }
            }
        }
    }

    @Composable
    fun TripDetails(route: Route, info: RouteTripInformation) {
        val navigator = LocalNavigator.currentOrThrow
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
                    RouteRandle(route)
                    Text(
                        route.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            item { Box(Modifier.height(0.5.rdp)) }
            item { RouteLine(route, info.stops.mapNotNull { it.stop }) }
            item { Box(Modifier.height(2.rdp)) }
            item {
                Row(
                    Modifier.padding(horizontal = 1.rdp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(1.rdp)
                ) {
                    Text(
                        stringResource(Res.string.accessibility_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
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
            item {
                Row(
                    Modifier.padding(horizontal = 1.rdp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(1.rdp)
                ) {
                    Text(
                        stringResource(Res.string.stops_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            items(info.stops) {
                if (it.stop == null) return@items
                StopCard(
                    it.stop!!,
                    it.arrivalTime?.let { StationTime.Scheduled(it) },
                    Modifier.fillMaxWidth(),
                    onClick = {
                        navigator.push(StopDetailScreen(
                            it.stopId
                        ))
                    }
                )
            }
            item { Box(Modifier.height(2.rdp)) }
        }
    }

    @Composable
    override fun MapScope.MapContent() {
        val viewModel = koinViewModel<RouteDetailViewModel>()
        val tripInformationRS by viewModel.tripInformation.collectAsState(RequestState.Initial())
        val info = (tripInformationRS as? RequestState.Success)?.value ?: return
        val route = info.route
        val stops = info.tripInformation?.stops ?: return
        if (stops.all { it.stop == null }) return

        val zoomPadding = with(LocalDensity.current) { 8.rdp.toIntPx() }

        LaunchedEffect(stops) {
            zoomToArea(stops.mapNotNull { it.stop?.location }.bounds(), zoomPadding)
        }

        Line(
            stops.mapNotNull { it.stop?.location },
            route.colors?.color() ?: defaultLineColor()
        )
//        for (stop in stops) {
//            Marker(stop.stop?.location ?: continue)
//        }
    }
}