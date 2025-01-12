package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp

@Composable
expect fun screenHeight(): Dp
@Composable
expect fun screenWidth(): Dp

@Composable
fun screenSize(): Size {
    return Size(screenWidth().toFloatPx(), screenHeight().toFloatPx())
}