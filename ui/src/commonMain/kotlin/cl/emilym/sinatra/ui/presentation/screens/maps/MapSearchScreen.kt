package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.maps.LocationProvider
import cl.emilym.sinatra.ui.maps.protectedCurrentLocation
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScope
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.MyLocationIcon
import cl.emilym.sinatra.ui.widgets.PillShape
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetValue
import cl.emilym.sinatra.ui.widgets.screenHeight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.KoinApplication.Companion.init

@KoinViewModel
class MapSearchViewModel(
    private val stopRepository: StopRepository,
    private val locationProvider: LocationProvider,
): ViewModel() {

    val stops = MutableStateFlow<RequestState<List<Stop>>>(RequestState.Initial())
    val currentLocation = locationProvider.protectedCurrentLocation

    init {
        retry()
    }

    fun retry() {
        viewModelScope.launch {
            stops.handle {
                stopRepository.stops().item
            }
        }
    }

}

class MapSearchScreen: MapScreen {
    override val key: ScreenKey = this::class.qualifiedName!!

    override val bottomSheetHalfHeight: Float
        get() = 0.25f

    @Composable
    override fun Content() {
        val viewModel = koinViewModel<MapSearchViewModel>()
        val mapControl = LocalMapControl.current
        val currentLocation by viewModel.currentLocation.collectAsState(null)

        var hasZoomedToPerson by mutableStateOf(false)
        LaunchedEffect(Unit) {
            viewModel.currentLocation.collectLatest {
                if (!hasZoomedToPerson && it != null) {
                    mapControl.zoomToPoint(it)
                    hasZoomedToPerson = true
                }
            }
        }

        ConstraintLayout(Modifier.fillMaxSize()) {
            val padding = 1.rdp
            val halfScreen = screenHeight() * bottomSheetHalfHeight
            val (searchBarRef, locationButtonRef) = createRefs()

            Row(
                Modifier
                    .shadow(10.dp, shape = PillShape)
                    .clip(PillShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(0.5.rdp)
                    .constrainAs(searchBarRef) {
                        top.linkTo(parent.top, padding)
                        start.linkTo(parent.start, padding)
                        end.linkTo(parent.end, padding)
                        width = Dimension.fillToConstraints
                    }
            ) {
                Text("Test")
            }

            currentLocation?.let {
                FloatingActionButton(
                    onClick = {
                        mapControl.zoomToPoint(it)
                    },
                    modifier = Modifier.constrainAs(locationButtonRef) {
                        end.linkTo(parent.end, padding)
                        bottom.linkTo(parent.bottom, halfScreen + padding)
                    }
                ) { MyLocationIcon() }
            }
        }
    }

    @Composable
    override fun MapScope.MapContent() {
    }

    @Composable
    override fun BottomSheetContent() {
        Navigator(RouteListScreen())
    }

}