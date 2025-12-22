package cl.emilym.sinatra.ui.presentation.decompose.main

import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import com.arkivanov.essenty.instancekeeper.retainedSimpleInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest

interface MainComponent: SinatraComponent {

    val state: StateFlow<MainComponentState>

}

class DefaultMainComponent(
    sinatraComponentContext: SinatraComponentContext
): MainComponent, SinatraComponentContext by sinatraComponentContext {

    companion object {
        private val ALL_TAB_BAR_ITEMS = listOf(
            TabBarItem.MAP,
            TabBarItem.NAVIGATE,
            TabBarItem.FAVOURITES,
            TabBarItem.MORE
        )
    }

    private val remoteConfigRepository = sinatraComponentContext.koin.get<RemoteConfigRepository>()
    private val selectedTabBarItem = retainedSimpleInstance { MutableStateFlow(TabBarItem.MAP) }
    private val tabBarOptions = flow {
        emit(remoteConfigRepository.feature(FeatureFlag.NAVIGATE_BUTTON_TAB_BAR))
    }.mapLatest {
        when (it) {
            true -> ALL_TAB_BAR_ITEMS
            else -> ALL_TAB_BAR_ITEMS.filter { it != TabBarItem.NAVIGATE }
        }
    }

    override val state: StateFlow<MainComponentState> = combine(
        selectedTabBarItem,
        tabBarOptions
    ) { selectedTabBarItem, tabBarOptions ->
        MainComponentState(
            tabBarOptions,
            selectedTabBarItem
        )
    }.state(MainComponentState(
        ALL_TAB_BAR_ITEMS,
        TabBarItem.MAP
    ))

}