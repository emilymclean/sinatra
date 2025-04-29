package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.domain.search.SearchType
import cl.emilym.sinatra.ui.widgets.ClearIcon
import cl.emilym.sinatra.ui.widgets.ListCard
import cl.emilym.sinatra.ui.widgets.SearchWidget
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.favourites_clear_favourite

// This is such a hack but IDK
class AddSpecialFavouriteScreen(
    private val type: SpecialFavouriteType
): Screen {
    override val key: ScreenKey = "add-special-favourite-${type.name}"

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<FavouriteViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(type) {
            viewModel.openSearch(type)
        }

        SearchWidget(
            listOf(SearchType.STOP, SearchType.PLACE),
            onBackPressed = { navigator.pop() },
            onStopPressed = {
                viewModel.selectSpecialFavourite(it)
                navigator.pop()
            },
            onPlacePressed = {
                viewModel.selectSpecialFavourite(it)
                navigator.pop()
            },
            onRoutePressed = {},
            extraPlaceholderContent = {
                if ((state as? FavouriteState.Search)?.hasExisting == true) {
                    item {
                        ListCard(
                            { ClearIcon() },
                            Modifier.fillMaxWidth(),
                            {
                                viewModel.selectSpecialFavourite(null)
                                navigator.pop()
                            },
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