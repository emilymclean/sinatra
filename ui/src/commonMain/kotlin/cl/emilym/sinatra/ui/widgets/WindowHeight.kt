package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import cl.emilym.sinatra.data.models.ScreenRegionSizePx

val LocalViewportSize = staticCompositionLocalOf<ScreenRegionSizePx> { error("No local viewport height provided") }

@Composable
fun viewportSize(
    insets: WindowInsets? = null
): ScreenRegionSizePx {
    val size = LocalViewportSize.current
    return when {
        insets != null -> {
            val density = LocalDensity.current
            val direction = LocalLayoutDirection.current
            ScreenRegionSizePx(
                size.width - insets.getLeft(density, direction) - insets.getRight(density, direction),
                size.height - insets.getTop(density) - insets.getBottom(density)
            )
        }
        else -> size
    }
}

@Composable
fun viewportHeight(
    insets: WindowInsets? = null
): Dp {
    return viewportSize(insets).height.toDp()
}

@Composable
fun viewportWidth(
    insets: WindowInsets? = null
): Dp {
    return viewportSize(insets).width.toDp()
}