package cl.emilym.sinatra.ui.presentation.decompose.main.tab

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

interface TabBarComponent: SinatraComponent {

    val state: StateFlow<TabBarComponentState>

    fun navigate(item: TabBarItem)

}

class DefaultTabBarComponent(
    sinatraComponentContext: SinatraComponentContext
): TabBarComponent, SinatraComponentContext by sinatraComponentContext {

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

    override val state: StateFlow<TabBarComponentState> = combine(
        selectedTabBarItem,
        tabBarOptions
    ) { selectedTabBarItem, tabBarOptions ->
        TabBarComponentState(
            tabBarOptions,
            selectedTabBarItem
        )
    }.state(TabBarComponentState(
        ALL_TAB_BAR_ITEMS,
        TabBarItem.MAP
    ))

    override fun navigate(item: TabBarItem) {

    }

}