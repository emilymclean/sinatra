package cl.emilym.sinatra.ui.presentation.decompose.main

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
import kotlinx.serialization.Serializable

interface MainComponent: SinatraComponent {

    val content: StateFlow<ChildStack<*, Child>>
    val tabBarContent: StateFlow<ChildSlot<*, TabBarChild>>

    sealed interface Child {
        data class Browse(
            override val mapComponent: BrowseMapComponent,
            override val bottomSheetComponent: BrowseBottomSheetComponent
        ): MapChild<BrowseMapComponent, BrowseBottomSheetComponent>

        interface MapChild<M: SinatraComponent, B: SinatraComponent>: Child {
            val mapComponent: M
            val bottomSheetComponent: B
        }

        interface PageChild<T: SinatraComponent>: Child {
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

    private fun createChild(config: Config, componentContext: SinatraComponentContext): MainComponent.Child =
        when (config) {
            is Config.Main -> MainComponent.Child.Browse(
                DefaultBrowseMapComponent(componentContext),
                DefaultBrowseBottomSheetComponent(
                    ::navigate,
                    componentContext
                )
            )
        }

    fun navigate(instruction: NavigationInstruction) {

    }

}