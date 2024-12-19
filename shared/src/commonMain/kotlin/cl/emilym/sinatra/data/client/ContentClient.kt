package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.network.GtfsApi
import org.koin.core.annotation.Factory

@Factory
class ContentClient(
    private val gtfsApi: GtfsApi
) {

    suspend fun content(): List<Content> {
        val response = gtfsApi.content()
        return response.pages.map { Content.fromPB(it) }
    }

}