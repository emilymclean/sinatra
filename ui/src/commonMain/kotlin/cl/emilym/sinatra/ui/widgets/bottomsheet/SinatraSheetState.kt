package cl.emilym.sinatra.ui.widgets.bottomsheet

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.dp

class SinatraSheetState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val skipPartiallyExpanded: Boolean,
    positionalThreshold: () -> Float,
    velocityThreshold: () -> Float,
    initialValue: SinatraSheetValue = SinatraSheetValue.Hidden,
    val confirmValueChange: (SinatraSheetValue) -> Boolean = { true },
    val skipHiddenState: Boolean = false,
) {

    init {
        if (skipPartiallyExpanded) {
            require(initialValue != SinatraSheetValue.PartiallyExpanded) {
                "The initial value must not be set to PartiallyExpanded if skipPartiallyExpanded " +
                        "is set to true."
            }
        }
        if (skipHiddenState) {
            require(initialValue != SinatraSheetValue.Hidden) {
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
    val currentValue: SinatraSheetValue
        get() = anchoredDraggableState.currentValue

    /**
     * The target value of the bottom sheet state.
     *
     * If a swipe is in progress, this is the value that the sheet would animate to if the swipe
     * finishes. If an animation is running, this is the target value of that animation. Finally, if
     * no swipe or animation is in progress, this is the same as the [currentValue].
     */
    val targetValue: SinatraSheetValue
        get() = anchoredDraggableState.targetValue

    /** Whether the modal bottom sheet is visible. */
    val isVisible: Boolean
        get() = anchoredDraggableState.currentValue != SinatraSheetValue.Hidden

    /**
     * Whether an expanding or collapsing sheet animation is currently in progress.
     *
     * See [expand], [partialExpand], [show] or [hide] for more information.
     */
    val isAnimationRunning: Boolean
        get() = anchoredDraggableState.isAnimationRunning

    /**
     * Require the current offset (in pixels) of the bottom sheet.
     *
     * The offset will be initialized during the first measurement phase of the provided sheet
     * content.
     *
     * These are the phases: Composition { -> Effects } -> Layout { Measurement -> Placement } ->
     * Drawing
     *
     * During the first composition, an [IllegalStateException] is thrown. In subsequent
     * compositions, the offset will be derived from the anchors of the previous pass. Always prefer
     * accessing the offset from a LaunchedEffect as it will be scheduled to be executed the next
     * frame, after layout.
     *
     * @throws IllegalStateException If the offset has not been initialized yet
     */
    fun requireOffset(): Float = anchoredDraggableState.requireOffset()

    /** Whether the sheet has an expanded state defined. */
    val hasExpandedState: Boolean
        get() = anchoredDraggableState.anchors.hasPositionFor(SinatraSheetValue.Expanded)

    /** Whether the modal bottom sheet has a partially expanded state defined. */
    val hasPartiallyExpandedState: Boolean
        get() = anchoredDraggableState.anchors.hasPositionFor(SinatraSheetValue.PartiallyExpanded)

    /**
     * If [confirmValueChange] returns true, fully expand the bottom sheet with animation and
     * suspend until it is fully expanded or animation has been cancelled.
     *
     * @throws [kotlinx.coroutines.CancellationException] if the animation is interrupted
     */
    suspend fun expand() {
        if (confirmValueChange(SinatraSheetValue.Expanded)) animateTo(SinatraSheetValue.Expanded, showMotionSpec)
    }

    /**
     * If [confirmValueChange] returns true, animate the bottom sheet and suspend until it is
     * partially expanded or animation has been cancelled.
     *
     * @throws [kotlinx.coroutines.CancellationException] if the animation is interrupted
     * @throws [IllegalStateException] if [skipPartiallyExpanded] is set to true
     */
    suspend fun partialExpand() {
        check(!skipPartiallyExpanded) {
            "Attempted to animate to partial expanded when skipPartiallyExpanded was enabled. Set" +
                    " skipPartiallyExpanded to false to use this function."
        }
        if (confirmValueChange(SinatraSheetValue.PartiallyExpanded)) animateTo(SinatraSheetValue.PartiallyExpanded, hideMotionSpec)
    }

    suspend fun halfExpand() {
        if (confirmValueChange(SinatraSheetValue.HalfExpanded)) animateTo(SinatraSheetValue.HalfExpanded, hideMotionSpec)
    }

    /**
     * If [confirmValueChange] returns true, expand the bottom sheet with animation and suspend
     * until it is [PartiallyExpanded] if defined, else [androidx.compose.material3.adaptive.layout.PaneAdaptedValue.Companion.Expanded].
     *
     * @throws [kotlinx.coroutines.CancellationException] if the animation is interrupted
     */
    suspend fun show() {
        val targetValue =
            when {
                hasPartiallyExpandedState -> SinatraSheetValue.PartiallyExpanded
                else -> SinatraSheetValue.Expanded
            }
        if (confirmValueChange(targetValue)) animateTo(targetValue, showMotionSpec)
    }

    /**
     * If [confirmValueChange] returns true, hide the bottom sheet with animation and suspend until
     * it is fully hidden or animation has been cancelled.
     *
     * @throws [kotlinx.coroutines.CancellationException] if the animation is interrupted
     */
    suspend fun hide() {
        check(!skipHiddenState) {
            "Attempted to animate to hidden when skipHiddenState was enabled. Set skipHiddenState" +
                    " to false to use this function."
        }
        if (confirmValueChange(SinatraSheetValue.Hidden)) animateTo(SinatraSheetValue.Hidden, hideMotionSpec)
    }

    /**
     * Animate to a [targetValue]. If the [targetValue] is not in the set of anchors, the
     * [currentValue] will be updated to the [targetValue] without updating the offset.
     *
     * @param targetValue The target value of the animation
     * @param animationSpec an [androidx.compose.animation.core.AnimationSpec]
     * @param velocity an initial velocity for the animation
     * @throws kotlinx.coroutines.CancellationException if the interaction interrupted by another interaction like a
     *   gesture interaction or another programmatic interaction like a [animateTo] or [snapTo]
     *   call.
     */
    internal suspend fun animateTo(
        targetValue: SinatraSheetValue,
        animationSpec: FiniteAnimationSpec<Float>,
        velocity: Float = anchoredDraggableState.lastVelocity,
    ) {
        anchoredDraggableState.anchoredDrag(targetValue = targetValue) { anchors, latestTarget ->
            val targetOffset = anchors.positionOf(latestTarget)
            if (!targetOffset.isNaN()) {
                var prev = if (offset.isNaN()) 0f else offset
                animate(prev, targetOffset, velocity, animationSpec) { value, velocity ->
                    // Our onDrag coerces the value within the bounds, but an animation may
                    // overshoot, for example a spring animation or an overshooting interpolator
                    // We respect the user's intention and allow the overshoot, but still use
                    // DraggableState's drag for its mutex.
                    dragTo(value, velocity)
                    prev = value
                }
            }
        }
    }

    /**
     * Snap to a [targetValue] without any animation.
     *
     * @param targetValue The target value of the animation
     * @throws kotlinx.coroutines.CancellationException if the interaction interrupted by another interaction like a
     *   gesture interaction or another programmatic interaction like a [animateTo] or [snapTo]
     *   call.
     */
    internal suspend fun snapTo(targetValue: SinatraSheetValue) {
        anchoredDraggableState.snapTo(targetValue)
    }

    /**
     * Find the closest anchor taking into account the velocity and settle at it with an animation.
     */
    internal suspend fun settle(velocity: Float) {
        anchoredDraggableState.settle(velocity)
    }

    internal var anchoredDraggableMotionSpec: AnimationSpec<Float> = BottomSheetAnimationSpec

    internal var anchoredDraggableState =
        SinatraAnchoredDraggableState(
            initialValue = initialValue,
            animationSpec = { anchoredDraggableMotionSpec },
            confirmValueChange = confirmValueChange,
            positionalThreshold = { positionalThreshold() },
            velocityThreshold = velocityThreshold,
        )

    internal val offset: Float
        get() = anchoredDraggableState.offset

    internal var showMotionSpec: FiniteAnimationSpec<Float> = snap()

    internal var hideMotionSpec: FiniteAnimationSpec<Float> = snap()

    companion object {
        /** The default [Saver] implementation for [androidx.compose.material3.SheetState]. */
        fun Saver(
            skipPartiallyExpanded: Boolean,
            positionalThreshold: () -> Float,
            velocityThreshold: () -> Float,
            confirmValueChange: (SinatraSheetValue) -> Boolean,
            skipHiddenState: Boolean,
        ) =
            androidx.compose.runtime.saveable.Saver<SinatraSheetState, SinatraSheetValue>(
                save = { it.currentValue },
                restore = { savedValue ->
                    SinatraSheetState(
                        skipPartiallyExpanded,
                        positionalThreshold,
                        velocityThreshold,
                        savedValue,
                        confirmValueChange,
                        skipHiddenState,
                    )
                },
            )
    }
}

private val DragHandleVerticalPadding = 22.dp

/** A function that provides the default animation spec used by [SheetState]. */
private val BottomSheetAnimationSpec: AnimationSpec<Float> =
    tween(durationMillis = 300, easing = FastOutSlowInEasing)