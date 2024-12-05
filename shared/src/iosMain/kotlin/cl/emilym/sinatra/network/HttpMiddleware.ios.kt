package cl.emilym.sinatra.network

import io.ktor.client.engine.HttpClientEngine

actual val engine: HttpClientEngine
    get() = Darwin.create {  }