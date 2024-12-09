package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScope
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.widgets.PillShape
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MapSearchViewModel(
    private val stopRepository: StopRepository
): ViewModel() {

    val stops = MutableStateFlow<RequestState<List<Stop>>>(RequestState.Initial())

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

    override val bottomSheetHalfHeight: Float
        get() = 0.25f

    @Composable
    override fun Content() {
        Box(Modifier.padding(1.rdp)) {
            Row(
                Modifier
                    .shadow(10.dp, shape = PillShape)
                    .clip(PillShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .padding(0.5.rdp)
            ) {
                Text("Test")
            }
        }
    }

    @Composable
    override fun MapScope.MapContent() {
        Marker(canberra)
        Marker(canberra.copy(lat = canberra.lat + 0.1))
        Line(listOf(
            canberra.copy(lat = canberra.lat - 0.5, lng = canberra.lng - 0.5),
            canberra.copy(lat = canberra.lat + 0.5, lng = canberra.lng + 0.5)
        ))
    }

    @Composable
    override fun BottomSheetContent() {
        Navigator(RouteListScreen())
    }

}