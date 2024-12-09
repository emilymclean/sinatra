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
import cl.emilym.sinatra.data.models.RouteServiceCanonicalTimetable
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfitBuilder
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory
import pbandk.decodeFromByteArray

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
    ktorfitBuilder: Ktorfit.Builder
): GtfsApi {
    return ktorfitBuilder.build {
        baseUrl("https://emilym.cl/gtfs-api/canberra/v1/")
    }.createGtfsApi()
}