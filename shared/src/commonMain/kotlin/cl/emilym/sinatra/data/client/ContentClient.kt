package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.AlertSeverity
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.models.Pages
import cl.emilym.sinatra.network.GtfsApi
import org.koin.core.annotation.Factory

@Factory
class ContentClient(
    private val gtfsApi: GtfsApi
) {

    suspend fun content(): Pages {
        val response = gtfsApi.content()
        return Pages(
            response.pages.map { Content.fromPB(it) },
            response.banners.mapNotNull {
                val k = it.key ?: return@mapNotNull null
                val v = it.value ?: return@mapNotNull null
                k to Alert.fromContentPB(v)
            }.toMap()
        )
    }

}