package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.data.models.ScreenRegionSizePx

@Composable
fun ViewportSizeWidget(content: @Composable () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val height = maxHeight.toFloatPx()
        val width = maxWidth.toFloatPx()

        CompositionLocalProvider(LocalViewportSize provides ScreenRegionSizePx(width, height)) {
            content()
        }
    }
}