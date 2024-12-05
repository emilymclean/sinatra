package cl.emilym.sinatra.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual val engine: HttpClientEngine
    get() = Darwin.create {  }