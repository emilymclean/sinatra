package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.ui.placeCardDefaultNavigation
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.PlaceCard
import cl.emilym.sinatra.ui.widgets.RouteCard
import cl.emilym.sinatra.ui.widgets.StarOutlineIcon
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.createRequestStateFlowFlow
import cl.emilym.sinatra.ui.widgets.handleFlowProperly
import cl.emilym.sinatra.ui.widgets.presentable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.favourites_nothing_favourited
import sinatra.ui.generated.resources.navigation_bar_favourites

@Factory
class FavouriteViewModel(
    private val favouriteRepository: FavouriteRepository
): ScreenModel {

    private val _favourites = createRequestStateFlowFlow<List<Favourite>>()
    val favourites: Flow<RequestState<List<Favourite>>> = _favourites.presentable()

    init {
        retry()
    }

    fun retry() {
        screenModelScope.launch {
            _favourites.handleFlowProperly { favouriteRepository.all() }
        }
    }

}

class FavouriteScreen: Screen {
    override val key: ScreenKey = "favourite"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<FavouriteViewModel>()
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
                    if (favourites.isNotEmpty()) {
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(favourites) {
                                when (it) {
                                    is Favourite.Stop -> StopCard(
                                        it.stop,
                                        onClick = {
                                            navigator.push(StopDetailScreen(it.stop.id))
                                        },
                                        showStopIcon = true
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
                                        },
                                        showStopIcon = true
                                    )

                                    is Favourite.Place -> PlaceCard(
                                        it.place,
                                        modifier = Modifier.fillMaxWidth(),
                                        showPlaceIcon = true,
                                        onClick = { navigator.placeCardDefaultNavigation(it.place) }
                                    )
                                }
                            }
                        }
                    } else {
                        ListHint(
                            stringResource(Res.string.favourites_nothing_favourited)
                        ) {
                            StarOutlineIcon(tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}