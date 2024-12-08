package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteTripInformation
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.domain.UpcomingRoutesForStopUseCase
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScope
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.widgets.RouteLine
import cl.emilym.sinatra.ui.widgets.RouteRandle
import cl.emilym.sinatra.ui.widgets.StopCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.stop_not_found

@KoinViewModel
class StopDetailViewModel(
    private val stopRepository: StopRepository,
    private val upcomingRoutesForStopUseCase: UpcomingRoutesForStopUseCase,
): ViewModel() {

    val stop = MutableStateFlow<RequestState<Stop?>>(RequestState.Initial())
    val upcoming = MutableStateFlow<RequestState<List<StopTimetableTime>>>(RequestState.Initial())

    fun retry(stopId: StopId) {
        viewModelScope.launch {
            stop.handle {
                stopRepository.stop(stopId).item
            }
        }
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

    @Composable
    override fun Content() {}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinViewModel<StopDetailViewModel>()
        val bottomSheetState = LocalBottomSheetState.current

        LaunchedEffect(bottomSheetState) {
            bottomSheetState.bottomSheetState.expand()
        }

        LaunchedEffect(stopId) {
            viewModel.retry(stopId)
        }

        val stop by viewModel.stop.collectAsState(RequestState.Initial())
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            RequestStateWidget(stop, { viewModel.retry(stopId) }) { stop ->
                when {
                    stop == null -> {
                        Text(stringResource(Res.string.stop_not_found))
                    }
                    else -> {
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
                                    Text(
                                        stop.name,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                            item { Box(Modifier.height(1.rdp)) }
                        }
                    }
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
            ZoomToPoint(stop.location)
        }

        Marker(stop.location)
    }
}