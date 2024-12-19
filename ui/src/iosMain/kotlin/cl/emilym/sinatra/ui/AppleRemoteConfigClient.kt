package cl.emilym.sinatra.ui

import cl.emilym.sinatra.data.client.RemoteConfigWrapper
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.Foundation.NSNumber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


interface RemoteConfigProtocol {

    fun fetch(callback: (Boolean) -> Unit)
    fun exists(key: String): Boolean
    fun string(key: String): String?
    fun number(key: String): NSNumber?
    fun boolean(key: String): Boolean?

}

class AppleRemoteConfigWrapper(
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


    override suspend fun load() {
        if (loaded) return
        lock.withLock {
            if (loaded) return
            fetchAndActivate()
            loaded = true
        }
    }

    override suspend fun string(key: String): String {
        return config.string(key) ?: throw DarwinException()
    }
    override suspend fun exists(key: String) = config.exists(key)
    override suspend fun number(key: String): Double {
        return config.number(key)?.doubleValue ?: throw DarwinException()
    }

    override suspend fun boolean(key: String): Boolean {
        return config.boolean(key) ?: throw DarwinException()
    }
}