package cl.emilym.sinatra.domain

import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.data.repository.NetworkGraphRepository
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.RoutingPreferencesRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.e
import cl.emilym.sinatra.router.Raptor
import cl.emilym.sinatra.router.RaptorConfig
import cl.emilym.sinatra.router.RaptorJourney
import cl.emilym.sinatra.router.RaptorJourneyConnection
import cl.emilym.sinatra.router.RaptorStop
import cl.emilym.sinatra.router.Seconds
import cl.emilym.sinatra.router.data.NetworkGraph
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
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

    private val lock = Mutex()
    private lateinit var graph: Cachable<NetworkGraph>
    private lateinit var stops: Cachable<List<Stop>>
    private lateinit var services: List<Cachable<List<ServiceId>>>
    private lateinit var startOfDay: Instant

    private lateinit var departureTime: Instant
    private lateinit var departureLocation: JourneyLocation
    private lateinit var arrivalLocation: JourneyLocation

    suspend operator fun invoke(
        departureLocation: JourneyLocation,
        arrivalLocation: JourneyLocation,
        departureTime: Instant = clock.now()
    ): List<Journey> {
        return withContext(Dispatchers.IO) {
            lock.withLock {
                this@CalculateJourneyUseCase.departureTime = departureTime
                this@CalculateJourneyUseCase.departureLocation = departureLocation
                this@CalculateJourneyUseCase.arrivalLocation = arrivalLocation

                val now = departureTime
                graph = networkGraphRepository.networkGraph()
                stops = stopRepository.stops()
                services = (-1..1).map {
                    activeServicesUseCase(now + (1 * it).days).map { it.map { it.id } }
                }
                val preferenceMaximumWalkingTime = routingPreferencesRepository.maximumWalkingTime()
                startOfDay = now.startOfDay(transportMetadataRepository.timeZone())

                val config = networkGraphRepository.config()

                val computationCutoffTime = clock.now() + config.item.maximumComputationTime
                val options = mutableListOf<Journey>()

                for (o in config.item.options) {
                    val maximumWalkingTime = minOf(
                        o.maximumWalkingTime,
                        preferenceMaximumWalkingTime
                    )
                    val departureStops = stops.item.nearbyStops(departureLocation, maximumWalkingTime)
                    val arrivalStops = stops.item.nearbyStops(arrivalLocation, maximumWalkingTime)

                    calculateJourney(
                        o.raptor.copy(
                            maximumWalkingTime = maximumWalkingTime.inWholeSeconds
                        ),
                        departureStops,
                        arrivalStops
                    )?.let { options += it }

                    if (clock.now() > computationCutoffTime) break
                }

                if (options.isEmpty()) {
                    throw RouterException.noJourneyFound()
                }

                options
                    .sortedWith(compareBy(
                        { it.arrivalTime },
                        { it.legs.filterIsInstance<JourneyLeg.Travel>().size }
                    ))
                    .distinctBy { it.deduplicationKey }
            }
        }
    }

    private suspend fun calculateJourney(
        config: RaptorConfig?,
        departureStops: List<RaptorStop>,
        arrivalStops: List<RaptorStop>
    ): Journey? {
        val raptor = Raptor(
            graph.item,
            services.map { it.item },
            config
        )

        val departureTimeSeconds: Seconds = (departureTime - startOfDay).inWholeSeconds

        return try {
            raptor.calculate(
                departureTimeSeconds,
                departureStops,
                arrivalStops
            ).toJourney(departureTimeSeconds)
        } catch (e: RouterException) {
            Napier.e(e)
            null
        }
    }

    private suspend fun RaptorJourney.toJourney(
        departureTimeSeconds: Seconds
    ): Journey {
        val raptorJourney = this

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
                    Time.create(connection.startTime.seconds, startOfDay),
                    Time.create(connection.endTime.seconds, startOfDay)
                )
                is RaptorJourneyConnection.Transfer -> JourneyLeg.Transfer(
                    stops,
                    connection.travelTime.seconds,
                    Time.create(lastEndTime, startOfDay),
                    Time.create(lastEndTime + connection.travelTime.seconds, startOfDay)
                )
            }.also { lastEndTime = it.arrivalTime.durationThroughDay }
        }

        if (!departureLocation.exact) {
            run {
                legs = legs.dropWhile { it is JourneyLeg.Transfer }.toMutableList()
                if (legs.isEmpty()) return@run
                val attachedStop = (legs.first { it is JourneyLeg.RouteJourneyLeg } as JourneyLeg.RouteJourneyLeg).stops.first()
                val time = distance(departureLocation.location, attachedStop.location) * graph.item.metadata.assumedWalkingSecondsPerKilometer.toLong()
                legs.add(0, JourneyLeg.TransferPoint(
                    time.seconds,
                    Time.create(departureTimeSeconds.seconds, startOfDay),
                    Time.create((departureTimeSeconds + time).seconds, startOfDay)
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

        return Journey(legs)
    }

    private fun List<Stop>.nearbyStops(location: JourneyLocation, maximumWalkingTime: Duration): List<RaptorStop> {
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