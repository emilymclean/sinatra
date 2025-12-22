package cl.emilym.sinatra.ui.presentation.decompose.root

enum class RootTabBarItem {
    MAP, NAVIGATE, FAVOURITES, MORE
}

data class RootComponentState(
    val tabBarItems: List<RootTabBarItem>,
    val showTabBar: Boolean
)