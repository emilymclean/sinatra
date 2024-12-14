package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.NoApiUrlException
import cl.emilym.sinatra.data.client.RemoteConfigClient
import org.koin.core.annotation.Factory
import kotlin.coroutines.cancellation.CancellationException

@Factory
class RemoteConfigRepository(
    private val remoteConfigClient: RemoteConfigClient
) {

    @Throws(NoApiUrlException::class, CancellationException::class)
    suspend fun apiUrl(): String {
        return remoteConfigClient.apiUrl()
    }

    suspend fun privacyPolicyUrl(): String? {
        return remoteConfigClient.privacyPolicyUrl()
    }

    suspend fun termsUrl(): String? {
        return remoteConfigClient.termsUrl()
    }

    suspend fun aboutContentUrl(): String? {
        return remoteConfigClient.aboutContentUrl()
    }

}