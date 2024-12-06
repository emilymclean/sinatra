package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.ui.maps.MapObjects
import cl.emilym.sinatra.ui.presentation.screens.MapsScreen
import cl.emilym.sinatra.ui.widgets.PillShape
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel

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

class MapSearchScreen: MapsScreen() {
    override val needsMapHandle: Boolean = true

    @Composable
    override fun MainContent() {
        val viewModel = koinViewModel<MapSearchViewModel>()

        val stops by viewModel.stops.unwrap(listOf()).collectAsState(listOf())
        MapObjects {
            for (stop in stops) {
                marker(stop.location)
            }
        }

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
}