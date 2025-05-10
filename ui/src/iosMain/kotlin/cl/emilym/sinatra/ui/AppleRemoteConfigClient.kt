package cl.emilym.sinatra.ui

import cl.emilym.sinatra.data.client.RemoteConfigWrapper
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.Foundation.NSNumber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


interface RemoteConfigProtocol {

    fun fetch(forced: Boolean, callback: (Boolean) -> Unit)
    fun exists(key: String): Boolean
    fun string(key: String): String?
    fun number(key: String): NSNumber?
    fun boolean(key: String): Boolean?

}

class AppleRemoteConfigWrapper(
    private val config: RemoteConfigProtocol
): RemoteConfigWrapper {

    private val lock = Mutex()
    private var _loaded = false
    override val loaded get() = _loaded

    private suspend fun fetchAndActivate(forced: Boolean = false) {
        return suspendCoroutine { token ->
            config.fetch(forced) {
                when (it) {
                    true -> token.resume(Unit)
                    false -> token.resumeWithException(DarwinException())
                }
            }
        }
    }

    override suspend fun load() {
        if (_loaded) return
        lock.withLock {
            if (_loaded) return
            fetchAndActivate()
            _loaded = true
        }
    }

    override suspend fun forceReload() {
        _loaded = false
        lock.withLock {
            fetchAndActivate(forced = true)
            _loaded = true
        }
    }

    override fun exists(key: String) = config.exists(key)

    override fun string(key: String): String {
        return config.string(key) ?: throw DarwinException()
    }

    override fun number(key: String): Double {
        return config.number(key)?.doubleValue ?: throw DarwinException()
    }

    override fun boolean(key: String): Boolean {
        return config.boolean(key) ?: throw DarwinException()
    }
}