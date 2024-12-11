package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen
import cl.emilym.sinatra.ui.widgets.RouteCard
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.createRequestStateFlowFlow
import cl.emilym.sinatra.ui.widgets.handleFlowProperly
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.navigation_bar_favourites

@KoinViewModel
class FavouriteViewModel(
    private val favouriteRepository: FavouriteRepository
): ViewModel() {

    private val _favourites = createRequestStateFlowFlow<List<Favourite>>()
    val favourites: Flow<RequestState<List<Favourite>>> = _favourites.flatMapLatest { it.map {
        Napier.d("Value = ${(it as? RequestState.Success)?.value}")
        it
    } }

    init {
        retry()
    }

    fun retry() {
        viewModelScope.launch {
            _favourites.handleFlowProperly { favouriteRepository.all() }
        }
    }

}

class FavouriteScreen: Screen {
    override val key: ScreenKey = "favourite"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinViewModel<FavouriteViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.navigation_bar_favourites)) }
                )
            }
        ) { internalPadding ->
            val favourites by viewModel.favourites.collectAsState(RequestState.Initial())
            Box(
                Modifier.fillMaxSize().padding(internalPadding),
                contentAlignment = Alignment.Center
            ) {
                RequestStateWidget(
                    favourites,
                    retry = { viewModel.retry() }
                ) { favourites ->
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(favourites) {
                            when (it) {
                                is Favourite.Stop -> StopCard(
                                    it.stop,
                                    onClick = {
                                        navigator.push(StopDetailScreen(it.stop.id))
                                    }
                                )
                                is Favourite.Route -> RouteCard(
                                    it.route,
                                    onClick = {
                                        navigator.push(RouteDetailScreen(it.route.id))
                                    }
                                )
                                is Favourite.StopOnRoute -> StopCard(
                                    it.stop,
                                    onClick = {
                                        navigator.push(StopDetailScreen(it.stop.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}