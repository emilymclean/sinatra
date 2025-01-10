package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.ContentClient
import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.models.ContentId
import cl.emilym.sinatra.data.persistence.ContentPersistence
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Factory

@Factory
class ContentRepository(
    private val contentClient: ContentClient,
    private val contentPersistence: ContentPersistence,
    private val remoteConfigRepository: RemoteConfigRepository
) {

    companion object {
        const val ABOUT_ID = "about"
        const val HOME_BANNER_ID = "home"
    }

    private val lock = Mutex()

    private suspend fun load() {
        if (contentPersistence.cached) return
        lock.withLock {
            if (contentPersistence.cached) return
            val content = contentClient.content(
                remoteConfigRepository.contentUrl()
            )
            contentPersistence.store(content.pages)
            contentPersistence.storeBanner(content.banner)
        }
    }

    suspend fun content(id: ContentId): Content? {
        load()
        return contentPersistence.get(id)
    }

    suspend fun banner(id: String): Alert? {
        load()
        return contentPersistence.getBanner(id)
    }

}