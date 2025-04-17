package cl.emilym.sinatra.domain

import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.MapLocation
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
import cl.emilym.sinatra.router.ArrivalBasedRouter
import cl.emilym.sinatra.router.DepartureBasedRouter
import cl.emilym.sinatra.router.RaptorConfig
import cl.emilym.sinatra.router.RaptorJourney
import cl.emilym.sinatra.router.RaptorJourneyConnection
import cl.emilym.sinatra.router.RaptorStop
import cl.emilym.sinatra.router.RouterPrefs
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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

data class JourneyLocation(
    val location: MapLocation,
    val exact: Boolean
)

sealed interface JourneyCalculationTime {
    val time: Instant

    data class DepartureTime(
        override val time: Instant
    ): JourneyCalculationTime
    data class ArrivalTime(
        override val time: Instant
    ): JourneyCalculationTime
}

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
    private var departureGraph: Cachable<NetworkGraph>? = null
    private var arrivalGraph: Cachable<NetworkGraph>? = null
    private lateinit var stops: Cachable<List<Stop>>
    private lateinit var services: List<Cachable<List<ServiceId>>>
    private lateinit var startOfDay: Instant

    private lateinit var anchorTime: JourneyCalculationTime
    private lateinit var departureLocation: JourneyLocation
    private lateinit var arrivalLocation: JourneyLocation
    private var onlyWheelchair: Boolean = false
    private var onlyBikes: Boolean = false

    suspend operator fun invoke(
        departureLocation: JourneyLocation,
        arrivalLocation: JourneyLocation,
        anchorTime: JourneyCalculationTime,
        onlyWheelchair: Boolean = false,
        onlyBikes: Boolean = false,
    ): List<Journey> {
        return withContext(Dispatchers.IO) {
            lock.withLock {
                this@CalculateJourneyUseCase.anchorTime = anchorTime
                this@CalculateJourneyUseCase.departureLocation = departureLocation
                this@CalculateJourneyUseCase.arrivalLocation = arrivalLocation
                this@CalculateJourneyUseCase.onlyBikes = onlyBikes
                this@CalculateJourneyUseCase.onlyWheelchair = onlyWheelchair

                val now = anchorTime.time
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

    private suspend fun getGraph(): Cachable<NetworkGraph> {
        return when (anchorTime) {
            is JourneyCalculationTime.ArrivalTime -> arrivalGraph ?: run {
                networkGraphRepository.networkGraph(true).also { arrivalGraph = it }
            }
            is JourneyCalculationTime.DepartureTime -> departureGraph ?: run {
                networkGraphRepository.networkGraph(false).also { departureGraph = it }
            }
        }
    }

    private suspend fun calculateJourney(
        config: RaptorConfig,
        departureStops: List<RaptorStop>,
        arrivalStops: List<RaptorStop>
    ): Journey? {
        val prefs = RouterPrefs(
            wheelchairAccessible = onlyWheelchair,
            bikesAllowed = onlyBikes
        )

        val raptor = when (anchorTime) {
            is JourneyCalculationTime.ArrivalTime -> ArrivalBasedRouter(
                getGraph().item,
                services.map { it.item },
                config,
                prefs
            )
            is JourneyCalculationTime.DepartureTime -> DepartureBasedRouter(
                getGraph().item,
                services.map { it.item },
                config,
                prefs
            )
        }

        val anchorTimeSeconds: Seconds = (anchorTime.time - startOfDay).inWholeSeconds

        return try {
            raptor.calculate(
                anchorTimeSeconds,
                departureStops,
                arrivalStops
            ).toJourney()
        } catch (e: Exception) {
            Napier.e(e)
            null
        }
    }

    private suspend fun RaptorJourney.toJourney(): Journey {
        val raptorJourney = this
        val connections = raptorJourney.connections

        val routes = routeRepository.routes(
            connections.filterIsInstance<RaptorJourneyConnection.Travel>().map { it.routeId }
        )
        var legs = mutableListOf<JourneyLeg>()
        var lastEndTime: Duration? = when (val connection = connections.first()) {
            is RaptorJourneyConnection.Travel -> connection.endTime.seconds
            else -> null
        }

        for (i in connections.indices) {
            val stops = connections[i].stops.mapNotNull { s -> stops.item.firstOrNull { it.id == s } }
            legs += when (val connection = connections[i]) {
                is RaptorJourneyConnection.Travel -> {
                    val startOfDayIndexed = startOfDay + connection.dayIndex.days
                    lastEndTime = connection.endTime.seconds
                    JourneyLeg.Travel(
                        stops,
                        (connection.endTime - connection.startTime).seconds,
                        routes.item.first { it?.id == connection.routeId }!!,
                        connection.heading,
                        Time.create(connection.startTime.seconds, startOfDayIndexed),
                        Time.create(connection.endTime.seconds, startOfDayIndexed)
                    )
                }
                is RaptorJourneyConnection.Transfer -> when {
                    lastEndTime != null -> JourneyLeg.Transfer(
                        stops,
                        connection.travelTime.seconds,
                        Time.create(lastEndTime, startOfDay),
                        Time.create(lastEndTime + connection.travelTime.seconds, startOfDay)
                    ).also {
                        lastEndTime += connection.travelTime.seconds
                    }
                    else -> {
                        val inbetweenTime = connections
                            .drop(i)
                            .takeWhile { it is RaptorJourneyConnection.Travel }
                            .sumOf { it.travelTime }
                        val concreteTime = ((connections
                            .drop(i)
                            .first { it is RaptorJourneyConnection.Travel } as RaptorJourneyConnection.Travel)
                            .startTime - inbetweenTime).seconds

                        JourneyLeg.Transfer(
                            stops,
                            connection.travelTime.seconds,
                            Time.create(concreteTime - connection.travelTime.seconds, startOfDay),
                            Time.create(concreteTime, startOfDay)
                        )
                    }
                }
            }
        }

        if (!departureLocation.exact) {
            run {
                legs = legs.dropWhile { it is JourneyLeg.Transfer }.toMutableList()
                if (legs.isEmpty()) return@run
                val attachedStop = (legs.first { it is JourneyLeg.RouteJourneyLeg } as JourneyLeg.RouteJourneyLeg).stops.first()
                val time = distance(departureLocation.location, attachedStop.location) * getGraph().item.metadata.assumedWalkingSecondsPerKilometer.toLong()
                legs.add(0, JourneyLeg.TransferPoint(
                    time.seconds,
                    departureTime = legs.first().arrivalTime - time.seconds,
                    arrivalTime = legs.first().arrivalTime
                ))
            }
        }

        if (!arrivalLocation.exact) {
            run {
                legs = legs.dropLastWhile { it is JourneyLeg.Transfer }.toMutableList()
                if (legs.isEmpty()) return@run
                val lastLeg = legs.last { it is JourneyLeg.RouteJourneyLeg } as JourneyLeg.RouteJourneyLeg
                val attachedStop = lastLeg.stops.last()
                val time = distance(arrivalLocation.location, attachedStop.location) * getGraph().item.metadata.assumedWalkingSecondsPerKilometer.toLong()
                legs.add(JourneyLeg.TransferPoint(
                    time.seconds,
                    departureTime = lastLeg.arrivalTime,
                    arrivalTime = lastLeg.arrivalTime + time.seconds
                ))
            }
        }

        return Journey(legs)
    }

    private suspend fun List<Stop>.nearbyStops(location: JourneyLocation, maximumWalkingTime: Duration): List<RaptorStop> {
        if (location.exact) {
            return listOf(RaptorStop(minBy { distance(location.location, it.location) }.id, 0L))
        }

        return map { StopWithDistance(it, distance(location.location, it.location)) }
                .filter { (it.distance * getGraph().item.metadata.assumedWalkingSecondsPerKilometer.toLong()) < maximumWalkingTime.inWholeSeconds}
                .map { RaptorStop(
                    it.stop.id,
                    (it.distance * getGraph().item.metadata.assumedWalkingSecondsPerKilometer.toInt()).toLong()
                ) }
    }

}