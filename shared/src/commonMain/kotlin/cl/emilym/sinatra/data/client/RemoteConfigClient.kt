package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.NoApiUrlException
import org.koin.core.annotation.Factory
import kotlin.coroutines.cancellation.CancellationException

interface RemoteConfigClient {

    @Throws(NoApiUrlException::class, CancellationException::class)
    suspend fun apiUrl(): String
    suspend fun nominatimUrl(): String?

    suspend fun privacyPolicyUrl(): String?
    suspend fun termsUrl(): String?
    suspend fun aboutContentUrl(): String?

    companion object {
        const val API_URL_KEY = "api_url"
        const val NOMINATIM_API_URL_KEY = "nominatim_api_url"
        const val PRIVACY_POLICY_URL_KEY = "privacy_policy_url"
        const val TERMS_URL_KEY = "terms_url"
        const val ABOUT_CONTENT_URL_KEY = "about_content_url"
    }

}

interface RemoteConfigWrapper {
    suspend fun string(key: String): String?
}

@Factory(binds = [RemoteConfigClient::class])
class DefaultRemoteConfigClient(
    private val wrapper: RemoteConfigWrapper
): RemoteConfigClient {

    override suspend fun apiUrl(): String {
        return wrapper.string(RemoteConfigClient.API_URL_KEY) ?: throw NoApiUrlException.default()
    }

    override suspend fun nominatimUrl(): String? {
        return wrapper.string(RemoteConfigClient.NOMINATIM_API_URL_KEY) ?: "nominatim.openstreetmap.org"
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