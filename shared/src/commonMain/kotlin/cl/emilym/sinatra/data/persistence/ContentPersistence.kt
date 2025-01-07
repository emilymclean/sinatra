package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.models.ContentId
import org.koin.core.annotation.Single

@Single
class ContentPersistence {

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