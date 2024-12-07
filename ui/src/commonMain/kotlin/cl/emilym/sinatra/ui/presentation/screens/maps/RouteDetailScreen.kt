package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.handle
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteTripInformation
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.domain.CurrentTripForRouteUseCase
import cl.emilym.sinatra.domain.CurrentTripInformation
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.widgets.RouteLine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.route_not_found
import sinatra.ui.generated.resources.trip_not_found

@KoinViewModel
class RouteDetailViewModel(
    private val routeRepository: RouteRepository,
    private val currentTripForRouteUseCase: CurrentTripForRouteUseCase
): ViewModel() {
    val tripInformation = MutableStateFlow<RequestState<CurrentTripInformation?>>(RequestState.Initial())

    fun retry(routeId: RouteId) {
        viewModelScope.launch {
            tripInformation.handle {
                currentTripForRouteUseCase(routeId).item
            }
        }
    }

}

class RouteDetailScreen(
    private val routeId: RouteId
): MapScreen {

    @Composable
    override fun Content() {}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun BottomSheetContent() {
        val viewModel = koinViewModel<RouteDetailViewModel>()
        val bottomSheetState = LocalBottomSheetState.current

        LaunchedEffect(bottomSheetState) {
            bottomSheetState.bottomSheetState.expand()
        }

        LaunchedEffect(routeId) {
            viewModel.retry(routeId)
        }

        val tripInformation by viewModel.tripInformation.collectAsState(RequestState.Initial())
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            RequestStateWidget(tripInformation, { viewModel.retry(routeId) }) { tripInformation ->
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
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            RouteLine(route, info.stops.mapNotNull { it.stop })
        }
    }
}