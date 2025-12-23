package cl.emilym.sinatra.ui.presentation.decompose.browse

import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.base.childItemsFlow
import cl.emilym.sinatra.ui.presentation.decompose.browse.BrowseBottomSheetComponent.Item
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.DefaultNearbyDepartureItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.DefaultQuickFavouriteItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.DefaultRouteItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.DefaultServiceUpdateItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.NearbyDepartureItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.QuickFavouriteItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.RouteItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.items.ServiceUpdateItemComponent
import cl.emilym.sinatra.ui.presentation.decompose.main.Navigation
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.items.ChildItems
import com.arkivanov.decompose.router.items.Items
import com.arkivanov.decompose.router.items.ItemsNavigation
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

interface BrowseBottomSheetComponent: SinatraComponent {

    @OptIn(ExperimentalDecomposeApi::class)
    val items: StateFlow<ChildItems<*, Item>>

    sealed interface Item {
        data class Routes(
            val component: RouteItemComponent
        ): Item
        data class QuickFavourite(
            val component: QuickFavouriteItemComponent
        ): Item
        data class NearbyDeparture(
            val component: NearbyDepartureItemComponent
        ): Item
        data class ServiceUpdate(
            val component: ServiceUpdateItemComponent
        ): Item
    }

}

class DefaultBrowseBottomSheetComponent(
    private val onNavigate: (Navigation) -> Unit,
    sinatraComponentContext: SinatraComponentContext
): BrowseBottomSheetComponent, SinatraComponentContext by sinatraComponentContext {

    @OptIn(ExperimentalDecomposeApi::class)
    private val navigation = ItemsNavigation<Config>()
    @OptIn(ExperimentalDecomposeApi::class)
    override val items: StateFlow<ChildItems<*, Item>> = childItemsFlow(
        source = navigation,
        serializer = Config.serializer(),
        initialItems = {
            Items(listOf(Config.Routes))
        },
        childFactory = ::createItem
    )

    private fun createItem(config: Config, context: SinatraComponentContext): Item =
        when (config) {
            is Config.Routes -> Item.Routes(
                DefaultRouteItemComponent(
                    onNavigate,
                    context
                )
            )
            is Config.QuickFavourite -> Item.QuickFavourite(
                DefaultQuickFavouriteItemComponent(
                    onNavigate,
                    context
                )
            )
            is Config.NearbyDeparture -> Item.NearbyDeparture(
                DefaultNearbyDepartureItemComponent(
                    onNavigate,
                    context
                )
            )
            is Config.ServiceUpdate -> Item.ServiceUpdate(
                DefaultServiceUpdateItemComponent(
                    onNavigate,
                    context
                )
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