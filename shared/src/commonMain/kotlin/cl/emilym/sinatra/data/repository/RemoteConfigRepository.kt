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
        const val DATA_CACHE_PERIOD_MULTIPLIER_KEY = "data_cache_period_multiplier"
        const val NOMINATIM_API_URL_KEY = "nominatim_api_url"
        const val NOMINATIM_EMAIL_KEY = "nominatim_email"
        const val NOMINATIM_USER_AGENT_KEY = "nominatim_user_agent"
    }

    @Throws(NoApiUrlException::class, CancellationException::class)
    suspend fun apiUrl(): String {
        return remoteConfigClient.string(API_URL_KEY) ?: throw NoApiUrlException()
    }

    suspend fun nominatimUrl(): String? {
        return remoteConfigClient.string(NOMINATIM_API_URL_KEY)
    }

    suspend fun nominatimEmail(): String? {
        return remoteConfigClient.string(NOMINATIM_EMAIL_KEY)
    }

    suspend fun nominatimUserAgent(): String? {
        return remoteConfigClient.string(NOMINATIM_USER_AGENT_KEY)
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

    suspend fun dataCachePeriodMultiplier(): Double {
        return remoteConfigClient.number(DATA_CACHE_PERIOD_MULTIPLIER_KEY) ?: 1.0
    }

}