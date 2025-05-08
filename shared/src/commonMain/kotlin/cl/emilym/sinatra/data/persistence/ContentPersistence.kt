package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.models.ContentId
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.data.repository.ContentRepository
import cl.emilym.sinatra.room.dao.ContentDao
import cl.emilym.sinatra.room.dao.ContentLinkDao
import cl.emilym.sinatra.room.entities.ContentEntity
import cl.emilym.sinatra.room.entities.ContentLinkEntity
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Single

@Single
class ContentPersistence(
    private val contentDao: ContentDao,
    private val contentLinkDao: ContentLinkDao
) {

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

    private val banners = mutableMapOf<String, Alert>()

    private var _cached = false
    val cached: Boolean get() = _cached

    suspend fun get(id: ContentId): Content? {
        Napier.d("${contentLinkDao.get(id)}")
        return (contentDao.get(id)?.toModel() ?: FALLBACK_CONTENT[id]).also {
            Napier.d("$it")
        }
    }

    suspend fun getBanner(id: String): Alert? {
        return banners[id]
    }

    suspend fun store(content: List<Content>) {
        val contentEntities = content.map { ContentEntity.fromModel(it) }.toTypedArray()
        val linkEntities = content
            .flatMap { ContentLinkEntity.fromModel(it) }
            .mapIndexed { index, it -> it.copy(id = index + 1) }
            .toTypedArray()
        contentDao.clear()
        contentDao.insert(*contentEntities)
        contentLinkDao.insert(*linkEntities)
        _cached = true
    }

    suspend fun storeBanner(banners: Map<String, Alert>) {
        this.banners.putAll(banners)
    }

}