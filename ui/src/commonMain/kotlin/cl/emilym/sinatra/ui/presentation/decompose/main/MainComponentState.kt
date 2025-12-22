package cl.emilym.sinatra.ui.presentation.decompose.main

enum class TabBarItem {
    MAP, NAVIGATE, FAVOURITES, MORE
}

data class MainComponentState(
    val availableTabBarItems: List<TabBarItem>,
    val selectedTabBarItem: TabBarItem
)