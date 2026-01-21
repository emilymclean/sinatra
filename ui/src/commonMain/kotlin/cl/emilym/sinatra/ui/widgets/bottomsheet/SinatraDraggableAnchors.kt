package cl.emilym.sinatra.ui.widgets.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.DraggableAnchors
import kotlin.math.abs

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