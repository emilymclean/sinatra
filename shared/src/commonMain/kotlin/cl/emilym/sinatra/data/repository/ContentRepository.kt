package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.models.ContentId
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.network.GtfsApi
import org.koin.core.annotation.Factory

@Factory
class ContentRepository(
    private val remoteConfigRepository: RemoteConfigRepository,
    // Shouldn't be accessing this directly, but it's kinda a quick things so it's fine
    private val gtfsApi: GtfsApi
) {

    companion object {
        const val ABOUT_ID = "about"
    }

    private suspend fun aboutUsContent(): Content? {
        val contentUrl = remoteConfigRepository.aboutContentUrl() ?: return null
        val termsUrl = remoteConfigRepository.termsUrl()
        val privacyUrl = remoteConfigRepository.privacyPolicyUrl()
        val content = gtfsApi.markdownContent(contentUrl)

        return Content(
            "About",
            content,
            listOfNotNull(
                ContentLink.external("Terms & Conditions", termsUrl),
                ContentLink.external("Privacy Policy", privacyUrl)
            )
        )
    }

    suspend fun content(id: ContentId): Content? {
        return when (id) {
            ABOUT_ID -> aboutUsContent()
            else -> null
        }
    }

}