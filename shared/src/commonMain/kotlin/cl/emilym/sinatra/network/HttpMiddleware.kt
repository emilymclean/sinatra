package cl.emilym.sinatra.network

import cl.emilym.gtfs.RouteCanonicalTimetableEndpoint
import cl.emilym.gtfs.RouteDetailEndpoint
import cl.emilym.gtfs.RouteEndpoint
import cl.emilym.gtfs.RouteServicesEndpoint
import cl.emilym.gtfs.RouteTimetableEndpoint
import cl.emilym.gtfs.RouteTripTimetableEndpoint
import cl.emilym.gtfs.ServiceEndpoint
import cl.emilym.gtfs.StopDetailEndpoint
import cl.emilym.gtfs.StopEndpoint
import cl.emilym.gtfs.StopTimetable
import cl.emilym.sinatra.NoApiUrlException
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfitBuilder
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.Sender
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory
import pbandk.decodeFromByteArray

const val TEMPORARY_URL_GTFS = "replaceable-main-api.com"
const val TEMPORARY_URL_NOMINATIM = "replaceable-nomatim.com"

expect val engine: HttpClientEngine

fun urlReplaceInterceptor(
    remoteConfigRepository: RemoteConfigRepository
): suspend Sender.(HttpRequestBuilder) -> HttpClientCall {
    return { request ->
        when(request.url.host) {
            TEMPORARY_URL_GTFS -> {
                val realUrl = remoteConfigRepository.apiUrl()
                request.url(request.url.buildString().replace(TEMPORARY_URL_GTFS, realUrl))
            }
            TEMPORARY_URL_NOMINATIM -> {
                val realUrl = remoteConfigRepository.nominatimUrl()
                request.url(request.url.buildString().replace(TEMPORARY_URL_NOMINATIM, realUrl ?: throw NoApiUrlException()))
            }
        }
        execute(request)
    }
}

@Factory
fun ktorDependency(
    remoteConfigRepository: RemoteConfigRepository
) = HttpClient(engine) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}.apply {
    plugin(HttpSend).intercept(urlReplaceInterceptor(remoteConfigRepository))
}

@Factory
fun protobufResponseConverterFactory(): ProtobufResponseConverterFactory {
    return ProtobufResponseConverterFactory(
        mapOf(
            RouteEndpoint::class to RouteEndpoint::decodeFromByteArray,
            StopEndpoint::class to StopEndpoint::decodeFromByteArray,
            StopDetailEndpoint::class to StopDetailEndpoint::decodeFromByteArray,
            RouteDetailEndpoint::class to RouteDetailEndpoint::decodeFromByteArray,
            ServiceEndpoint::class to ServiceEndpoint::decodeFromByteArray,
            RouteTimetableEndpoint::class to RouteTimetableEndpoint::decodeFromByteArray,
            RouteServicesEndpoint::class to RouteServicesEndpoint::decodeFromByteArray,
            StopTimetable::class to StopTimetable::decodeFromByteArray,
            RouteCanonicalTimetableEndpoint::class to RouteCanonicalTimetableEndpoint::decodeFromByteArray,
            RouteTripTimetableEndpoint::class to RouteTripTimetableEndpoint::decodeFromByteArray
        )
    )
}

@Factory
fun ktorfitBuilderDependency(
    httpClient: HttpClient,
    protobufResponseConverterFactory: ProtobufResponseConverterFactory
) = ktorfitBuilder {
    httpClient(httpClient)
    converterFactories(protobufResponseConverterFactory)
}

@Factory
fun gtfsApi(
    ktorfitBuilder: Ktorfit.Builder,
): GtfsApi {
    return ktorfitBuilder.build {
        baseUrl("https://$TEMPORARY_URL_GTFS/canberra/v1/")
    }.createGtfsApi()
}

@Factory
fun nominatimApi(
    ktorfitBuilder: Ktorfit.Builder
): NominatimApi {
    return ktorfitBuilder.build {
        baseUrl("https://$TEMPORARY_URL_NOMINATIM/")
    }.createNominatimApi()
}