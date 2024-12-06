package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.domain.DisplayRoutesUseCase
import cl.emilym.sinatra.ui.widgets.RouteCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel

@KoinViewModel
class RouteListViewModel(
    val displayRoutesUseCase: DisplayRoutesUseCase
): ViewModel() {

    val routes = MutableStateFlow<RequestState<List<Route>>>(RequestState.Initial())

    init {
        retry()
    }

    fun retry() {
        viewModelScope.launch {
            routes.handle {
                displayRoutesUseCase().item
            }
        }
    }

}

class RouteListScreen: Screen {

    @Composable
    override fun Content() {
        val viewModel = koinViewModel<RouteListViewModel>()

        Scaffold { innerPadding ->
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val routes by viewModel.routes.collectAsState(RequestState.Initial())
                RequestStateWidget(routes, { viewModel.retry() }) { routes ->
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(0.5.rdp)
                    ) {
                        items(routes.size) {
                            RouteCard(routes[it])
                        }
                    }
                }
            }
        }
    }
}