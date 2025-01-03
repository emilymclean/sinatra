package cl.emilym.sinatra.data.persistence

import android.content.Context
import org.koin.core.annotation.Factory
import java.io.File

@Factory(binds = [CacheFileWriter::class])
class AndroidCacheFileWriter(
    private val context: Context
): CacheFileWriter {
    override fun save(fileName: String, data: ByteArray) {
        val file = File(context.filesDir, fileName)
        file.writeBytes(data)
    }

    override fun retrieve(fileName: String): ByteArray? {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return null
        return file.readBytes()
    }

    override fun exists(fileName: String): Boolean {
        return File(context.filesDir, fileName).exists()
    }
}