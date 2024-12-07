package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

interface MapScreen: Screen {
    @Composable
    fun BottomSheetContent() {}

    @Composable
    fun MapScope.MapContent() {}
}

@Composable
fun CurrentMapOverlayContent() {
    CurrentScreen()
}

@Composable
fun CurrentBottomSheetContent() {
    val navigator = LocalNavigator.currentOrThrow
    val currentScreen = navigator.lastItem
    if (currentScreen !is MapScreen) return

    navigator.saveableState("bottomSheetScreen") {
        currentScreen.BottomSheetContent()
    }
}

@Composable
expect fun MapScope.CurrentMapContent()