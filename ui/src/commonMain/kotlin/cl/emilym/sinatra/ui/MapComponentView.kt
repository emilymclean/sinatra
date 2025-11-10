package cl.emilym.sinatra.ui

import androidx.compose.runtime.Composable
import cl.emilym.sinatra.component.MapComponent
import cl.emilym.sinatra.component.MapFabChild
import cl.emilym.sinatra.component.MapSheetChild

@Composable
fun <FAB: MapFabChild, SHEET: MapSheetChild> MapComponentView(
    mapComponent: MapComponent<FAB, SHEET>,
    fabProvider: ComponentViewProvider<FAB>,
    sheetProvider: ComponentViewProvider<SHEET>,
) {

}