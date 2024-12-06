package cl.emilym.sinatra.ui.navigation

import androidx.compose.runtime.Composable

interface MapScreen {

    @Composable
    fun MapOverlayContent()

    @Composable
    fun BottomSheetContent()

    @Composable
    fun MapScope.MapContent()

}