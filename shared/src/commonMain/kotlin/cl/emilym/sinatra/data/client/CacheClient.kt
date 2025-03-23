package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.network.GtfsApi
import org.koin.core.annotation.Factory

@Factory
class CacheClient(
    private val gtfsApi: GtfsApi
) {

    suspend fun cacheInvalidationKey(): String = gtfsApi.cacheInvalidationKey()

}