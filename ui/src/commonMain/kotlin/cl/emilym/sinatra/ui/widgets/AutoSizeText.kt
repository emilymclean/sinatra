// Credit Reda El Madini
// MIT License
package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastFilter
import cl.emilym.sinatra.ui.widgets.SuggestedFontSizesStatus.Companion.validSuggestedFontSizes
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    suggestedFontSizes: List<TextUnit> = emptyList(),
    suggestedFontSizesStatus: SuggestedFontSizesStatus = SuggestedFontSizesStatus.UNKNOWN,
    stepGranularityTextSize: TextUnit = TextUnit.Unspecified,
    minTextSize: TextUnit = TextUnit.Unspecified,
    maxTextSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    alignment: Alignment = Alignment.TopStart,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    lineSpaceRatio: Float = style.lineHeight.value / style.fontSize.value,
) {
    AutoSizeText(
        text = AnnotatedString(text),
        modifier = modifier,
        color = color,
        suggestedFontSizes = suggestedFontSizes,
        suggestedFontSizesStatus = suggestedFontSizesStatus,
        stepGranularityTextSize = stepGranularityTextSize,
        minTextSize = minTextSize,
        maxTextSize = maxTextSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        alignment = alignment,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
        lineSpacingRatio = lineSpaceRatio,
    )
}

/**
 * Composable function that automatically adjusts the text size to fit within given constraints using AnnotatedString, considering the ratio of line spacing to text size.
 *
 * Features:
 *  Similar to AutoSizeText(String), with support for AnnotatedString.
 *
 * @param inlineContent a map storing composables that replaces certain ranges of the text, used to
 * insert composables into text layout. See [InlineTextContent].
 * @see AutoSizeText
 */
@Composable
fun AutoSizeText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    suggestedFontSizes: List<TextUnit> = emptyList(),
    suggestedFontSizesStatus: SuggestedFontSizesStatus = SuggestedFontSizesStatus.UNKNOWN,
    stepGranularityTextSize: TextUnit = TextUnit.Unspecified,
    minTextSize: TextUnit = TextUnit.Unspecified,
    maxTextSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    alignment: Alignment = Alignment.TopStart,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    lineSpacingRatio: Float = style.lineHeight.value / style.fontSize.value,
) {
    // Change font scale to 1F
    val newDensity = Density(density = LocalDensity.current.density, fontScale = 1F)
    CompositionLocalProvider(LocalDensity provides newDensity) {
        BoxWithConstraints(
            modifier = modifier,
            contentAlignment = alignment,
        ) {
            val combinedTextStyle = LocalTextStyle.current + style.copy(
                color = color.takeIf { it.isSpecified } ?: style.color,
                fontStyle = fontStyle ?: style.fontStyle,
                fontWeight = fontWeight ?: style.fontWeight,
                fontFamily = fontFamily ?: style.fontFamily,
                letterSpacing = letterSpacing.takeIf { it.isSpecified } ?: style.letterSpacing,
                textDecoration = textDecoration ?: style.textDecoration,
                textAlign = when (alignment) {
                    Alignment.TopStart, Alignment.CenterStart, Alignment.BottomStart -> TextAlign.Start
                    Alignment.TopCenter, Alignment.Center, Alignment.BottomCenter -> TextAlign.Center
                    Alignment.TopEnd, Alignment.CenterEnd, Alignment.BottomEnd -> TextAlign.End
                    else -> TextAlign.Unspecified
                },
            )

            val layoutDirection = LocalLayoutDirection.current
            val density = LocalDensity.current
            val fontFamilyResolver = LocalFontFamilyResolver.current
            val textMeasurer = rememberTextMeasurer()
            val coercedLineSpacingRatio = lineSpacingRatio.takeIf { it.isFinite() && it >= 1 } ?: 1F
            val shouldMoveBackward: (TextUnit) -> Boolean = {
                shouldShrink(
                    text = text,
                    textStyle = combinedTextStyle.copy(
                        fontSize = it,
                        lineHeight = it * coercedLineSpacingRatio,
                    ),
                    maxLines = maxLines,
                    layoutDirection = layoutDirection,
                    softWrap = softWrap,
                    density = density,
                    fontFamilyResolver = fontFamilyResolver,
                    textMeasurer = textMeasurer,
                )
            }

            val electedFontSize = remember(
                key1 = suggestedFontSizes,
                key2 = suggestedFontSizesStatus,
            ) {
                if (suggestedFontSizesStatus == SuggestedFontSizesStatus.VALID)
                    suggestedFontSizes
                else
                    suggestedFontSizes.validSuggestedFontSizes
            }?.let {
                remember(
                    key1 = it,
                    key2 = shouldMoveBackward,
                ) {
                    it.findElectedValue(shouldMoveBackward = shouldMoveBackward)
                }
            } ?: run {
                val candidateFontSizesIntProgress = rememberCandidateFontSizesIntProgress(
                    density = density,
                    containerDpSize = DpSize(maxWidth, maxHeight),
                    maxTextSize = maxTextSize,
                    minTextSize = minTextSize,
                    stepGranularityTextSize = stepGranularityTextSize,
                )
                remember(
                    key1 = candidateFontSizesIntProgress,
                    key2 = shouldMoveBackward,
                ) {
                    candidateFontSizesIntProgress.findElectedValue(
                        transform = { density.intPxToSp(it) },
                        shouldMoveBackward = shouldMoveBackward,
                    )
                }
            }

            Text(
                text = text,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                minLines = minLines,
                inlineContent = inlineContent,
                onTextLayout = onTextLayout,
                style = combinedTextStyle.copy(
                    fontSize = electedFontSize,
                    lineHeight = electedFontSize * coercedLineSpacingRatio,
                ),
            )
        }
    }
}

private fun BoxWithConstraintsScope.shouldShrink(
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int,
    layoutDirection: LayoutDirection,
    softWrap: Boolean,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver,
    textMeasurer: TextMeasurer,
) = textMeasurer.measure(
    text = text,
    style = textStyle,
    overflow = TextOverflow.Clip,
    softWrap = softWrap,
    maxLines = maxLines,
    constraints = constraints,
    layoutDirection = layoutDirection,
    density = density,
    fontFamilyResolver = fontFamilyResolver,
).hasVisualOverflow

@Stable
@Composable
private fun rememberCandidateFontSizesIntProgress(
    density: Density,
    containerDpSize: DpSize,
    minTextSize: TextUnit = TextUnit.Unspecified,
    maxTextSize: TextUnit = TextUnit.Unspecified,
    stepGranularityTextSize: TextUnit = TextUnit.Unspecified,
): IntProgression {
    val max = remember(key1 = density, key2 = maxTextSize, key3 = containerDpSize) {
        val intSize = density.dpSizeRoundToIntSize(containerDpSize)
        min(intSize.width, intSize.height).let { max ->
            maxTextSize
                .takeIf { it.isSp }
                ?.let { density.spRoundToPx(it) }
                ?.coerceIn(range = 0..max)
                ?: max
        }
    }

    val min = remember(key1 = density, key2 = minTextSize, key3 = max) {
        minTextSize
            .takeIf { it.isSp }
            ?.let { density.spToIntPx(it) }
            ?.coerceIn(range = 0..max)
            ?: 0
    }

    val step = remember(
        key1 = listOf(
            density,
            min,
            max,
            stepGranularityTextSize,
        )
    ) {
        stepGranularityTextSize
            .takeIf { it.isSp }
            ?.let { density.spToIntPx(it) }
            ?.coerceIn(1, max - min)
            ?: 1
    }

    return remember(key1 = min, key2 = max, key3 = step) {
        min..max step step
    }
}

// This function works by using a binary search algorithm
fun <T> List<T>.findElectedValue(shouldMoveBackward: (T) -> Boolean) = run {
    indices.findElectedValue(
        transform = { this[it] },
        shouldMoveBackward = shouldMoveBackward,
    )
}

// This function works by using a binary search algorithm
private fun <T> IntProgression.findElectedValue(
    transform: (Int) -> T,
    shouldMoveBackward: (T) -> Boolean,
) = run {
    var low = first / step
    var high = last / step
    while (low <= high) {
        val mid = low + (high - low) / 2
        if (shouldMoveBackward(transform(mid * step)))
            high = mid - 1
        else
            low = mid + 1
    }
    transform((high * step).coerceAtLeast(first * step))
}

enum class SuggestedFontSizesStatus {
    VALID, INVALID, UNKNOWN;

    companion object {
        val List<TextUnit>.suggestedFontSizesStatus
            get() = if (isNotEmpty() && fastAll { it.isSp } && sortedBy { it.value } == this)
                VALID
            else
                INVALID

        val List<TextUnit>.validSuggestedFontSizes
            get() = takeIf { it.isNotEmpty() } // Optimization: empty check first to immediately return null
                ?.fastFilter { it.isSp }
                ?.takeIf { it.isNotEmpty() }
                ?.sortedBy { it.value }
    }
}

// DP
fun Density.dpToSp(dp: Dp) = if (dp.isSpecified) dp.toSp() else TextUnit.Unspecified

fun Density.dpToFloatPx(dp: Dp) = if (dp.isSpecified) dp.toPx() else Float.NaN

fun Density.dpToIntPx(dp: Dp) = if (dp.isSpecified) dp.toPx().toInt() else 0

fun Density.dpRoundToPx(dp: Dp) = if (dp.isSpecified) dp.roundToPx() else 0

@Composable
fun Dp.toSp() = LocalDensity.current.dpToSp(this)

@Composable
fun Dp.toFloatPx() = LocalDensity.current.dpToFloatPx(this)

@Composable
fun Dp.toIntPx() = LocalDensity.current.dpToIntPx(this)

@Composable
fun Dp.roundToPx() = LocalDensity.current.dpRoundToPx(this)

fun Dp.toRecDpSize() = if (isSpecified) DpSize(this, this) else DpSize.Unspecified

fun Dp.toRecDpOffset() = if (isSpecified) DpOffset(this, this) else DpOffset.Unspecified


// TEXT UNIT
fun Density.spToDp(sp: TextUnit) = if (sp.isSpecified) sp.toDp() else Dp.Unspecified

fun Density.spToFloatPx(sp: TextUnit) = if (sp.isSpecified) sp.toPx() else Float.NaN

fun Density.spToIntPx(sp: TextUnit) = if (sp.isSpecified) sp.toPx().toInt() else 0

fun Density.spRoundToPx(sp: TextUnit) = if (sp.isSpecified) sp.roundToPx() else 0

@Composable
fun TextUnit.toDp() = LocalDensity.current.spToDp(this)

@Composable
fun TextUnit.toFloatPx() = LocalDensity.current.spToFloatPx(this)

@Composable
fun TextUnit.toIntPx() = LocalDensity.current.spToIntPx(this)

@Composable
fun TextUnit.roundToPx() = LocalDensity.current.spRoundToPx(this)


// FLOAT
fun Density.floatPxToDp(px: Float) = if (px.isFinite()) px.toDp() else Dp.Unspecified

fun Density.floatPxToSp(px: Float) = if (px.isFinite()) px.toSp() else TextUnit.Unspecified

@Composable
fun Float.toDp() = LocalDensity.current.floatPxToDp(this)

@Composable
fun Float.toSp() = LocalDensity.current.floatPxToSp(this)

fun Float.toIntPx() = if (isFinite()) toInt() else 0

fun Float.roundToPx() = if (isFinite()) roundToInt() else 0

fun Float.toRecSize() = if (isFinite()) Size(this, this) else Size.Unspecified

fun Float.toRecOffset() = if (isFinite()) Offset(this, this) else Offset.Unspecified


// INT
fun Density.intPxToDp(px: Int) = px.toDp()

fun Density.intPxToSp(px: Int) = px.toSp()

@Composable
fun Int.toDp() = LocalDensity.current.intPxToDp(this)

@Composable
fun Int.toSp() = LocalDensity.current.intPxToSp(this)

fun Int.toFloatPx() = toFloat()

fun Int.toRecIntSize() = IntSize(this, this)

fun Int.toRecIntOffset() = IntOffset(this, this)


// DP SIZE
fun Density.dpSizeToIntSize(dpSize: DpSize) =
    if (dpSize.isSpecified) IntSize(dpSize.width.toPx().toInt(), dpSize.height.toPx().toInt())
    else IntSize.Zero

fun Density.dpSizeRoundToIntSize(dpSize: DpSize) =
    if (dpSize.isSpecified) IntSize(dpSize.width.roundToPx(), dpSize.height.roundToPx())
    else IntSize.Zero

fun Density.dpSizeToSize(dpSize: DpSize) =
    if (dpSize.isSpecified) Size(dpSize.width.toPx(), dpSize.height.toPx())
    else Size.Unspecified

@Composable
fun DpSize.toIntSize() = LocalDensity.current.dpSizeToIntSize(this)

@Composable
fun DpSize.roundToIntSize() = LocalDensity.current.dpSizeRoundToIntSize(this)

@Composable
fun DpSize.toSize() = LocalDensity.current.dpSizeToSize(this)

fun DpSize.isSpaced() = isSpecified && width > 0.dp && height > 0.dp


// SIZE
fun Density.sizeToDpSize(size: Size) =
    if (size.isSpecified) DpSize(size.width.toDp(), size.height.toDp())
    else DpSize.Unspecified

@Composable
fun Size.toDpSize() =
    if (isSpecified) LocalDensity.current.sizeToDpSize(this)
    else DpSize.Unspecified

fun Size.toIntSize() =
    if (isSpecified) IntSize(width.toInt(), height.toInt())
    else IntSize.Zero

fun Size.isSpaced() = isSpecified && width > 0F && height > 0F


// INT SIZE
fun Density.intSizeToDpSize(intSize: IntSize) = DpSize(intSize.width.toDp(), intSize.height.toDp())

@Composable
fun IntSize.toDpSize() = LocalDensity.current.intSizeToDpSize(this)

@Composable
fun IntSize.toSize() = Size(width.toFloat(), height.toFloat())

fun IntSize.isSpaced() = width > 0 && height > 0


// DP OFFSET
fun Density.dpOffsetToIntOffset(dpOffset: DpOffset) =
    if (dpOffset.isSpecified) IntOffset(dpOffset.x.toPx().toInt(), dpOffset.y.toPx().toInt())
    else IntOffset.Zero

fun Density.dpOffsetRoundToIntOffset(dpOffset: DpOffset) =
    if (dpOffset.isSpecified) IntOffset(dpOffset.x.roundToPx(), dpOffset.y.roundToPx())
    else IntOffset.Zero

fun Density.dpOffsetToOffset(dpOffset: DpOffset) =
    if (dpOffset.isSpecified) Offset(dpOffset.x.toPx(), dpOffset.y.toPx())
    else Offset.Unspecified

@Composable
fun DpOffset.toIntOffset() = LocalDensity.current.dpOffsetToIntOffset(this)

@Composable
fun DpOffset.roundToIntOffset() = LocalDensity.current.dpOffsetRoundToIntOffset(this)

@Composable
fun DpOffset.toOffset() = LocalDensity.current.dpOffsetToOffset(this)


// OFFSET
fun Density.offsetToDpOffset(offset: Offset) =
    if (offset.isSpecified) DpOffset(offset.x.toDp(), offset.y.toDp())
    else DpOffset.Unspecified

@Composable
fun Offset.toDpOffset() = LocalDensity.current.offsetToDpOffset(this)

fun Offset.toIntOffset() =
    if (isSpecified) IntOffset(x.toInt(), y.toInt())
    else IntOffset.Zero

// INT OFFSET
fun Density.intOffsetToDpOffset(intOffset: IntOffset) = DpOffset(intOffset.x.toDp(), intOffset.y.toDp())

@Composable
fun IntOffset.toDpOffset() = LocalDensity.current.intOffsetToDpOffset(this)

fun IntOffset.toOffset() = Offset(x.toFloat(), y.toFloat())