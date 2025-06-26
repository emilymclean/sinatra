package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.map
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.models.NavigationObject
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.specialType
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.domain.search.SearchType
import cl.emilym.sinatra.ui.label
import cl.emilym.sinatra.ui.placeCardDefaultNavigation
import cl.emilym.sinatra.ui.presentation.screens.maps.route.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.stop.StopDetailScreen
import cl.emilym.sinatra.ui.retryIfNeeded
import cl.emilym.sinatra.ui.widgets.ClearIcon
import cl.emilym.sinatra.ui.widgets.FavouriteCard
import cl.emilym.sinatra.ui.widgets.HomeIcon
import cl.emilym.sinatra.ui.widgets.ListCard
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.QuickSelectCard
import cl.emilym.sinatra.ui.widgets.SearchWidget
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import cl.emilym.sinatra.ui.widgets.StarOutlineIcon
import cl.emilym.sinatra.ui.widgets.WorkIcon
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.defaultConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.favourites_no_home
import sinatra.ui.generated.resources.favourites_no_work
import sinatra.ui.generated.resources.favourites_nothing_favourited
import sinatra.ui.generated.resources.navigation_bar_favourites
import sinatra.ui.generated.resources.favourites_clear_favourite

sealed interface FavouriteState {
    data object Favourite: FavouriteState
    data class Search(
        val type: SpecialFavouriteType,
        val hasExisting: Boolean
    ): FavouriteState
}

data class SpecialFavourite(
    val type: SpecialFavouriteType,
    val favourite: Favourite?
)

@Factory
class FavouriteViewModel(
    private val favouriteRepository: FavouriteRepository
): SinatraScreenModel {

    companion object {
        private val SPECIAL_ORDER = listOf(SpecialFavouriteType.HOME, SpecialFavouriteType.WORK)
        private val SPECIAL_ORDER_EMPTY = SPECIAL_ORDER.map { SpecialFavourite(it, null) }
    }

    private val allFavourites = flatRequestStateFlow(defaultConfig) { favouriteRepository.all() }
    private val searchType = MutableStateFlow<SpecialFavouriteType?>(null)

    val anyFavourites = allFavourites.mapLatest {
        it.unwrap()?.isNotEmpty() ?: true
    }.state(true)
    val favourites = allFavourites.mapLatest {
        it.map {
            it.filter { it.specialType == null }
        }
    }.state(RequestState.Initial())
    val special = allFavourites.mapLatest {
        val specials = it.unwrap(listOf()).filter { it.specialType != null }
        SPECIAL_ORDER.map { type ->
            SpecialFavourite(
                type,
                specials.firstOrNull { it.specialType == type }
            )
        }
    }.state(listOf())

    val state = searchType.flatMapLatest { searchType ->
        when (searchType) {
            null -> flowOf(FavouriteState.Favourite)
            else -> special.mapLatest {
                FavouriteState.Search(
                    searchType,
                    it.firstOrNull { it.type == searchType }?.favourite != null
                )
            }
        }
    }.state(FavouriteState.Favourite)

    fun retry() {
        screenModelScope.launch { allFavourites.retryIfNeeded(favourites.value) }
    }

    fun openSearch(type: SpecialFavouriteType) {
        searchType.value = type
    }

    fun closeSearch() {
        searchType.value = null
    }

    fun selectSpecialFavourite(favourite: NavigationObject?) {
        val type = (state.value as? FavouriteState.Search)?.type ?: return

        MainScope().launch {
            closeSearch()
            withContext(Dispatchers.IO) {
                when (favourite) {
                    is Stop -> favouriteRepository.setStopFavourite(favourite.id, true, type)
                    is Place -> favouriteRepository.setPlaceFavourite(favourite.id, true, type)
                    null -> favouriteRepository.clearSpecial(type)
                }
            }
        }
    }

}

class FavouriteScreen: Screen {
    override val key: ScreenKey = "favourite"

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<FavouriteViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        when (state) {
            is FavouriteState.Favourite -> FavouriteContent()
            is FavouriteState.Search -> SearchWidget(
                listOf(SearchType.STOP, SearchType.PLACE),
                onBackPressed = { viewModel.closeSearch() },
                onStopPressed = {
                    viewModel.selectSpecialFavourite(it)
                },
                onPlacePressed = {
                    viewModel.selectSpecialFavourite(it)
                },
                onRoutePressed = {},
                extraPlaceholderContent = {
                    if ((state as? FavouriteState.Search)?.hasExisting == true) {
                        item {
                            ListCard(
                                { ClearIcon() },
                                Modifier.fillMaxWidth(),
                                { viewModel.selectSpecialFavourite(null) },
                                hideForwardIcon = true
                            ) {
                                Text(stringResource(Res.string.favourites_clear_favourite),)
                            }
                            Spacer(Modifier.height(1.rdp))
                        }
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun FavouriteContent() {
        val viewModel = koinScreenModel<FavouriteViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.navigation_bar_favourites)) }
                )
            }
        ) { internalPadding ->
            val favourites by viewModel.favourites.collectAsStateWithLifecycle()
            val anyFavourites by viewModel.anyFavourites.collectAsStateWithLifecycle()
            Box(
                Modifier.fillMaxSize().padding(internalPadding),
                contentAlignment = Alignment.Center
            ) {
                RequestStateWidget(
                    favourites,
                    retry = { viewModel.retry() }
                ) { favourites ->
                    LazyColumn(Modifier.fillMaxSize()) {
                        item {
                            val specials by viewModel.special.collectAsStateWithLifecycle()
                            LazyRow(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(1.rdp, Alignment.CenterHorizontally),
                                contentPadding = PaddingValues(horizontal = 1.rdp)
                            ) {
                                items(
                                    specials,
                                    { it.type }
                                ) {
                                    SpecialFavouriteWidget(
                                        it,
                                        onClick = {
                                            if (it.favourite == null) {
                                                viewModel.openSearch(it.type)
                                            } else {
                                                it.favourite.navigate(navigator)
                                            }
                                        },
                                        onLongClick = if (it.favourite != null) {
                                            { viewModel.openSearch(it.type) }
                                        } else null,
                                        modifier = Modifier.animateItem()
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(1.rdp))
                        }
                        if (anyFavourites) {
                            items(favourites) {
                                FavouriteCard(
                                    it,
                                    onClick = { it.navigate(navigator) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            item {
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
    }

    private fun Favourite.navigate(navigator: Navigator) {
        when (this) {
            is Favourite.Stop -> navigator.push(StopDetailScreen(stop.id))
            is Favourite.Route -> navigator.push(RouteDetailScreen(route.id))
            is Favourite.StopOnRoute -> navigator.push(StopDetailScreen(stop.id))
            is Favourite.Place -> navigator.placeCardDefaultNavigation(place)
        }
    }

}

@Composable
fun SpecialFavouriteType.Icon() = when (this) {
        SpecialFavouriteType.HOME -> HomeIcon()
        SpecialFavouriteType.WORK -> WorkIcon()
    }

val SpecialFavouriteType.label
    @Composable
    get() = when (this) {
        SpecialFavouriteType.HOME -> stringResource(Res.string.favourites_no_home)
        SpecialFavouriteType.WORK -> stringResource(Res.string.favourites_no_work)
    }

@Composable
fun SpecialFavouriteWidget(
    special: SpecialFavourite,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    QuickSelectCard(
        onClick,
        onLongClick,
        modifier
    ) {
        special.type.Icon()
        Text(
            when {
                special.favourite != null -> special.favourite.label
                else -> special.type.label
            }
        )
    }
}