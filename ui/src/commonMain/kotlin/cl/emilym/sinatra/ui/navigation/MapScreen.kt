package cl.emilym.sinatra.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.units.px
import cl.emilym.sinatra.ui.maps.MapItem
import cl.emilym.sinatra.ui.maps.MapScope
import cl.emilym.sinatra.ui.maps.NativeMapScope

const val DEFAULT_HALF_HEIGHT = 0.66f

interface MapScreen: Screen {
    val bottomSheetHalfHeight: Float
        get() = DEFAULT_HALF_HEIGHT

    @Composable
    fun BottomSheetContent() {}

    @Composable
    fun MapScope.mapItems(): List<MapItem> = listOf()
}

interface NativeMapScreen {

    @Composable
    fun NativeMapScope.DrawMapNative()

}

@Composable
fun bottomSheetHalfHeight(): Float {
    val navigator = LocalNavigator.currentOrThrow
    val currentScreen = navigator.lastItem
    return (currentScreen as? MapScreen)?.bottomSheetHalfHeight ?: DEFAULT_HALF_HEIGHT
}

@Composable
fun isCurrentMapScreen(): Boolean {
    val navigator = LocalNavigator.currentOrThrow
    return (navigator.lastItem is MapScreen)
}

@Composable
fun CurrentMapOverlayContent() {
    CurrentScreen()
}

@Composable
fun CurrentBottomSheetContent() {
    val navigator = LocalNavigator.currentOrThrow
    val currentScreen = navigator.lastItem
    if (currentScreen !is MapScreen) {
        Box(Modifier.height(1.px))
        return
    }

    navigator.saveableState("bottomSheetScreen") {
        currentScreen.BottomSheetContent()
    }
}

@Composable
fun MapScope.currentMapItems(): List<MapItem> {
    val navigator = LocalNavigator.currentOrThrow
    val currentScreen = navigator.lastItem
    val mapScreen = (currentScreen as? MapScreen) ?: return listOf()

    return with(mapScreen) { mapItems() }
}

@Composable
fun NativeMapScope.currentDrawNativeMap() {
    val navigator = LocalNavigator.currentOrThrow
    val currentScreen = navigator.lastItem
    val mapScreen = (currentScreen as? NativeMapScreen) ?: return

    navigator.saveableState("nativeMap") {
        with(mapScreen) { DrawMapNative() }
    }
}