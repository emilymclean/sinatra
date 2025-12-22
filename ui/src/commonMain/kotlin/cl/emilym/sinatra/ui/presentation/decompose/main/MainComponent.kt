package cl.emilym.sinatra.ui.presentation.decompose.main

import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.presentation.decompose.base.MapComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.base.childSlotFlow
import cl.emilym.sinatra.ui.presentation.decompose.base.childStackFlow
import cl.emilym.sinatra.ui.presentation.decompose.browse.BrowseBottomSheetComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.BrowseMapComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.DefaultBrowseBottomSheetComponent
import cl.emilym.sinatra.ui.presentation.decompose.browse.DefaultBrowseMapComponent
import cl.emilym.sinatra.ui.presentation.decompose.main.tab.DefaultTabBarComponent
import cl.emilym.sinatra.ui.presentation.decompose.main.tab.TabBarComponent
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.Serializable

interface MainComponent: SinatraComponent {

    val content: StateFlow<ChildStack<*, Child>>
    val tabBarContent: StateFlow<ChildSlot<*, TabBarChild>>

    val mapItems: StateFlow<List<MapItem>>

    sealed interface Child {
        data class Browse(
            override val mapComponent: BrowseMapComponent,
            override val bottomSheetComponent: BrowseBottomSheetComponent
        ): MapChild<BrowseMapComponent, BrowseBottomSheetComponent>

        interface MapChild<M: MapComponent, B: SinatraComponent>: Child {
            val mapComponent: M
            val bottomSheetComponent: B
        }

        interface MainChild<T: SinatraComponent>: Child {
            val component: T
        }
    }

    sealed interface TabBarChild {
        data class Main(
            val component: TabBarComponent
        ): TabBarChild
    }

}

class DefaultMainComponent(
    sinatraComponentContext: SinatraComponentContext
): MainComponent, SinatraComponentContext by sinatraComponentContext {

    @Serializable
    private sealed interface TabBarConfig {
        @Serializable
        data object Main: TabBarConfig
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Main: Config
    }

    private val tabBarNavigation = SlotNavigation<TabBarConfig>()
    override val tabBarContent: StateFlow<ChildSlot<*, MainComponent.TabBarChild>> = childSlotFlow(
        source = tabBarNavigation,
        serializer = TabBarConfig.serializer(),
        initialConfiguration = { TabBarConfig.Main },
        handleBackButton = false,
        childFactory = { _, context ->
            MainComponent.TabBarChild.Main(
                DefaultTabBarComponent(context)
            )
        },
    )

    private val navigation = StackNavigation<Config>()
    override val content: StateFlow<ChildStack<*, MainComponent.Child>> = childStackFlow(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Main,
        handleBackButton = true,
        childFactory = ::createChild
    )

    override val mapItems: StateFlow<List<MapItem>> = content.flatMapLatest {
        (it.active.instance as? MainComponent.Child.MapChild<*,*>)?.mapComponent?.mapItems
            ?: flowOf(emptyList())
    }.state(emptyList())

    private fun createChild(config: Config, componentContext: SinatraComponentContext): MainComponent.Child =
        when (config) {
            is Config.Main -> MainComponent.Child.Browse(
                DefaultBrowseMapComponent(componentContext),
                DefaultBrowseBottomSheetComponent(componentContext)
            )
        }

}