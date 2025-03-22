package cl.emilym.sinatra.ui.maps

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import cl.emilym.sinatra.data.models.DensityPixel
import cl.emilym.sinatra.data.models.Pixel
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.data.models.ScreenRegion
import cl.emilym.sinatra.ui.widgets.toFloatPx

data class PrecomputedPaddingValuesDp(
    val top: DensityPixel,
    val bottom: DensityPixel,
    val left: DensityPixel,
    val right: DensityPixel
) {
    val horizontal: DensityPixel get() = left + right
    val vertical: DensityPixel get() = top + bottom
}

data class PrecomputedPaddingValues(
    val top: Pixel,
    val bottom: Pixel,
    val left: Pixel,
    val right: Pixel
) {

    val horizontal: Pixel get() = left + right
    val vertical: Pixel get() = top + bottom

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

    operator fun unaryMinus(): PrecomputedPaddingValues {
        return PrecomputedPaddingValues(
            -top,
            -bottom,
            -left,
            -right
        )
    }

    operator fun plus(other: PrecomputedPaddingValues): PrecomputedPaddingValues {
        return PrecomputedPaddingValues(
            top + other.top,
            bottom + other.bottom,
            left + other.left,
            right + other.right
        )
    }

    operator fun times(other: Number): PrecomputedPaddingValues {
        val otherF = other.toDouble()
        return PrecomputedPaddingValues(
            (top * otherF).toFloat(),
            (bottom * otherF).toFloat(),
            (left * otherF).toFloat(),
            (right * otherF).toFloat()
        )
    }

    operator fun div(other: Number): PrecomputedPaddingValues {
        val otherF = other.toDouble()
        return PrecomputedPaddingValues(
            (top / otherF).toFloat(),
            (bottom / otherF).toFloat(),
            (left / otherF).toFloat(),
            (right / otherF).toFloat()
        )
    }

}

@Composable
fun PaddingValues.precompute(): PrecomputedPaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PrecomputedPaddingValues(
        calculateTopPadding().toFloatPx(),
        calculateBottomPadding().toFloatPx(),
        calculateLeftPadding(layoutDirection).toFloatPx(),
        calculateRightPadding(layoutDirection).toFloatPx()
    )
}

@Composable
fun PaddingValues.precomputeDp(): PrecomputedPaddingValuesDp {
    val layoutDirection = LocalLayoutDirection.current
    return PrecomputedPaddingValuesDp(
        calculateTopPadding().value,
        calculateBottomPadding().value,
        calculateLeftPadding(layoutDirection).value,
        calculateRightPadding(layoutDirection).value
    )
}

fun ScreenRegion.padded(padding: PrecomputedPaddingValues): ScreenRegion {
    return ScreenRegion(
        topLeft = ScreenLocation(
            topLeft.x - padding.left,
            topLeft.y - padding.top,
        ),
        bottomRight = ScreenLocation(
            bottomRight.x + padding.right,
            bottomRight.y + padding.bottom
        )
    )
}

fun ScreenRegion.moveForPadding(padding: PrecomputedPaddingValues): ScreenRegion {
    val shiftY = (padding.bottom - padding.top)
    val shiftX = (padding.right - padding.left)
    return ScreenRegion(
        topLeft = ScreenLocation(
            topLeft.x + shiftX,
            topLeft.y + shiftY
        ),
        bottomRight = ScreenLocation(
            bottomRight.x + shiftX,
            bottomRight.y + shiftY
        )
    )
}

