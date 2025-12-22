package cl.emilym.sinatra.ui.presentation.decompose.main.tab

enum class TabBarItem {
    MAP, NAVIGATE, FAVOURITES, MORE
}

data class TabBarComponentState(
    val availableTabBarItems: List<TabBarItem>,
    val selectedTabBarItem: TabBarItem
)