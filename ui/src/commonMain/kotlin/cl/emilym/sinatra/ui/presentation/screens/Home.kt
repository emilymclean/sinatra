package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.handle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.KoinApplication.Companion.init

@KoinViewModel
class HomeViewModel(
    private val stopRepository: StopRepository
): ViewModel() {

    val stops = MutableStateFlow<RequestState<List<Stop>>>(RequestState.Initial())
    val cached = MutableStateFlow(false)

    init {
        retry()
    }

    fun retry() {
        viewModelScope.launch {
            stops.handle {
                stopRepository.stops().also {
                    cached.value = !it.state.live
                }.item
            }
        }
    }

}

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val homeViewModel = koinViewModel<HomeViewModel>()

        Scaffold { innerPadding ->
            Column(Modifier.padding(innerPadding)) {
                val stops by homeViewModel.stops.collectAsState()
                val cached by homeViewModel.cached.collectAsState()
                RequestStateWidget(stops) { stops ->
                    Text("There are ${stops.size} stops (cached = ${cached}, name of first = ${stops.firstOrNull()?.name})")
                }
            }
        }
    }
}