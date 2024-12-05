package cl.emilym.sinatra.data.models

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