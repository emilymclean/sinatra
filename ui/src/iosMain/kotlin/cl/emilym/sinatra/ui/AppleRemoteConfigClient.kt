package cl.emilym.sinatra.ui

import cl.emilym.sinatra.data.client.RemoteConfigWrapper
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


interface RemoteConfigProtocol {

    fun fetch(callback: (Boolean) -> Unit)
    fun string(key: String): String?

}

class AppleRemoteConfigWrapper constructor(
    private val config: RemoteConfigProtocol
): RemoteConfigWrapper {

    private val lock = Mutex()
    private var loaded = false

    private suspend fun fetchAndActivate() {
        return suspendCoroutine { token ->
            config.fetch {
                when (it) {
                    true -> token.resume(Unit)
                    false -> token.resumeWithException(DarwinException())
                }
            }
        }
    }


    private suspend fun load() {
        if (loaded) return
        lock.withLock {
            if (loaded) return
            fetchAndActivate()
            loaded = true
        }
    }

    override suspend fun string(key: String): String? {
        try {
            load()
        } catch(e: Exception) {
            Napier.e(e)
            return null
        }
        return config.string(key)
    }

}