package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.client.RemoteConfigClient
import cl.emilym.sinatra.data.models.VersionName
import cl.emilym.sinatra.e
import cl.emilym.sinatra.flagName
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory

@Factory
class RemoteConfigRepository(
    private val remoteConfigClient: RemoteConfigClient
) {

    companion object {
        const val DATA_CACHE_PERIOD_MULTIPLIER_KEY = "data_cache_period_multiplier"
        const val NOMINATIM_API_URL_KEY = "nominatim_api_url"
        const val NOMINATIM_EMAIL_KEY = "nominatim_email"
        const val NOMINATIM_USER_AGENT_KEY = "nominatim_user_agent"
        const val CONTENT_URL_KEY = "content_url"
        const val MINIMUM_VERSION_KEY = "minimum_version"
        const val FEATURE_FLAG_PREFIX = "feature_"
    }

    val loaded: Boolean get() = remoteConfigClient.loaded

    suspend fun load() {
        remoteConfigClient.load()
    }

    suspend fun forceReload() {
        remoteConfigClient.forceReload()
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

    suspend fun dataCachePeriodMultiplier(): Double {
        return remoteConfigClient.number(DATA_CACHE_PERIOD_MULTIPLIER_KEY) ?: 1.0
    }

    suspend fun contentUrl(): String? {
        return remoteConfigClient.string(CONTENT_URL_KEY)
    }

    suspend fun minimumVersion(): VersionName? {
        return remoteConfigClient.string(MINIMUM_VERSION_KEY)
    }

    suspend fun feature(flag: FeatureFlag): Boolean {
        return remoteConfigClient.boolean("$FEATURE_FLAG_PREFIX${flag.flagName}") ?: flag.default
    }

    fun featureImmediate(flag: FeatureFlag): Boolean {
        return try {
            remoteConfigClient.booleanImmediate("$FEATURE_FLAG_PREFIX${flag.flagName}") ?: flag.default
        } catch (e: Exception) {
            Napier.e(e)
            flag.default
        }
    }

}