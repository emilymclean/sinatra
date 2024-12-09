package cl.emilym.sinatra.ui.widgets.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotShitBottomSheet(
    state: NotShitSheetState,
    calculateAnchors: (sheetSize: IntSize) -> DraggableAnchors<NotShitSheetValue>,
    peekHeight: Dp,
    sheetMaxWidth: Dp,
    sheetSwipeEnabled: Boolean,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    tonalElevation: Dp,
    shadowElevation: Dp,
    dragHandle: @Composable (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit
) {
    val scope = rememberCoroutineScope()

    val orientation = Orientation.Vertical

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
                    NotShitSheetValue.Hidden, NotShitSheetValue.PartiallyExpanded -> NotShitSheetValue.PartiallyExpanded
                    NotShitSheetValue.Expanded -> {
                        if (newAnchors.hasAnchorFor(NotShitSheetValue.Expanded)) NotShitSheetValue.Expanded else NotShitSheetValue.PartiallyExpanded
                    }
                    NotShitSheetValue.HalfExpanded -> {
                        if (newAnchors.hasAnchorFor(NotShitSheetValue.HalfExpanded)) NotShitSheetValue.HalfExpanded else NotShitSheetValue.PartiallyExpanded
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
            content()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun <T : Any> GoddamnDraggableAnchors(
    builder: FuckingDraggableAnchorsConfig<T>.() -> Unit
): DraggableAnchors<T> = FuckJetpackDraggableAnchors(
    FuckingDraggableAnchorsConfig<T>().apply(builder).anchors
)

@OptIn(ExperimentalFoundationApi::class)
class FuckJetpackDraggableAnchors<T>(private val anchors: Map<T, Float>) : DraggableAnchors<T> {

    override fun positionOf(value: T): Float = anchors[value] ?: Float.NaN
    override fun hasAnchorFor(value: T) = anchors.containsKey(value)

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

    override fun minAnchor() = anchors.values.minOrNull() ?: Float.NaN

    override fun maxAnchor() = anchors.values.maxOrNull() ?: Float.NaN

    override val size: Int
        get() = anchors.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FuckJetpackDraggableAnchors<*>) return false

        return anchors == other.anchors
    }

    override fun forEach(block: (anchor: T, position: Float) -> Unit) {
        for (i in anchors.entries) {
            block(i.key, i.value)
        }
    }

    override fun hashCode() = 31 * anchors.hashCode()

    override fun toString() = "FuckJetpackDraggableAnchors($anchors)"
}

class FuckingDraggableAnchorsConfig<T> {

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
