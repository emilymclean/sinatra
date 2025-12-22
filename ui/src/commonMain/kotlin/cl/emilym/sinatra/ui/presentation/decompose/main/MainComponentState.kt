package cl.emilym.sinatra.ui.presentation.decompose.main

enum class TabBarItem {
    MAP, NAVIGATE, FAVOURITES, MORE
}

data class MainComponentState(
    val tabBarItems: List<TabBarItem>,
    val showTabBar: Boolean
)