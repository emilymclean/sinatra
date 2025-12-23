package cl.emilym.sinatra.ui.presentation.decompose.browse

import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.domain.GetFilteredStopsUseCase
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.base.asStateFlow
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.BrowseItemPlugin
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.DefaultNearbyDepartureItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.DefaultQuickFavouriteItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.DefaultRouteItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.DefaultServiceUpdateItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.main.NavigationInstruction
import cl.emilym.sinatra.ui.widgets.defaultConfig
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.items.ChildItems
import com.arkivanov.decompose.router.items.Items
import com.arkivanov.decompose.router.items.ItemsNavigation
import com.arkivanov.decompose.router.items.childItems
import com.arkivanov.essenty.lifecycle.doOnCreate
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.Serializable

interface BrowseBottomSheetComponent: SinatraComponent {

    val stops: StateFlow<List<Stop>>

    @OptIn(ExperimentalDecomposeApi::class)
    val items: StateFlow<ChildItems<*, BrowseItemPlugin<*>>>

}

@OptIn(ExperimentalDecomposeApi::class)
class DefaultBrowseBottomSheetComponent(
    private val onNavigate: (NavigationInstruction) -> Unit,
    sinatraComponentContext: SinatraComponentContext
): BrowseBottomSheetComponent, SinatraComponentContext by sinatraComponentContext {

    private val getFilteredStopsUseCase: GetFilteredStopsUseCase = koin.get()

    // TODO handle errors
    override val stops: StateFlow<List<Stop>> =
        flatRequestStateFlow(defaultConfig) {
            getFilteredStopsUseCase()
        }
        .unwrap(Cachable.live(emptyList<Stop>()))
        .mapLatest { it.item }
        .state(emptyList())

    @OptIn(ExperimentalDecomposeApi::class)
    private val navigation = ItemsNavigation<Config>()

    private val _items = childItems(
        source = navigation,
        serializer = Config.serializer(),
        initialItems = {
            Items(
                listOf(
                    Config.QuickFavourite,
                    Config.NearbyDeparture,
                    Config.ServiceUpdate,
                    Config.Routes
                )
            )
        },
        childFactory = ::createItem
    )
    @OptIn(ExperimentalDecomposeApi::class)
    override val items: StateFlow<ChildItems<*, BrowseItemPlugin<*>>> = _items.asStateFlow()

    init {
        lifecycle.doOnCreate {
            navigation.navigate(
                transformer = {
                    it.copy(
                        activeItems = it.items.associateWith { Items.ActiveLifecycleState.CREATED }
                    )
                },
                onComplete = { before, after -> }
            )
        }
    }

    private fun createItem(config: Config, context: SinatraComponentContext): BrowseItemPlugin<*> =
        when (config) {
            is Config.Routes -> DefaultRouteItemComponent(
                onNavigate,
                context
            )
            is Config.QuickFavourite -> DefaultQuickFavouriteItemComponent(
                onNavigate,
                context
            )
            is Config.NearbyDeparture -> DefaultNearbyDepartureItemComponent(
                onNavigate,
                context
            )
            is Config.ServiceUpdate -> DefaultServiceUpdateItemComponent(
                onNavigate,
                context
            )
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Routes: Config
        @Serializable
        data object QuickFavourite: Config
        @Serializable
        data object NearbyDeparture: Config
        @Serializable
        data object ServiceUpdate: Config
    }

}