package cl.emilym.sinatra.ui.presentation.screens.maps.navigate

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.NavigationLocation

class NavigateEntryScreen(
    val destination: MapLocation,
    val destinationNavigation: NavigationLocation?,
): Screen {
    override val key: ScreenKey = "navigateEntryScreen-${destination}-${destinationNavigation?.navigationName}"

    @Composable
    override fun Content() {
        
    }

}