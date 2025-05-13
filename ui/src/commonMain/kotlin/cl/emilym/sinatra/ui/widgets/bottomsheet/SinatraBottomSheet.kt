package cl.emilym.sinatra.ui.widgets.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.times
import cl.emilym.compose.units.px
import cl.emilym.sinatra.ui.widgets.viewportHeight
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SinatraBottomSheet(
    state: SinatraSheetState,
    calculateAnchors: (sheetSize: IntSize) -> DraggableAnchors<SinatraSheetValue>,
    peekHeight: Dp,
    sheetMaxWidth: Dp,
    sheetHalfHeight: Float,
    sheetSwipeEnabled: Boolean,
    containerColor: Color,
    contentColor: Color,
    tonalElevation: Dp,
    shadowElevation: Dp,
    dragHandle: @Composable (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit
) {
    val scope = rememberCoroutineScope()

    val orientation = Orientation.Vertical

    // Tween corner radius to 0 during swipe between halfHeight and expanded
    val viewportHeight = viewportHeight()
    val halfHeight = remember(viewportHeight, sheetHalfHeight) { sheetHalfHeight * viewportHeight }
    val offsetPx = state.offset?.px ?: 0.dp
    val corner = remember(halfHeight, offsetPx, viewportHeight) {
        val adjustedHeight = viewportHeight - halfHeight
        (1f - ((viewportHeight - offsetPx - halfHeight) / adjustedHeight)).coerceIn(0f, 1f) * 28.dp
    }
    val shape = remember(corner) {
        RoundedCornerShape(
            corner,
            corner,
            0.dp,
            0.dp
        )
    }

    Surface(
        modifier = Modifier
            .widthIn(max = sheetMaxWidth)
            .fillMaxWidth()
            .requiredHeightIn(min = peekHeight)
            .nestedScroll(
                remember(state.anchoredDraggableState) {
                    ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
                        sheetState = state,
                        orientation = orientation,
                        onFling = { scope.launch { state.settle(it) } }
                    )
                }
            )
            .anchoredDraggable(
                state = state.anchoredDraggableState,
                orientation = orientation,
                enabled = sheetSwipeEnabled
            )
            .onSizeChanged { layoutSize ->
                val newAnchors = calculateAnchors(layoutSize)
                val newTarget = when (state.anchoredDraggableState.targetValue) {
                    SinatraSheetValue.Hidden, SinatraSheetValue.PartiallyExpanded -> SinatraSheetValue.PartiallyExpanded
                    SinatraSheetValue.Expanded -> {
                        if (newAnchors.hasPositionFor(SinatraSheetValue.Expanded)) SinatraSheetValue.Expanded else SinatraSheetValue.PartiallyExpanded
                    }
                    SinatraSheetValue.HalfExpanded -> {
                        if (newAnchors.hasPositionFor(SinatraSheetValue.HalfExpanded)) SinatraSheetValue.HalfExpanded else SinatraSheetValue.PartiallyExpanded
                    }
                }
                state.anchoredDraggableState.updateAnchors(newAnchors, newTarget)
            },
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
    ) {
        Column(Modifier.fillMaxWidth()) {
            if (dragHandle != null) {
                Box(
                    Modifier
                        .align(CenterHorizontally),
                ) {
                    dragHandle()
                }
            }

            val statusWindowInsets = WindowInsets.statusBars.getTop(LocalDensity.current).px
            Column(
                Modifier
                    .fillMaxWidth()
                    .consumeWindowInsets(WindowInsets.statusBars.only(WindowInsetsSides.Top))
                    .padding(top = max(statusWindowInsets - DRAG_HANDLE_HEIGHT, 0.dp))
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun <T : Any> sinatraDraggableAnchors(
    builder: SinatraDraggableAnchorsConfig<T>.() -> Unit
): DraggableAnchors<T> = SinatraDraggableAnchors(
    SinatraDraggableAnchorsConfig<T>().apply(builder).anchors
)

@OptIn(ExperimentalFoundationApi::class)
class SinatraDraggableAnchors<T>(private val anchors: Map<T, Float>) : DraggableAnchors<T> {

    override fun positionOf(value: T): Float = anchors[value] ?: Float.NaN
    override fun hasPositionFor(value: T) = anchors.containsKey(value)

    override fun closestAnchor(position: Float): T? = anchors.minByOrNull {
        abs(position - it.value)
    }?.key

    override fun closestAnchor(
        position: Float,
        searchUpwards: Boolean
    ): T? {
        return anchors.minByOrNull { (_, anchor) ->
            val delta = if (searchUpwards) anchor - position else position - anchor
            if (delta < 0) Float.POSITIVE_INFINITY else delta
        }?.key
    }

    override fun minPosition() = anchors.values.minOrNull() ?: Float.NaN

    override fun maxPosition() = anchors.values.maxOrNull() ?: Float.NaN

    override val size: Int
        get() = anchors.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SinatraDraggableAnchors<*>) return false

        return anchors == other.anchors
    }

    override fun anchorAt(index: Int): T? {
        TODO("Not yet implemented")
    }

    override fun positionAt(index: Int): Float {
        TODO("Not yet implemented")
    }

    override fun hashCode() = 31 * anchors.hashCode()

    override fun toString() = "FuckJetpackDraggableAnchors($anchors)"
}

class SinatraDraggableAnchorsConfig<T> {

    val anchors = mutableMapOf<T, Float>()

    /**
     * Set the anchor position for [this] anchor.
     *
     * @param position The anchor position.
     */
    @Suppress("BuilderSetStyle")
    infix fun T.at(position: Float) {
        anchors[this] = position
    }
}
