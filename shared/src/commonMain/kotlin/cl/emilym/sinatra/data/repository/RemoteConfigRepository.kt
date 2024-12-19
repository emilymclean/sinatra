package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.NoApiUrlException
import cl.emilym.sinatra.data.client.RemoteConfigClient
import org.koin.core.annotation.Factory
import kotlin.coroutines.cancellation.CancellationException

@Factory
class RemoteConfigRepository(
    private val remoteConfigClient: RemoteConfigClient
) {

    companion object {
        const val API_URL_KEY = "api_url"
        const val PRIVACY_POLICY_URL_KEY = "privacy_policy_url"
        const val TERMS_URL_KEY = "terms_url"
        const val ABOUT_CONTENT_URL_KEY = "about_content_url"
    }

    @Throws(NoApiUrlException::class, CancellationException::class)
    suspend fun apiUrl(): String {
        return remoteConfigClient.string(API_URL_KEY) ?: throw NoApiUrlException()
    }

    suspend fun privacyPolicyUrl(): String? {
        return remoteConfigClient.string(PRIVACY_POLICY_URL_KEY)
    }

    suspend fun termsUrl(): String? {
        return remoteConfigClient.string(TERMS_URL_KEY)
    }

    suspend fun aboutContentUrl(): String? {
        return remoteConfigClient.string(ABOUT_CONTENT_URL_KEY)
    }

}