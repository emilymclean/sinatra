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

}

@Factory(binds = [RemoteConfigClient::class])
class TemporaryRemoteConfigClient: RemoteConfigClient {

    override suspend fun apiUrl(): String {
        return "https://emilym.cl/gtfs-api/"
    }

    override suspend fun privacyPolicyUrl(): String? {
        return "https://emilym.cl/sinatra/privacy"
    }

    override suspend fun termsUrl(): String? {
        return "https://emilym.cl/sinatra/terms"
    }

    override suspend fun aboutContentUrl(): String? {
        return "https://emilym.cl/assets/content/sinatra/about.md"
    }

}