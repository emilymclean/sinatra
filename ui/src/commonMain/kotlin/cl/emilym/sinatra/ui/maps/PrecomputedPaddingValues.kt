package cl.emilym.sinatra.ui.maps

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import cl.emilym.sinatra.data.models.Pixel
import cl.emilym.sinatra.ui.widgets.toIntPx

data class PrecomputedPaddingValues(
    val top: Pixel,
    val bottom: Pixel,
    val left: Pixel,
    val right: Pixel
) {

    companion object {
        fun all(value: Pixel): PrecomputedPaddingValues {
            return PrecomputedPaddingValues(
                top = value,
                bottom = value,
                left = value,
                right = value,
            )
        }
    }

}

@Composable
fun PaddingValues.precompute(): PrecomputedPaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PrecomputedPaddingValues(
        calculateTopPadding().toIntPx(),
        calculateBottomPadding().toIntPx(),
        calculateLeftPadding(layoutDirection).toIntPx(),
        calculateRightPadding(layoutDirection).toIntPx()
    )
}