package cl.emilym.sinatra.ui.presentation.screens

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey

interface GoogleMapsScreen: Screen {
    val bottomSheetScreenKey: ScreenKey?
    val googleMapsStackKey: ScreenKey?
}