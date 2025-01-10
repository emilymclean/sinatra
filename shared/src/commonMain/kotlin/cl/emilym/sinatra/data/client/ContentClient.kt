package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.AlertSeverity
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.models.Pages
import cl.emilym.sinatra.data.models.ShaDigest
import cl.emilym.sinatra.data.repository.Platform
import cl.emilym.sinatra.data.repository.isAndroid
import cl.emilym.sinatra.data.repository.isIos
import cl.emilym.sinatra.network.GtfsApi
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory

@Factory
class ContentClient(
    private val gtfsApi: GtfsApi
) {

    fun contentPair(url: String? = null) = object : EndpointDigestPair<Pages>() {
        override val endpoint: suspend () -> Pages = { content(url) }
        override val digest: suspend () -> ShaDigest = { contentDigest(url) }
    }

    suspend fun content(url: String? = null): Pages {
        val response = when {
            url != null -> gtfsApi.contentDynamic(url)
            isIos -> gtfsApi.contentIos()
            isAndroid -> gtfsApi.contentAndroid()
            else -> gtfsApi.content()
        }
        return Pages(
            response.pages.map { Content.fromPB(it) },
            response.banners.mapNotNull {
                val k = it.key ?: return@mapNotNull null
                val v = it.value ?: return@mapNotNull null
                k to Alert.fromContentPB(v)
            }.toMap()
        )
    }

    suspend fun contentDigest(url: String? = null): ShaDigest {
        return when {
            url != null -> gtfsApi.contentDynamicDigest("$url.sha")
            isIos -> gtfsApi.contentIosDigest()
            isAndroid -> gtfsApi.contentAndroidDigest()
            else -> gtfsApi.contentDigest()
        }
    }

}