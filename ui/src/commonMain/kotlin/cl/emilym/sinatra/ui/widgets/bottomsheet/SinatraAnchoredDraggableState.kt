package cl.emilym.sinatra.ui.widgets.bottomsheet

import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.gestures.AnchoredDragScope
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.structuralEqualityPolicy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

private fun Float.coerceToTarget(target: Float): Float {
    if (target == 0f) return 0f
    return if (target > 0) coerceAtMost(target) else coerceAtLeast(target)
}

@OptIn(ExperimentalFoundationApi::class)
suspend fun <T> SinatraAnchoredDraggableState<T>.animateTo(targetValue: T) {
    anchoredDrag(targetValue = targetValue) { anchors, latestTarget ->
        animateTo(lastVelocity, this, anchors, latestTarget)
    }
}

@OptIn(ExperimentalFoundationApi::class)
suspend fun <T> SinatraAnchoredDraggableState<T>.snapTo(targetValue: T) {
    anchoredDrag(targetValue = targetValue) { anchors, latestTarget ->
        val targetOffset = anchors.positionOf(latestTarget)
        if (!targetOffset.isNaN()) dragTo(targetOffset)
    }
}

@OptIn(ExperimentalFoundationApi::class)
private suspend fun <T> SinatraAnchoredDraggableState<T>.animateTo(
    velocity: Float,
    anchoredDragScope: AnchoredDragScope,
    anchors: DraggableAnchors<T>,
    latestTarget: T
) {
    with(anchoredDragScope) {
        val targetOffset = anchors.positionOf(latestTarget)
        var prev = if (offset.isNaN()) 0f else offset
        if (!targetOffset.isNaN() && prev != targetOffset) {
            animate(prev, targetOffset, velocity, snapAnimationSpec) { value, velocity ->
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

@ExperimentalFoundationApi
suspend fun <T> SinatraAnchoredDraggableState<T>.animateToWithDecay(
    targetValue: T,
    velocity: Float,
): Float {
    var remainingVelocity = velocity
    anchoredDrag(targetValue = targetValue) { anchors, latestTarget ->
        val targetOffset = anchors.positionOf(latestTarget)
        if (!targetOffset.isNaN()) {
            var prev = if (offset.isNaN()) 0f else offset
            if (prev != targetOffset) {
                // If targetOffset is not in the same direction as the direction of the drag (sign
                // of the velocity) we fall back to using target animation.
                // If the component is at the target offset already, we use decay animation that will
                // not consume any velocity.
                if (velocity * (targetOffset - prev) < 0f || velocity == 0f) {
                    animateTo(velocity, this, anchors, latestTarget)
                    remainingVelocity = 0f
                } else {
                    val projectedDecayOffset =
                        decayAnimationSpec.calculateTargetValue(prev, velocity)

                    val canDecayToTarget = if (velocity > 0) {
                        projectedDecayOffset >= targetOffset
                    } else {
                        projectedDecayOffset <= targetOffset
                    }
                    if (canDecayToTarget) {
                        AnimationState(prev, velocity)
                            .animateDecay(decayAnimationSpec) {
                                if (abs(value) >= abs(targetOffset)) {
                                    val finalValue = value.coerceToTarget(targetOffset)
                                    dragTo(finalValue, this.velocity)
                                    remainingVelocity =
                                        if (this.velocity.isNaN()) 0f else this.velocity
                                    prev = finalValue
                                    cancelAnimation()
                                } else {
                                    dragTo(value, this.velocity)
                                    remainingVelocity = this.velocity
                                    prev = value
                                }
                            }
                    } else {
                        animateTo(velocity, this, anchors, latestTarget)
                        remainingVelocity = 0f
                    }
                }
            }
        }
    }
    return velocity - remainingVelocity
}

@Stable
@ExperimentalFoundationApi
class SinatraAnchoredDraggableState<T>(
    initialValue: T,
    internal val positionalThreshold: (totalDistance: Float) -> Float,
    internal val velocityThreshold: () -> Float,
    val snapAnimationSpec: AnimationSpec<Float>,
    val decayAnimationSpec: DecayAnimationSpec<Float>,
    internal val confirmValueChange: (newValue: T) -> Boolean = { true }
) {

    /**
     * Construct an [AnchoredDraggableState] instance with anchors.
     *
     * @param initialValue The initial value of the state.
     * @param anchors The anchors of the state. Use [updateAnchors] to update the anchors later.
     * @param snapAnimationSpec The default animation spec that will be used to animate to a new
     * state.
     * @param decayAnimationSpec The animation spec that will be used when flinging with a large
     * enough velocity to reach or cross the target state.
     * @param confirmValueChange Optional callback invoked to confirm or veto a pending state
     * change.
     * @param positionalThreshold The positional threshold, in px, to be used when calculating the
     * target state while a drag is in progress and when settling after the drag ends. This is the
     * distance from the start of a transition. It will be, depending on the direction of the
     * interaction, added or subtracted from/to the origin offset. It should always be a positive
     * value.
     * @param velocityThreshold The velocity threshold (in px per second) that the end velocity has
     * to exceed in order to animate to the next state, even if the [positionalThreshold] has not
     * been reached.
     */
    @ExperimentalFoundationApi
    constructor(
        initialValue: T,
        anchors: DraggableAnchors<T>,
        positionalThreshold: (totalDistance: Float) -> Float,
        velocityThreshold: () -> Float,
        snapAnimationSpec: AnimationSpec<Float>,
        decayAnimationSpec: DecayAnimationSpec<Float>,
        confirmValueChange: (newValue: T) -> Boolean = { true }
    ) : this(
        initialValue,
        positionalThreshold,
        velocityThreshold,
        snapAnimationSpec,
        decayAnimationSpec,
        confirmValueChange
    ) {
        this.anchors = anchors
        trySnapTo(initialValue)
    }

    private val dragMutex = MutatorMutex()

    /**
     * The current value of the [AnchoredDraggableState].
     *
     * That is the closest anchor point that the state has passed through.
     */
    var currentValue: T by mutableStateOf(initialValue)
        private set

    /**
     * The value the [AnchoredDraggableState] is currently settled at.
     *
     * When progressing through multiple anchors, e.g. `A -> B -> C`, [settledValue] will stay the
     * same until settled at an anchor, while [currentValue] will update to the closest anchor.
     */
    var settledValue: T by mutableStateOf(initialValue)
        private set

    /**
     * The target value. This is the closest value to the current offset. If no interactions like
     * animations or drags are in progress, this will be the current value.
     */
    val targetValue: T by derivedStateOf {
        dragTarget ?: run {
            val currentOffset = offset
            if (!currentOffset.isNaN()) {
                anchors.closestAnchor(offset) ?: currentValue
            } else currentValue
        }
    }

    /**
     * The current offset, or [Float.NaN] if it has not been initialized yet.
     *
     * The offset will be initialized when the anchors are first set through [updateAnchors].
     *
     * Strongly consider using [requireOffset] which will throw if the offset is read before it is
     * initialized. This helps catch issues early in your workflow.
     */
    var offset: Float by mutableFloatStateOf(Float.NaN)
        private set

    /**
     * Require the current offset.
     *
     * @see offset
     *
     * @throws IllegalStateException If the offset has not been initialized yet
     */
    fun requireOffset(): Float {
        check(!offset.isNaN()) {
            "The offset was read before being initialized. Did you access the offset in a phase " +
                    "before layout, like effects or composition?"
        }
        return offset
    }

    /**
     * Whether an animation is currently in progress.
     */
    val isAnimationRunning: Boolean get() = dragTarget != null

    /**
     * The fraction of the offset between [from] and [to], as a fraction between [0f..1f], or 1f if
     * [from] is equal to [to].
     *
     * @param from The starting value used to calculate the distance
     * @param to The end value used to calculate the distance
     */
    @FloatRange(from = 0.0, to = 1.0)
    fun progress(from: T, to: T): Float {
        val fromOffset = anchors.positionOf(from)
        val toOffset = anchors.positionOf(to)
        val currentOffset = offset.coerceIn(
            min(fromOffset, toOffset), // fromOffset might be > toOffset
            max(fromOffset, toOffset)
        )
        val fraction = (currentOffset - fromOffset) / (toOffset - fromOffset)
        return if (!fraction.isNaN()) {
            // If we are very close to 0f or 1f, we round to the closest
            if (fraction < 1e-6f) 0f else if (fraction > 1 - 1e-6f) 1f else abs(fraction)
        } else 1f
    }

    /**
     * The fraction of the progress going from [settledValue] to [targetValue], within [0f..1f]
     * bounds, or 1f if the [AnchoredDraggableState] is in a settled state.
     */
    @Deprecated(
        message = "Use the progress function to query the progress between two specified " +
                "anchors.",
        replaceWith = ReplaceWith("progress(state.settledValue, state.targetValue)")
    )
    @get:FloatRange(from = 0.0, to = 1.0)
    val progress: Float by derivedStateOf(structuralEqualityPolicy()) {
        val a = anchors.positionOf(settledValue)
        val b = anchors.positionOf(targetValue)
        val distance = abs(b - a)
        if (!distance.isNaN() && distance > 1e-6f) {
            val progress = (this.requireOffset() - a) / (b - a)
            // If we are very close to 0f or 1f, we round to the closest
            if (progress < 1e-6f) 0f else if (progress > 1 - 1e-6f) 1f else progress
        } else 1f
    }

    /**
     * The velocity of the last known animation. Gets reset to 0f when an animation completes
     * successfully, but does not get reset when an animation gets interrupted.
     * You can use this value to provide smooth reconciliation behavior when re-targeting an
     * animation.
     */
    var lastVelocity: Float by mutableFloatStateOf(0f)
        private set

    private var dragTarget: T? by mutableStateOf(null)

    var anchors: DraggableAnchors<T> by mutableStateOf(SinatraDraggableAnchors(mapOf()))
        private set

    /**
     * Update the anchors. If there is no ongoing [anchoredDrag] operation, snap to the [newTarget],
     * otherwise restart the ongoing [anchoredDrag] operation (e.g. an animation) with the new
     * anchors.
     *
     * <b>If your anchors depend on the size of the layout, updateAnchors should be called in the
     * layout (placement) phase, e.g. through Modifier.onSizeChanged.</b> This ensures that the
     * state is set up within the same frame.
     * For static anchors, or anchors with different data dependencies, [updateAnchors] is safe to
     * be called from side effects or layout.
     *
     * @param newAnchors The new anchors.
     * @param newTarget The new target, by default the closest anchor or the current target if there
     * are no anchors.
     */
    fun updateAnchors(
        newAnchors: DraggableAnchors<T>,
        newTarget: T = if (!offset.isNaN()) {
            newAnchors.closestAnchor(offset) ?: targetValue
        } else targetValue
    ) {
        if (anchors != newAnchors) {
            anchors = newAnchors
            // Attempt to snap. If nobody is holding the lock, we can immediately update the offset.
            // If anybody is holding the lock, we send a signal to restart the ongoing work with the
            // updated anchors.
            val snapSuccessful = trySnapTo(newTarget)
            if (!snapSuccessful) {
                dragTarget = newTarget
            }
        }
    }

    /**
     * Find the closest anchor, taking into account the [velocityThreshold] and
     * [positionalThreshold], and settle at it with an animation.
     *
     * If the [velocity] is lower than the [velocityThreshold], the closest anchor by distance and
     * [positionalThreshold] will be the target. If the [velocity] is higher than the
     * [velocityThreshold], the [positionalThreshold] will <b>not</b> be considered and the next
     * anchor in the direction indicated by the sign of the [velocity] will be the target.
     *
     * Based on the [velocity], either [snapAnimationSpec] or [decayAnimationSpec] will be used
     * to animate towards the target.
     *
     * @return The velocity consumed in the animation
     */
    suspend fun settle(velocity: Float): Float {
        val previousValue = this.currentValue
        val targetValue = computeTarget(
            offset = requireOffset(),
            currentValue = previousValue,
            velocity = velocity
        )
        return if (confirmValueChange(targetValue)) {
            animateToWithDecay(targetValue, velocity)
        } else {
            // If the user vetoed the state change, rollback to the previous state.
            animateToWithDecay(previousValue, velocity)
        }
    }

    private fun computeTarget(
        offset: Float,
        currentValue: T,
        velocity: Float
    ): T {
        val currentAnchors = anchors
        val currentAnchorPosition = currentAnchors.positionOf(currentValue)
        val velocityThresholdPx = velocityThreshold()
        return if (currentAnchorPosition == offset || currentAnchorPosition.isNaN()) {
            currentValue
        } else {
            if (abs(velocity) >= abs(velocityThresholdPx)) {
                currentAnchors.closestAnchor(
                    offset,
                    sign(velocity) > 0
                )!!
            } else {
                val neighborAnchor =
                    currentAnchors.closestAnchor(
                        offset,
                        offset - currentAnchorPosition > 0
                    )!!
                val neighborAnchorPosition = currentAnchors.positionOf(neighborAnchor)
                val distance = abs(currentAnchorPosition - neighborAnchorPosition)
                val relativeThreshold = abs(positionalThreshold(distance))
                val relativePosition = abs(currentAnchorPosition - offset)
                if (relativePosition <= relativeThreshold) currentValue else neighborAnchor
            }
        }
    }

    private val anchoredDragScope = object : AnchoredDragScope {
        var leftBound: T? = null
        var rightBound: T? = null
        var distance = Float.NaN

        override fun dragTo(newOffset: Float, lastKnownVelocity: Float) {
            val previousOffset = offset
            offset = newOffset
            lastVelocity = lastKnownVelocity
            if (previousOffset.isNaN()) return
            val isMovingForward = newOffset >= previousOffset
            updateIfNeeded(isMovingForward)
        }

        fun updateIfNeeded(isMovingForward: Boolean) {
            updateBounds(isMovingForward)
            val distanceToCurrentAnchor = abs(offset - anchors.positionOf(currentValue))
            val crossedThreshold = distanceToCurrentAnchor >= distance / 2f
            if (crossedThreshold) {
                val closestAnchor = (if (isMovingForward) rightBound else leftBound) ?: currentValue
                if (confirmValueChange(closestAnchor)) {
                    currentValue = closestAnchor
                }
            }
        }

        fun updateBounds(isMovingForward: Boolean) {
            val currentAnchorPosition = anchors.positionOf(currentValue)
            if (offset == currentAnchorPosition) {
                val searchStartPosition = offset + (if (isMovingForward) 1f else -1f)
                val closestExcludingCurrent =
                    anchors.closestAnchor(searchStartPosition, isMovingForward) ?: currentValue
                if (isMovingForward) {
                    leftBound = currentValue
                    rightBound = closestExcludingCurrent
                } else {
                    leftBound = closestExcludingCurrent
                    rightBound = currentValue
                }
            } else {
                val closestLeft = anchors.closestAnchor(offset, false) ?: currentValue
                val closestRight = anchors.closestAnchor(offset, true) ?: currentValue
                leftBound = closestLeft
                rightBound = closestRight
            }
            distance = abs(anchors.positionOf(leftBound!!) - anchors.positionOf(rightBound!!))
        }
    }

    /**
     * Call this function to take control of drag logic and perform anchored drag with the latest
     * anchors.
     *
     * All actions that change the [offset] of this [AnchoredDraggableState] must be performed
     * within an [anchoredDrag] block (even if they don't call any other methods on this object)
     * in order to guarantee that mutual exclusion is enforced.
     *
     * If [anchoredDrag] is called from elsewhere with the [dragPriority] higher or equal to ongoing
     * drag, the ongoing drag will be cancelled.
     *
     * <b>If the [anchors] change while the [block] is being executed, it will be cancelled and
     * re-executed with the latest anchors and target.</b> This allows you to target the correct
     * state.
     *
     * @param dragPriority of the drag operation
     * @param block perform anchored drag given the current anchor provided
     */
    suspend fun anchoredDrag(
        dragPriority: MutatePriority = MutatePriority.Default,
        block: suspend AnchoredDragScope.(anchors: DraggableAnchors<T>) -> Unit
    ) {
        dragMutex.mutate(dragPriority) {
            restartable(inputs = { anchors }) { latestAnchors ->
                anchoredDragScope.block(latestAnchors)
            }
            val closest = anchors.closestAnchor(offset)
            if (closest != null) {
                val closestAnchorOffset = anchors.positionOf(closest)
                val isAtClosestAnchor = abs(offset - closestAnchorOffset) < 0.5f
                if (isAtClosestAnchor && confirmValueChange.invoke(closest)) {
                    settledValue = closest
                    currentValue = closest
                }
            }
        }
    }

    /**
     * Call this function to take control of drag logic and perform anchored drag with the latest
     * anchors and target.
     *
     * All actions that change the [offset] of this [AnchoredDraggableState] must be performed
     * within an [anchoredDrag] block (even if they don't call any other methods on this object)
     * in order to guarantee that mutual exclusion is enforced.
     *
     * This overload allows the caller to hint the target value that this [anchoredDrag] is intended
     * to arrive to. This will set [AnchoredDraggableState.targetValue] to provided value so
     * consumers can reflect it in their UIs.
     *
     * <b>If the [anchors] or [AnchoredDraggableState.targetValue] change while the [block] is being
     * executed, it will be cancelled and re-executed with the latest anchors and target.</b> This
     * allows you to target the correct state.
     *
     * If [anchoredDrag] is called from elsewhere with the [dragPriority] higher or equal to ongoing
     * drag, the ongoing drag will be cancelled.
     *
     * @param targetValue hint the target value that this [anchoredDrag] is intended to arrive to
     * @param dragPriority of the drag operation
     * @param block perform anchored drag given the current anchor provided
     */
    suspend fun anchoredDrag(
        targetValue: T,
        dragPriority: MutatePriority = MutatePriority.Default,
        block: suspend AnchoredDragScope.(anchor: DraggableAnchors<T>, targetValue: T) -> Unit
    ) {
        if (anchors.hasPositionFor(targetValue)) {
            try {
                dragMutex.mutate(dragPriority) {
                    dragTarget = targetValue
                    restartable(
                        inputs = { anchors to this.targetValue }
                    ) { (anchors, latestTarget) ->
                        anchoredDragScope.block(anchors, latestTarget)
                    }
                    if (confirmValueChange(targetValue)) {
                        val latestTargetOffset = anchors.positionOf(targetValue)
                        anchoredDragScope.dragTo(latestTargetOffset, lastVelocity)
                        settledValue = targetValue
                        currentValue = targetValue
                    }
                }
            } finally {
                dragTarget = null
            }
        } else {
            if (confirmValueChange(targetValue)) {
                settledValue = targetValue
                currentValue = targetValue
            }
        }
    }

    internal fun newOffsetForDelta(delta: Float) =
        ((if (offset.isNaN()) 0f else offset) + delta)
            .coerceIn(anchors.minPosition(), anchors.maxPosition())

    /**
     * Drag by the [delta], coerce it in the bounds and dispatch it to the [AnchoredDraggableState].
     *
     * @return The delta the consumed by the [AnchoredDraggableState]
     */
    fun dispatchRawDelta(delta: Float): Float {
        val newOffset = newOffsetForDelta(delta)
        val oldOffset = if (offset.isNaN()) 0f else offset
        offset = newOffset
        return newOffset - oldOffset
    }

    /**
     * Attempt to snap synchronously. Snapping can happen synchronously when there is no other drag
     * transaction like a drag or an animation is progress. If there is another interaction in
     * progress, the suspending [snapTo] overload needs to be used.
     *
     * @return true if the synchronous snap was successful, or false if we couldn't snap synchronous
     */
    private fun trySnapTo(targetValue: T): Boolean = dragMutex.tryMutate {
        with(anchoredDragScope) {
            val targetOffset = anchors.positionOf(targetValue)
            if (!targetOffset.isNaN()) {
                dragTo(targetOffset)
                dragTarget = null
            }
            currentValue = targetValue
            settledValue = targetValue
        }
    }

    companion object {
        /**
         * The default [Saver] implementation for [AnchoredDraggableState].
         */
        @ExperimentalFoundationApi
        fun <T : Any> Saver(
            snapAnimationSpec: AnimationSpec<Float>,
            decayAnimationSpec: DecayAnimationSpec<Float>,
            positionalThreshold: (distance: Float) -> Float,
            velocityThreshold: () -> Float,
            confirmValueChange: (T) -> Boolean = { true },
        ) = Saver<AnchoredDraggableState<T>, T>(
            save = { it.currentValue },
            restore = {
                AnchoredDraggableState(
                    initialValue = it,
                    snapAnimationSpec = snapAnimationSpec,
                    decayAnimationSpec = decayAnimationSpec,
                    confirmValueChange = confirmValueChange,
                    positionalThreshold = positionalThreshold,
                    velocityThreshold = velocityThreshold
                )
            }
        )
    }
}

private suspend fun <I> restartable(inputs: () -> I, block: suspend (I) -> Unit) {
    try {
        coroutineScope {
            var previousDrag: Job? = null
            snapshotFlow(inputs)
                .collect { latestInputs ->
                    previousDrag?.apply {
                        cancel(SinatraAnchoredDragFinishedSignal())
                        join()
                    }
                    previousDrag = launch(start = CoroutineStart.UNDISPATCHED) {
                        block(latestInputs)
                        this@coroutineScope.cancel(SinatraAnchoredDragFinishedSignal())
                    }
                }
        }
    } catch (exception: Exception) {
        // Ignored
    }
}

private class SinatraAnchoredDragFinishedSignal: CancellationException("IDK")