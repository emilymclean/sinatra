package cl.emilym.sinatra.lib

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

interface MemoryCache<T> {
    suspend fun get(): T
}

abstract class AbstractMemoryCache<T>: MemoryCache<T> {
    private val lock = Mutex()
    private var memo: T? = null

    protected abstract val expired: Boolean

    abstract suspend fun fetch(): T

    override suspend fun get(): T {
        if (!expired) memo?.let { return it }
        return lock.withLock {
            if (!expired) memo?.let { return it }

            fetch().also {
                memo = it
            }
        }
    }
}

class TTLMemoryCache<T>(
    private val ttl: Duration = 1.minutes,
    private val clock: Clock = Clock.System,
    private val fetch: suspend () -> T
): AbstractMemoryCache<T>() {

    private var expireTime: Instant = Instant.DISTANT_PAST
    override val expired: Boolean = clock.now() > expireTime

    override suspend fun fetch(): T {
        return fetch.invoke().also {
            expireTime = clock.now() + ttl
        }
    }

}