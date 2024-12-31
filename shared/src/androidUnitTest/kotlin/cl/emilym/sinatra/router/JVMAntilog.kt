package cl.emilym.sinatra.router

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

class JVMAntilog: Antilog() {
    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        System.out.println("${priority.name} - $message")
        throwable?.printStackTrace()
    }
}