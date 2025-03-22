package cl.emilym.sinatra.android

import cl.emilym.sinatra.data.client.RemoteConfigWrapper
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

@Factory
fun remoteConfig() = Firebase.remoteConfig

@Single(binds = [RemoteConfigWrapper::class])
class AndroidRemoteConfigWrapper(
    private val config: FirebaseRemoteConfig
): RemoteConfigWrapper {

    private val lock = Mutex()
    private var _loaded = false
    override val loaded: Boolean
        get() = _loaded

    override suspend fun load() {
        if (_loaded) return
        lock.withLock {
            if (_loaded) return
            config.fetchAndActivate().await()
            _loaded = true
        }
    }

    override fun exists(key: String): Boolean {
        return config.getKeysByPrefix("").contains(key)
    }

    override fun string(key: String): String {
        return config.getString(key)
    }

    override fun number(key: String): Double {
        return config.getDouble(key)
    }

    override fun boolean(key: String): Boolean {
        return config.getBoolean(key)
    }

}