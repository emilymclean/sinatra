package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.models.ContentId
import org.koin.core.annotation.Single

@Single
class ContentPersistence {

    private val contents = mutableMapOf<String, Content>()

    fun get(id: ContentId): Content? {
        return contents[id]
    }

    fun store(id: ContentId, content: Content) {
        contents[id] = content
    }

}