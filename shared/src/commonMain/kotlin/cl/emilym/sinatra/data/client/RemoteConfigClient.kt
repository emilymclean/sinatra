package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.NoApiUrlException
import org.koin.core.annotation.Factory
import kotlin.coroutines.cancellation.CancellationException

interface RemoteConfigClient {

    @Throws(NoApiUrlException::class, CancellationException::class)
    suspend fun apiUrl(): String

    suspend fun privacyPolicyUrl(): String?
    suspend fun termsUrl(): String?
    suspend fun aboutContentUrl(): String?

    companion object {
        const val API_URL_KEY = "api_url"
        const val PRIVACY_POLICY_URL_KEY = "privacy_policy_url"
        const val TERMS_URL_KEY = "terms_url"
        const val ABOUT_CONTENT_URL_KEY = "about_content_url"
    }

}