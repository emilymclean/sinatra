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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.UpcomingRoutesForStopUseCase
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScope
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.widgets.AccessibilityIconLockup
import cl.emilym.sinatra.ui.widgets.UpcomingRouteCard
import cl.emilym.sinatra.ui.widgets.WheelchairAccessibleIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.stop_not_found
import sinatra.ui.generated.resources.upcoming_vehicles
import sinatra.ui.generated.resources.accessibility_wheelchair_accessible
import sinatra.ui.generated.resources.accessibility_not_wheelchair_accessible

@KoinViewModel
class StopDetailViewModel(
    private val stopRepository: StopRepository,
    private val upcomingRoutesForStopUseCase: UpcomingRoutesForStopUseCase,
): ViewModel() {

    val stop = MutableStateFlow<RequestState<Stop?>>(RequestState.Initial())
    val upcoming = MutableStateFlow<RequestState<List<StopTimetableTime>>>(RequestState.Initial())

    fun load(stopId: StopId) {
        retryStop(stopId)
        retryUpcoming(stopId)
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
            upcoming.handle {
                upcomingRoutesForStopUseCase(stopId).item
            }
        }
    }

}

class StopDetailScreen(
    val stopId: StopId
): MapScreen {
    override val key: ScreenKey = "${this::class.qualifiedName!!}/$stopId"

    @Composable
    override fun Content() {}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinViewModel<StopDetailViewModel>()
        val bottomSheetState = LocalBottomSheetState.current
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(bottomSheetState) {
            bottomSheetState.bottomSheetState.halfExpand()
        }

        LaunchedEffect(stopId) {
            viewModel.load(stopId)
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
                                    Text(
                                        stop.name,
                                        style = MaterialTheme.typography.titleLarge
                                    )
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
                items(upcoming.value) {
                    UpcomingRouteCard(
                        it,
                        StationTime.Scheduled(it.arrivalTime),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navigator.push(RouteDetailScreen(
                            it.routeId, it.serviceId, it.tripId
                        )) }
                    )
                }
            }
        }
    }

    @Composable
    override fun MapScope.MapContent() {
        val viewModel = koinViewModel<StopDetailViewModel>()
        val stopRS by viewModel.stop.collectAsState(RequestState.Initial())
        val stop = (stopRS as? RequestState.Success)?.value ?: return

        LaunchedEffect(stop.location) {
            zoomToPoint(stop.location)
        }

        Marker(stop.location)
    }
}