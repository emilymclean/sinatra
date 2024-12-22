package cl.emilym.sinatra.data.persistence

import android.content.Context
import org.koin.core.annotation.Factory
import java.io.File

@Factory(binds = [CacheFileWriter::class])
class AndroidCacheFileWriter(
    private val context: Context
): CacheFileWriter {
    override fun save(fileName: String, data: ByteArray) {
        val file = File(context.cacheDir, fileName)
        file.delete()
        file.createNewFile()
        file.writeBytes(data)
    }

    override fun retrieve(fileName: String): ByteArray? {
        val file = File(context.cacheDir, fileName)
        if (!file.exists()) return null
        return file.readBytes()
    }

    override fun exists(fileName: String): Boolean {
        return File(context.cacheDir, fileName).exists()
    }
}