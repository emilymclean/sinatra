package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import cl.emilym.sinatra.data.models.ScreenRegionSizePx

@Composable
expect fun screenHeight(): Dp
@Composable
expect fun screenWidth(): Dp

@Composable
fun screenSize(): ScreenRegionSizePx {
    return ScreenRegionSizePx(screenWidth().toFloatPx(), screenHeight().toFloatPx())
}