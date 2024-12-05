package cl.emilym.sinatra.data.models

import kotlin.math.max

enum class CacheState(
    val live: Boolean,
    val expired: Boolean
) {
    LIVE(true, false), CACHED(false, false), EXPIRED_CACHE(false, true)
}

data class Cachable<T>(
    val item: T,
    val state: CacheState
) {

    companion object {
        fun <T> live(item: T): Cachable<T> {
            return Cachable(item, CacheState.LIVE)
        }
    }

}

fun List<CacheState>.combine() = CacheState.entries[fold(0) { acc, s ->
    max(acc, s.ordinal)
}]

inline fun <T,R> Cachable<T>.map(operation: (T) -> R): Cachable<R> {
    return Cachable(
        operation(item),
        state
    )
}

inline fun <T,R> Cachable<T>.flatMap(operation: (T) -> Cachable<R>): Cachable<R> {
    val out = operation(item)
    return Cachable(
        out.item,
        listOf(state, out.state).combine()
    )
}

inline fun <T,J,R> Cachable<T>.merge(other: Cachable<J>, operation: (T,J) -> R): Cachable<R> {
    val forwardingState = listOf(state, other.state).combine()
    return Cachable(
        operation(this.item, other.item),
        forwardingState
    )
}