package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.NetworkGraphRepository
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.RoutingPreferencesRepository
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.data.repository.startOfDay
import cl.emilym.sinatra.router.Raptor
import cl.emilym.sinatra.router.RaptorConfig
import cl.emilym.sinatra.router.RaptorJourneyConnection
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Factory
class CalculateJourneyUseCase(
    private val networkGraphRepository: NetworkGraphRepository,
    private val activeServicesUseCase: ActiveServicesUseCase,
    private val stopRepository: StopRepository,
    private val routeRepository: RouteRepository,
    private val clock: Clock,
    private val transportMetadataRepository: TransportMetadataRepository,
    private val routingPreferencesRepository: RoutingPreferencesRepository,
) {

    suspend operator fun invoke(
        departureLocation: MapLocation,
        arrivalLocation: MapLocation,
        departureTime: Instant = clock.now()
    ): Journey {
        return withContext(Dispatchers.IO) {
            val now = departureTime
            val graph = networkGraphRepository.networkGraph()
            val stops = stopRepository.stops()
            val services = activeServicesUseCase(now).map { it.map { it.id } }
            val maximumWalkingTime = routingPreferencesRepository.maximumWalkingTime()

            Napier.d("Active services = ${services}")

            val departureStop = stops.map { it.minBy { distance(departureLocation, it.location) } }.item
            val arrivalStop = stops.map { it.minBy { distance(arrivalLocation, it.location) } }.item

            val departureTime = departureTime + (
                    distance(departureLocation, departureStop.location) *
                            graph.item.metadata.assumedWalkingSecondsPerKilometer.toInt()
                    ).seconds

            val raptor = Raptor(
                graph.item,
                services.item,
                RaptorConfig(
                    maximumWalkingTime = maximumWalkingTime.inWholeSeconds,
                    transferPenalty = 5.minutes.inWholeSeconds
                )
            )

            val departureTimeSeconds = (departureTime - clock.startOfDay(transportMetadataRepository.timeZone())).inWholeSeconds
            val raptorJourney =
                raptor.calculate(
                    departureTimeSeconds,
                    departureStop.id,
                    arrivalStop.id
                )

            val routes = routeRepository.routes(
                raptorJourney.connections.filterIsInstance<RaptorJourneyConnection.Travel>().map { it.routeId }
            )
            val legs = mutableListOf<JourneyLeg>()
            var lastEndTime = departureTimeSeconds.seconds
            for (i in raptorJourney.connections.indices) {
                val stops = raptorJourney.connections[i].stops.mapNotNull { s -> stops.item.firstOrNull { it.id == s } }
                legs += when (val connection = raptorJourney.connections[i]) {
                    is RaptorJourneyConnection.Travel -> JourneyLeg.Travel(
                        stops,
                        (connection.endTime - connection.startTime).seconds,
                        routes.item.first { it?.id == connection.routeId }!!,
                        connection.heading,
                        connection.startTime.seconds,
                        connection.endTime.seconds
                    )
                    is RaptorJourneyConnection.Transfer -> JourneyLeg.Transfer(
                        stops,
                        connection.travelTime.seconds,
                        lastEndTime,
                        lastEndTime + connection.travelTime.seconds
                    )
                }.also { lastEndTime = it.arrivalTime }
            }

            Journey(legs)
        }
    }

}