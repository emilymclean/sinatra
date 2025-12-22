package cl.emilym.sinatra.ui.presentation.decompose.root

import cl.emilym.sinatra.domain.IsAboveMinimumVersionUseCase
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.base.childSlotFlow
import cl.emilym.sinatra.ui.presentation.decompose.main.DefaultMainComponent
import cl.emilym.sinatra.ui.presentation.decompose.main.MainComponent
import cl.emilym.sinatra.ui.presentation.decompose.outofdate.AppOutOfDateComponent
import cl.emilym.sinatra.ui.presentation.decompose.outofdate.DefaultAppOutOfDateComponent
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.navigate
import com.arkivanov.essenty.lifecycle.doOnCreate
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

interface RootComponent: SinatraComponent {

    val content: StateFlow<ChildSlot<*, Child>>

    sealed interface Child {
        class AppOutOfDateChild(val component: AppOutOfDateComponent): Child
        class Main(val component: MainComponent): Child
    }

}

class DefaultRootComponent(
    sinatraComponentContext: SinatraComponentContext
): RootComponent, SinatraComponentContext by sinatraComponentContext {

    @Serializable
    private sealed interface Config {
        @Serializable
        data object AppOutOfDate : Config
        @Serializable
        data object Main : Config
    }

    private val isAboveMinimumVersionUseCase = sinatraComponentContext.koin.get<IsAboveMinimumVersionUseCase>()

    private val navigation = SlotNavigation<Config>()

    override val content: StateFlow<ChildSlot<*, RootComponent.Child>> =
        childSlotFlow(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = { Config.Main },
            handleBackButton = true,
            childFactory = ::createChild,
        )

    private fun createChild(config: Config, componentContext: SinatraComponentContext): RootComponent.Child =
        when (config) {
            is Config.AppOutOfDate -> RootComponent.Child.AppOutOfDateChild(
                DefaultAppOutOfDateComponent(
                    componentContext
                )
            )
            is Config.Main -> RootComponent.Child.Main(
                DefaultMainComponent(
                    componentContext
                )
            )
        }

    init {
        lifecycle.doOnCreate {
            componentScope.launch {
                if (isAboveMinimumVersionUseCase()) return@launch
                navigation.navigate { Config.AppOutOfDate }
            }
        }
    }

}