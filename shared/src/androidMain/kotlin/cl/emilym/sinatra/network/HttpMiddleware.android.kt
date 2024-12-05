package cl.emilym.sinatra.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual val engine: HttpClientEngine
    get() = OkHttp.create { }