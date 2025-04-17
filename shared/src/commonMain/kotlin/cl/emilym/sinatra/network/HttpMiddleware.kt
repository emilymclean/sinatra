package cl.emilym.sinatra.network

import cl.emilym.gtfs.RouteCanonicalTimetableEndpoint
import cl.emilym.gtfs.RouteDetailEndpoint
import cl.emilym.gtfs.RouteEndpoint
import cl.emilym.gtfs.RouteHeadingsEndpoint
import cl.emilym.gtfs.RouteServicesEndpoint
import cl.emilym.gtfs.RouteTimetableEndpoint
import cl.emilym.gtfs.RouteTripTimetableEndpoint
import cl.emilym.gtfs.ServiceAlertEndpoint
import cl.emilym.gtfs.ServiceEndpoint
import cl.emilym.gtfs.StopDetailEndpoint
import cl.emilym.gtfs.StopEndpoint
import cl.emilym.gtfs.StopRoutesEndpoint
import cl.emilym.gtfs.StopTimetable
import cl.emilym.gtfs.content.Pages
import cl.emilym.sinatra.BuildKonfig
import cl.emilym.sinatra.data.repository.LocaleRepository
import com.google.transit.realtime.FeedMessage
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
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory
import pbandk.decodeFromByteArray

expect val engine: HttpClientEngine

fun loggingInterceptor(): suspend Sender.(HttpRequestBuilder) -> HttpClientCall {
    return { request ->
        Napier.d("Requesting ${request.url} (with language = ${request.headers["Accept-Language"]})", tag = "Http")
        execute(request)
    }
}

fun languagesHeader(
    localeRepository: LocaleRepository
): suspend Sender.(HttpRequestBuilder) -> HttpClientCall {
    return { request ->
        request.header("Accept-Language", localeRepository.acceptedLanguages)
        execute(request)
    }
}

@Factory
fun ktorDependency(
    localeRepository: LocaleRepository
) = HttpClient(engine) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}.apply {
    plugin(HttpSend).intercept(languagesHeader(localeRepository))
    plugin(HttpSend).intercept(loggingInterceptor())
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
            RouteTripTimetableEndpoint::class to RouteTripTimetableEndpoint::decodeFromByteArray,
            Pages::class to Pages::decodeFromByteArray,
            FeedMessage::class to FeedMessage::decodeFromByteArray,
            ServiceAlertEndpoint::class to ServiceAlertEndpoint::decodeFromByteArray,
            RouteHeadingsEndpoint::class to RouteHeadingsEndpoint::decodeFromByteArray,
            StopRoutesEndpoint::class to StopRoutesEndpoint::decodeFromByteArray,
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
        baseUrl(BuildKonfig.apiUrl)
    }.createGtfsApi()
}

@Factory
fun nominatimApi(
    ktorfitBuilder: Ktorfit.Builder
): NominatimApi {
    return ktorfitBuilder.build {}.createNominatimApi()
}