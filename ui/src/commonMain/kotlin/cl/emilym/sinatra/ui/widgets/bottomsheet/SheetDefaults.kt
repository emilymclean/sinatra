package cl.emilym.sinatra.ui.widgets.bottomsheet

import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.animateToWithDecay
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import org.jetbrains.compose.resources.getString
import kotlin.jvm.JvmName

// FUCK YOU JETPACK COMPOSE DEVELOPERS

class NotShitSheetState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val skipPartiallyExpanded: Boolean,
    initialValue: NotShitSheetValue = NotShitSheetValue.Hidden,
    confirmValueChange: (NotShitSheetValue) -> Boolean = { true },
    val skipHiddenState: Boolean = false,
) {

    /**
     * State of a sheet composable, such as [ModalBottomSheet]
     *
     * Contains states relating to its swipe position as well as animations between state values.
     *
     * @param skipPartiallyExpanded Whether the partially expanded state, if the sheet is large
     * enough, should be skipped. If true, the sheet will always expand to the [Expanded] state and move
     * to the [Hidden] state if available when hiding the sheet, either programmatically or by user
     * interaction.
     * @param initialValue The initial value of the state.
     * @param density The density that this state can use to convert values to and from dp.
     * @param confirmValueChange Optional callback invoked to confirm or veto a pending state change.
     * @param skipHiddenState Whether the hidden state should be skipped. If true, the sheet will always
     * expand to the [Expanded] state and move to the [PartiallyExpanded] if available, either
     * programmatically or by user interaction.
     */
    @ExperimentalMaterial3Api
    @Suppress("Deprecation")
    constructor(
        skipPartiallyExpanded: Boolean,
        density: Density,
        initialValue: NotShitSheetValue = NotShitSheetValue.Hidden,
        confirmValueChange: (NotShitSheetValue) -> Boolean = { true },
        skipHiddenState: Boolean = false,
    ) : this(skipPartiallyExpanded, initialValue, confirmValueChange, skipHiddenState) {
        this.density = density
    }
    init {
        if (skipPartiallyExpanded) {
            require(initialValue != NotShitSheetValue.PartiallyExpanded) {
                "The initial value must not be set to PartiallyExpanded if skipPartiallyExpanded " +
                        "is set to true."
            }
        }
        if (skipHiddenState) {
            require(initialValue != NotShitSheetValue.Hidden) {
                "The initial value must not be set to Hidden if skipHiddenState is set to true."
            }
        }
    }

    /**
     * The current value of the state.
     *
     * If no swipe or animation is in progress, this corresponds to the state the bottom sheet is
     * currently in. If a swipe or an animation is in progress, this corresponds the state the sheet
     * was in before the swipe or animation started.
     */

    @OptIn(ExperimentalFoundationApi::class)
    val currentValue: NotShitSheetValue get() = anchoredDraggableState.currentValue

    /**
     * The target value of the bottom sheet state.
     *
     * If a swipe is in progress, this is the value that the sheet would animate to if the
     * swipe finishes. If an animation is running, this is the target value of that animation.
     * Finally, if no swipe or animation is in progress, this is the same as the [currentValue].
     */
    @OptIn(ExperimentalFoundationApi::class)
    val targetValue: NotShitSheetValue get() = anchoredDraggableState.targetValue

    /**
     * Whether the modal bottom sheet is visible.
     */
    @OptIn(ExperimentalFoundationApi::class)
    val isVisible: Boolean
        get() = anchoredDraggableState.currentValue != NotShitSheetValue.Hidden

    /**
     * Require the current offset (in pixels) of the bottom sheet.
     *
     * The offset will be initialized during the first measurement phase of the provided sheet
     * content.
     *
     * These are the phases:
     * Composition { -> Effects } -> Layout { Measurement -> Placement } -> Drawing
     *
     * During the first composition, an [IllegalStateException] is thrown. In subsequent
     * compositions, the offset will be derived from the anchors of the previous pass. Always prefer
     * accessing the offset from a LaunchedEffect as it will be scheduled to be executed the next
     * frame, after layout.
     *
     * @throws IllegalStateException If the offset has not been initialized yet
     */
    @OptIn(ExperimentalFoundationApi::class)
    fun requireOffset(): Float = anchoredDraggableState.requireOffset()

    /**
     * Whether the sheet has an expanded state defined.
     */

    @OptIn(ExperimentalFoundationApi::class)
    val hasExpandedState: Boolean
        get() = anchoredDraggableState.anchors.hasAnchorFor(NotShitSheetValue.Expanded)

    @OptIn(ExperimentalFoundationApi::class)
    val hasHalfExpandedState: Boolean
        get() = anchoredDraggableState.anchors.hasAnchorFor(NotShitSheetValue.HalfExpanded)

    /**
     * Whether the modal bottom sheet has a partially expanded state defined.
     */
    @OptIn(ExperimentalFoundationApi::class)
    val hasPartiallyExpandedState: Boolean
        get() = anchoredDraggableState.anchors.hasAnchorFor(NotShitSheetValue.PartiallyExpanded)

    /**
     * Fully expand the bottom sheet with animation and suspend until it is fully expanded or
     * animation has been cancelled.
     * *
     * @throws [CancellationException] if the animation is interrupted
     */
    @OptIn(ExperimentalFoundationApi::class)
    suspend fun expand() {
        anchoredDraggableState.animateTo(NotShitSheetValue.Expanded)
    }

    @OptIn(ExperimentalFoundationApi::class)
    suspend fun halfExpand() {
        anchoredDraggableState.animateTo(NotShitSheetValue.HalfExpanded)
    }

    /**
     * Animate the bottom sheet and suspend until it is partially expanded or animation has been
     * cancelled.
     * @throws [CancellationException] if the animation is interrupted
     * @throws [IllegalStateException] if [skipPartiallyExpanded] is set to true
     */
    suspend fun partialExpand() {
        check(!skipPartiallyExpanded) {
            "Attempted to animate to partial expanded when skipPartiallyExpanded was enabled. Set" +
                    " skipPartiallyExpanded to false to use this function."
        }
        animateTo(NotShitSheetValue.PartiallyExpanded)
    }

    /**
     * Expand the bottom sheet with animation and suspend until it is [PartiallyExpanded] if defined
     * else [Expanded].
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun show() {
        val targetValue = when {
            hasPartiallyExpandedState -> NotShitSheetValue.PartiallyExpanded
            else -> NotShitSheetValue.Expanded
        }
        animateTo(targetValue)
    }

    /**
     * Hide the bottom sheet with animation and suspend until it is fully hidden or animation has
     * been cancelled.
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun hide() {
        check(!skipHiddenState) {
            "Attempted to animate to hidden when skipHiddenState was enabled. Set skipHiddenState" +
                    " to false to use this function."
        }
        animateTo(NotShitSheetValue.Hidden)
    }

    /**
     * Animate to a [targetValue].
     * If the [targetValue] is not in the set of anchors, the [currentValue] will be updated to the
     * [targetValue] without updating the offset.
     *
     * @throws CancellationException if the interaction interrupted by another interaction like a
     * gesture interaction or another programmatic interaction like a [animateTo] or [snapTo] call.
     *
     * @param targetValue The target value of the animation
     */
    @OptIn(ExperimentalFoundationApi::class)
    internal suspend fun animateTo(
        targetValue: NotShitSheetValue,
        velocity: Float = anchoredDraggableState.lastVelocity
    ) {
        anchoredDraggableState.animateToWithDecay(targetValue, velocity)
    }

    /**
     * Snap to a [targetValue] without any animation.
     *
     * @throws CancellationException if the interaction interrupted by another interaction like a
     * gesture interaction or another programmatic interaction like a [animateTo] or [snapTo] call.
     *
     * @param targetValue The target value of the animation
     */
    @OptIn(ExperimentalFoundationApi::class)
    internal suspend fun snapTo(targetValue: NotShitSheetValue) {
        anchoredDraggableState.snapTo(targetValue)
    }

    /**
     * Find the closest anchor taking into account the velocity and settle at it with an animation.
     */
    @OptIn(ExperimentalFoundationApi::class)
    internal suspend fun settle(velocity: Float) {
        anchoredDraggableState.settle(velocity)
    }

    @OptIn(ExperimentalFoundationApi::class)
    internal var anchoredDraggableState = AnchoredDraggableState(
        initialValue = initialValue,
        snapAnimationSpec = SpringSpec<Float>(),
        decayAnimationSpec = exponentialDecay(),
        positionalThreshold = { with(requireDensity()) { 56.dp.toPx() } },
        velocityThreshold = { with(requireDensity()) { 125.dp.toPx() } },
        confirmValueChange = confirmValueChange
    )

    @OptIn(ExperimentalFoundationApi::class)
    internal val offset: Float? get() = anchoredDraggableState.offset

    internal var density: Density? = null
    private fun requireDensity() = requireNotNull(density) {
        "SheetState did not have a density attached. Are you using SheetState with " +
                "BottomSheetScaffold or ModalBottomSheet component?"
    }

    companion object {
        /**
         * The default [Saver] implementation for [SheetState].
         */
        @OptIn(ExperimentalMaterial3Api::class)
        fun Saver(
            skipPartiallyExpanded: Boolean,
            confirmValueChange: (NotShitSheetValue) -> Boolean,
            density: Density
        ) = Saver<NotShitSheetState, NotShitSheetValue>(
            save = { it.currentValue },
            restore = { savedValue ->
                NotShitSheetState(skipPartiallyExpanded, density, savedValue, confirmValueChange)
            }
        )

        /**
         * The default [Saver] implementation for [SheetState].
         */
        @Deprecated(
            message = "This function is deprecated. Please use the overload where Density is" +
                    " provided.",
            replaceWith = ReplaceWith(
                "Saver(skipPartiallyExpanded, confirmValueChange, LocalDensity.current)"
            )
        )
        @Suppress("Deprecation")
        fun Saver(
            skipPartiallyExpanded: Boolean,
            confirmValueChange: (NotShitSheetValue) -> Boolean
        ) = Saver<NotShitSheetState, NotShitSheetValue>(
            save = { it.currentValue },
            restore = { savedValue ->
                NotShitSheetState(skipPartiallyExpanded, savedValue, confirmValueChange)
            }
        )
    }
}

/**
 * Possible values of [SheetState].
 */
enum class NotShitSheetValue {
    /**
     * The sheet is not visible.
     */
    Hidden,

    /**
     * The sheet is visible at full height.
     */
    Expanded,

    HalfExpanded,

    /**
     * The sheet is partially visible.
     */
    PartiallyExpanded,
}

/**
 * Contains the default values used by [ModalBottomSheet] and [BottomSheetScaffold].
 */
object BottomSheetDefaults {
    /** The default shape for bottom sheets in a [Hidden] state. */
    val HiddenShape: Shape
        @Composable get() = RectangleShape

    /** The default shape for a bottom sheets in [PartiallyExpanded] and [Expanded] states. */
    val ExpandedShape: Shape
        // REALLY YOU MADE _TOP_ INTERNAL
        @Composable get() = MaterialTheme.shapes.extraLarge.copy(bottomStart = CornerSize(0f), bottomEnd = CornerSize(0f))

    /** The default container color for a bottom sheet. */
    val ContainerColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.primaryContainer

    /** The default elevation for a bottom sheet. */
    val Elevation = 1.dp

    /** The default color of the scrim overlay for background content. */
    val ScrimColor: Color
        @Composable get() = MaterialTheme.colorScheme.scrim.copy(0.32f)

    /**
     * The default peek height used by [BottomSheetScaffold].
     */
    val SheetPeekHeight = 56.dp

    /**
     * The default max width used by [ModalBottomSheet] and [BottomSheetScaffold]
     */
    val SheetMaxWidth = 640.dp

    /**
     * Default insets to be used and consumed by the [ModalBottomSheet] window.
     */
    val windowInsets: WindowInsets
        @Composable
        get() = WindowInsets.systemBars.only(WindowInsetsSides.Vertical)

    /**
     * The optional visual marker placed on top of a bottom sheet to indicate it may be dragged.
     */
    @Composable
    fun DragHandle(
        modifier: Modifier = Modifier,
        width: Dp = 32.0.dp,
        height: Dp = 4.0.dp,
        shape: Shape = MaterialTheme.shapes.extraLarge,
        color: Color = MaterialTheme.colorScheme.onSurfaceVariant
            .copy(alpha = 0.4f),
    ) {
        Surface(
            modifier = modifier
                .padding(vertical = DragHandleVerticalPadding),
            color = color,
            shape = shape
        ) {
            Box(
                Modifier
                    .size(
                        width = width,
                        height = height
                    )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
internal fun ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
    sheetState: NotShitSheetState,
    orientation: Orientation,
    onFling: (velocity: Float) -> Unit
): NestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta < 0 && source == NestedScrollSource.Drag) {
            sheetState.anchoredDraggableState.dispatchRawDelta(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return if (source == NestedScrollSource.Drag) {
            sheetState.anchoredDraggableState.dispatchRawDelta(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = available.toFloat()
        val currentOffset = sheetState.requireOffset()
        val minAnchor = sheetState.anchoredDraggableState.anchors.minAnchor()
        return if (toFling < 0 && currentOffset > minAnchor) {
            onFling(toFling)
            // since we go to the anchor with tween settling, consume all for the best UX
            available
        } else {
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        onFling(available.toFloat())
        return available
    }

    private fun Float.toOffset(): Offset = Offset(
        x = if (orientation == Orientation.Horizontal) this else 0f,
        y = if (orientation == Orientation.Vertical) this else 0f
    )

    @JvmName("velocityToFloat")
    private fun Velocity.toFloat() = if (orientation == Orientation.Horizontal) x else y

    @JvmName("offsetToFloat")
    private fun Offset.toFloat(): Float = if (orientation == Orientation.Horizontal) x else y
}

@Composable
@ExperimentalMaterial3Api
internal fun rememberNotShitSheetState(
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (NotShitSheetValue) -> Boolean = { true },
    initialValue: NotShitSheetValue = NotShitSheetValue.Hidden,
    skipHiddenState: Boolean = false,
): NotShitSheetState {

    val density = LocalDensity.current
    return rememberSaveable(
        skipPartiallyExpanded, confirmValueChange,
        saver = NotShitSheetState.Saver(
            skipPartiallyExpanded = skipPartiallyExpanded,
            confirmValueChange = confirmValueChange,
            density = density
        )
    ) {
        NotShitSheetState(
            skipPartiallyExpanded,
            density,
            initialValue,
            confirmValueChange,
            skipHiddenState
        )
    }
}

private val DragHandleVerticalPadding = 22.dp