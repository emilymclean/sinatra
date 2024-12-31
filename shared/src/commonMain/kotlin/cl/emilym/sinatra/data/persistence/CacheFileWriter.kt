package cl.emilym.sinatra.data.persistence

interface CacheFileWriter {
    fun save(fileName: String, data: ByteArray)
    fun retrieve(fileName: String): ByteArray?
    fun exists(fileName: String): Boolean
}