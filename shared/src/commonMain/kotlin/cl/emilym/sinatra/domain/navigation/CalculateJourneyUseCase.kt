package cl.emilym.sinatra.domain.navigation

import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.data.repository.NetworkGraphRepository
import cl.emilym.sinatra.data.repository.RoutingPreferencesRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.domain.ActiveServicesUseCase
import cl.emilym.sinatra.e
import cl.emilym.sinatra.router.RaptorConfig
import cl.emilym.sinatra.router.RaptorStop
import cl.emilym.sinatra.router.RouterPrefs
import cl.emilym.sinatra.router.Seconds
import cl.emilym.sinatra.router.data.NetworkGraph
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@Factory
class CalculateJourneyUseCase(
    private val networkGraphRepository: NetworkGraphRepository,
    private val activeServicesUseCase: ActiveServicesUseCase,
    private val stopRepository: StopRepository,
    private val clock: Clock,
    private val transportMetadataRepository: TransportMetadataRepository,
    private val routingPreferencesRepository: RoutingPreferencesRepository,
    private val routerFactory: RouterFactory,
    private val reconstructJourneyUseCase: ReconstructJourneyUseCase,
    private val directWalkingJourneyUseCase: DirectWalkingJourneyUseCase
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

                directWalkingJourneyUseCase(
                    departureLocation,
                    arrivalLocation,
                    anchorTime,
                    getGraph().item.metadata.assumedWalkingSecondsPerKilometer.toInt().seconds
                )?.let {
                    options += it
                }

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

                    currentCoroutineContext().ensureActive()
                    if (clock.now() > computationCutoffTime) break
                }

                if (options.isEmpty()) {
                    throw RouterException.noJourneyFound()
                }

                options
                    .sortedWith(compareBy(
                        {
                            when (anchorTime) {
                                is JourneyCalculationTime.DepartureTime -> it.arrivalTime.instant.epochSeconds
                                is JourneyCalculationTime.ArrivalTime -> it.departureTime.instant.epochSeconds * -1
                            }
                        },
                        { it.duration },
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

    suspend fun calculateJourney(
        config: RaptorConfig,
        departureStops: List<RaptorStop>,
        arrivalStops: List<RaptorStop>
    ): Journey? {
        val prefs = RouterPrefs(
            wheelchairAccessible = onlyWheelchair,
            bikesAllowed = onlyBikes
        )

        val raptor = routerFactory(
            anchorTime,
            getGraph().item,
            services,
            config,
            prefs
        )

        val anchorTimeSeconds: Seconds = (anchorTime.time - startOfDay).inWholeSeconds

        return try {
            reconstructJourneyUseCase(
                raptor.calculate(
                    anchorTimeSeconds,
                    departureStops,
                    arrivalStops
                ),
                departureLocation,
                arrivalLocation,
                anchorTime,
                getGraph().item
            )
        } catch (e: Exception) {
            Napier.e(e)
            null
        }
    }

    suspend fun List<Stop>.nearbyStops(location: JourneyLocation, maximumWalkingTime: Duration): List<RaptorStop> {
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

