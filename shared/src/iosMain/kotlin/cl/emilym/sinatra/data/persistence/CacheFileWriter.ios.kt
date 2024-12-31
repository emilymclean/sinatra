package cl.emilym.sinatra.data.persistence

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import org.koin.core.annotation.Factory
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.posix.memcpy

val cacheDirectory: String
    get() {
        val paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
        return paths.first() as String
    }

@Factory(binds = [CacheFileWriter::class])
class AppleCacheFileWriter: CacheFileWriter {

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override fun save(fileName: String, data: ByteArray) {
        val filePath = "$cacheDirectory/$fileName"
        memScoped {
            val nsData = NSData.create(bytes = allocArrayOf(data), length = data.size.toULong())
            nsData.writeToFile(filePath, true)
        }
    }

    @OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
    override fun retrieve(fileName: String): ByteArray? {
        val filePath = "$cacheDirectory/$fileName"
        val nsData = NSData.create(contentsOfFile = filePath) ?: return null
        return ByteArray(nsData.length.toInt()).apply {
            usePinned {
                memcpy(it.addressOf(0), nsData.bytes, nsData.length)
            }
        }
    }

    override fun exists(fileName: String): Boolean {
        val filePath = "$cacheDirectory/$fileName"
        return NSFileManager.defaultManager.fileExistsAtPath(filePath)
    }

}