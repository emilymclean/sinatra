package cl.emilym.betterbuscanberra.network

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfitBuilder
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory

expect val engine: HttpClientEngine

@Factory
fun ktorDependency() = HttpClient(engine) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

@Factory
fun ktorfitBuilderDependency(
    httpClient: HttpClient
) = ktorfitBuilder {
    httpClient(httpClient)
}

@Factory
fun gtfsApi(
    ktorfitBuilder: Ktorfit.Builder
): GtfsApi {
    return ktorfitBuilder.build {
        baseUrl("https://emilym.cl/gtfs-api/v1/canberra")
    }.createGtfsApi()
}