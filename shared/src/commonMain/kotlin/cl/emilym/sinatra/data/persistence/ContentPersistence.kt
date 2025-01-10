package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.models.ContentId
import org.koin.core.annotation.Single

@Single
class ContentPersistence(
    private val cacheFileWriter: CacheFileWriter
) {

    companion object {
        const val CONTENT_FILENAME = "content.pb"
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

    suspend fun save(data: ByteArray) {
        cacheFileWriter.save(CONTENT_FILENAME, data)
    }

    suspend fun exists(): Boolean {
        return cacheFileWriter.exists(CONTENT_FILENAME)
    }

    suspend fun restore() {
        return cacheFileWriter.retrieve(CONTENT_FILENAME)
    }

}