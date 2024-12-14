package cl.emilym.sinatra.android

import android.util.Log
import cl.emilym.sinatra.NoApiUrlException
import cl.emilym.sinatra.data.client.RemoteConfigClient
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

@Single
class RemoteConfigWrapper(
    private val config: FirebaseRemoteConfig
) {

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

    suspend fun string(key: String): String? {
        try {
            load()
        } catch(e: Exception) {
            Log.e("RemoteConfigWrapper", e.message, e)
            return null
        }
        return config.getString(key)
    }

}

@Factory(binds = [RemoteConfigClient::class])
class AndroidRemoteConfigClient(
    private val wrapper: RemoteConfigWrapper
): RemoteConfigClient {

    override suspend fun apiUrl(): String {
        return wrapper.string(RemoteConfigClient.API_URL_KEY) ?: throw NoApiUrlException.default()
    }

    override suspend fun privacyPolicyUrl(): String? {
        return wrapper.string(RemoteConfigClient.PRIVACY_POLICY_URL_KEY)
    }

    override suspend fun termsUrl(): String? {
        return wrapper.string(RemoteConfigClient.TERMS_URL_KEY)
    }

    override suspend fun aboutContentUrl(): String? {
        return wrapper.string(RemoteConfigClient.ABOUT_CONTENT_URL_KEY)
    }
}