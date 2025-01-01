package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopWithDistance
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
import cl.emilym.sinatra.router.RaptorStop
import cl.emilym.sinatra.router.Seconds
import cl.emilym.sinatra.router.data.NetworkGraph
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class JourneyLocation(
    val location: MapLocation,
    val exact: Boolean
)

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

    private lateinit var graph: Cachable<NetworkGraph>

    suspend operator fun invoke(
        departureLocation: JourneyLocation,
        arrivalLocation: JourneyLocation,
        departureTime: Instant = clock.now()
    ): Journey {
        return withContext(Dispatchers.IO) {
            val now = departureTime
            graph = networkGraphRepository.networkGraph()
            val stops = stopRepository.stops()
            val services = activeServicesUseCase(now).map { it.map { it.id } }
            val maximumWalkingTime = routingPreferencesRepository.maximumWalkingTime()

            Napier.d("Active services = ${services}")

            val departureStops = stops.map { it.nearbyStops(departureLocation, maximumWalkingTime) }
            val arrivalStops = stops.item.nearbyStops(arrivalLocation, maximumWalkingTime)

            val raptor = Raptor(
                graph.item,
                services.item,
                RaptorConfig(
                    maximumWalkingTime = maximumWalkingTime.inWholeSeconds,
                    transferPenalty = 5.minutes.inWholeSeconds
                )
            )

            val departureTimeSeconds: Seconds = (departureTime - clock.startOfDay(transportMetadataRepository.timeZone())).inWholeSeconds
            val raptorJourney =
                raptor.calculate(
                    departureTimeSeconds,
                    departureStops.item,
                    arrivalStops
                )

            val routes = routeRepository.routes(
                raptorJourney.connections.filterIsInstance<RaptorJourneyConnection.Travel>().map { it.routeId }
            )
            var legs = mutableListOf<JourneyLeg>()
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

            if (!departureLocation.exact) {
                run {
                    legs = legs.dropWhile { it is JourneyLeg.Transfer }.toMutableList()
                    if (legs.isEmpty()) return@run
                    val attachedStop = (legs.first { it is JourneyLeg.RouteJourneyLeg } as JourneyLeg.RouteJourneyLeg).stops.first()
                    val time = distance(departureLocation.location, attachedStop.location) * graph.item.metadata.assumedWalkingSecondsPerKilometer.toLong()
                    legs.add(0, JourneyLeg.TransferPoint(
                        time.seconds,
                        departureTimeSeconds.seconds,
                        (departureTimeSeconds + time).seconds
                    ))
                }
            }

            if (!arrivalLocation.exact) {
                run {
                    legs = legs.dropLastWhile { it is JourneyLeg.Transfer }.toMutableList()
                    if (legs.isEmpty()) return@run
                    val lastLeg = legs.last { it is JourneyLeg.RouteJourneyLeg } as JourneyLeg.RouteJourneyLeg
                    val attachedStop = lastLeg.stops.last()
                    val time = distance(arrivalLocation.location, attachedStop.location) * graph.item.metadata.assumedWalkingSecondsPerKilometer.toLong()
                    legs.add(JourneyLeg.TransferPoint(
                        time.seconds,
                        lastLeg.arrivalTime,
                        lastLeg.arrivalTime + time.seconds
                    ))
                }
            }

            Journey(legs)
        }
    }

    fun List<Stop>.nearbyStops(location: JourneyLocation, maximumWalkingTime: Duration): List<RaptorStop> {
        if (location.exact) {
            return listOf(RaptorStop(minBy { distance(location.location, it.location) }.id, 0L))
        }

        return map { StopWithDistance(it, distance(location.location, it.location)) }
                .filter { (it.distance * graph.item.metadata.assumedWalkingSecondsPerKilometer.toLong()) < maximumWalkingTime.inWholeSeconds}
                .map { RaptorStop(
                    it.stop.id,
                    (it.distance * graph.item.metadata.assumedWalkingSecondsPerKilometer.toInt()).toLong()
                ) }
    }

}