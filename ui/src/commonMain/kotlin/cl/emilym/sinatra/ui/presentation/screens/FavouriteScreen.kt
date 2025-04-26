package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.map
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.data.models.specialType
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.ui.label
import cl.emilym.sinatra.ui.placeCardDefaultNavigation
import cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailScreen
import cl.emilym.sinatra.ui.widgets.HomeIcon
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.PlaceCard
import cl.emilym.sinatra.ui.widgets.RouteCard
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import cl.emilym.sinatra.ui.widgets.StarOutlineIcon
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.WorkIcon
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.favourites_no_home
import sinatra.ui.generated.resources.favourites_no_work
import sinatra.ui.generated.resources.favourites_nothing_favourited
import sinatra.ui.generated.resources.navigation_bar_favourites

data class SpecialFavourite(
    val type: SpecialFavouriteType,
    val favourite: Favourite?
)

@Factory
class FavouriteViewModel(
    private val favouriteRepository: FavouriteRepository
): SinatraScreenModel {

    private val allFavourites = flatRequestStateFlow {
        favouriteRepository.all()
    }

    val anyFavourites = allFavourites.mapLatest {
        it.unwrap()?.isNotEmpty() ?: true
    }.state(true)
    val favourites = allFavourites.mapLatest {
        it.map {
            it.filter {
                when (it) {
                    is Favourite.Stop -> it.specialType == null
                    else -> true
                }
            }
        }
    }.state(RequestState.Initial())
    val special = allFavourites.mapLatest {
        val specials = it.unwrap(listOf()).filter { it.specialType != null }
        listOf(SpecialFavouriteType.HOME, SpecialFavouriteType.WORK).map { type ->
            SpecialFavourite(
                type,
                specials.firstOrNull { it.specialType == type }
            )
        }
    }.state(listOf())

    fun retry() {
        screenModelScope.launch {
            allFavourites.retry()
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
                                items(specials) {
                                    SpecialFavouriteWidget(
                                        it,
                                        {},
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(1.rdp))
                        }
                        if (anyFavourites) {
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
}

@Composable
fun SpecialFavouriteWidget(
    special: SpecialFavourite,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable {
                onClick()
            }
            .semantics {
                this.role = Role.Button
            }
            .padding(1.rdp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(1.rdp, Alignment.CenterHorizontally)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface
        ) {
            when (special.type) {
                SpecialFavouriteType.HOME -> HomeIcon()
                SpecialFavouriteType.WORK -> WorkIcon()
            }
            Text(
                when {
                    special.favourite != null -> special.favourite.label
                    else -> when (special.type) {
                        SpecialFavouriteType.HOME -> stringResource(Res.string.favourites_no_home)
                        SpecialFavouriteType.WORK -> stringResource(Res.string.favourites_no_work)
                    }
                }
            )
        }
    }
}