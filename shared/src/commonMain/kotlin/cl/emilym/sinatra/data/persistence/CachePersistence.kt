package cl.emilym.sinatra.data.persistence

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CachePersistence(
    private val cacheFileWriter: CacheFileWriter
) {

    companion object {
        const val CACHE_INVALIDATION_KEY_FILENAME = "cache-invalidation-key"
    }

    suspend fun save(key: String) {
        withContext(Dispatchers.IO) {
            cacheFileWriter.save(CACHE_INVALIDATION_KEY_FILENAME, key.encodeToByteArray())
        }
    }

    suspend fun get(): String? {
        return withContext(Dispatchers.IO) {
            cacheFileWriter.retrieve(CACHE_INVALIDATION_KEY_FILENAME)?.decodeToString()
        }
    }

}