package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.models.ContentId
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.data.repository.ContentRepository
import org.koin.core.annotation.Single

@Single
class ContentPersistence {

    companion object {
        val FALLBACK_CONTENT = mapOf(
            ContentRepository.MORE_ID to Content(
                ContentRepository.MORE_ID,
                "More",
                "Unable to load content, some options may be unavailable.",
                listOf(
                    ContentLink.Native(
                        "Settings",
                        ContentRepository.NATIVE_PREFERENCES_ID,
                        0
                    ),
                    ContentLink.Content(
                        "Service Updates",
                        ContentRepository.SERVICE_ALERT_ID,
                        1
                    )
                )
            ),
            ContentRepository.SERVICE_ALERT_ID to Content(
                ContentRepository.SERVICE_ALERT_ID,
                "Service Updates",
                "",
                listOf()
            )
        )
    }

    private val contents = mutableMapOf<String, Content>()
    private val banners = mutableMapOf<String, Alert>()
    val cached: Boolean get() = contents.isNotEmpty()

    fun get(id: ContentId): Content? {
        return contents[id]
    }

    fun getBanner(id: String): Alert? {
        return banners[id]
    }

    fun store(content: List<Content>) {
        content.forEach { contents[it.id] = it }
    }

    fun storeBanner(banners: Map<String, Alert>) {
        this.banners.putAll(banners)
    }

}