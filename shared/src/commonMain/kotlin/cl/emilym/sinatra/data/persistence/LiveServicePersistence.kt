package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.lib.periodicFlow
import com.google.transit.realtime.FeedMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.minutes

@Single
class LiveServicePersistence constructor() {

    companion object {

        val REFRESH_RATE = 1.minutes

    }

    private val lock = Mutex()
    private val tracked = mutableMapOf<String, SharedFlow<Result<FeedMessage>>>()

    suspend fun get(url: String, create: (Flow<Unit>) -> SharedFlow<Result<FeedMessage>>): Flow<FeedMessage> {
        tracked[url]?.let { return@let it.throwIfNeeded() }
        return lock.withLock {
            val current = tracked[url]
            if (current != null) return@withLock current.throwIfNeeded()
            create(periodicFlow(REFRESH_RATE)).also {
                tracked[url] = it
            }.throwIfNeeded()
        }
    }

}

fun <T> Flow<Result<T>>.throwIfNeeded() = map {
    it.getOrThrow()
}