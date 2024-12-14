package cl.emilym.sinatra.android

import android.util.Log
import cl.emilym.sinatra.NoApiUrlException
import cl.emilym.sinatra.data.client.RemoteConfigClient
import cl.emilym.sinatra.data.client.RemoteConfigWrapper
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.remoteConfig
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
    private var loaded = false

    private suspend fun load() {
        if (loaded) return
        lock.withLock {
            if (loaded) return
            config.fetchAndActivate().await()
            loaded = true
        }
    }

    override suspend fun string(key: String): String? {
        try {
            load()
        } catch(e: Exception) {
            Log.e("RemoteConfigWrapper", e.message, e)
            return null
        }
        return config.getString(key)
    }

}