package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp

val LocalViewportSize = staticCompositionLocalOf<Size> { error("No local viewport height provided") }

@Composable
fun viewportSize(): Size {
    return LocalViewportSize.current
}

@Composable
fun viewportHeight(): Dp {
    return viewportSize().height.toDp()
}

@Composable
fun viewportWidth(): Dp {
    return viewportSize().width.toDp()
}