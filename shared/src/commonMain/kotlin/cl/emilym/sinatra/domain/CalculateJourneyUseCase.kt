package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.NetworkGraphRepository
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.router.Raptor
import cl.emilym.sinatra.router.RaptorJourneyConnection
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.seconds

@Factory
class CalculateJourneyUseCase(
    private val networkGraphRepository: NetworkGraphRepository,
    private val activeServicesUseCase: ActiveServicesUseCase,
    private val stopRepository: StopRepository,
    private val routeRepository: RouteRepository,
    private val clock: Clock
) {

    suspend operator fun invoke(
        departureLocation: MapLocation,
        arrivalLocation: MapLocation,
        departureTime: Instant = clock.now()
    ): Journey {
        val now = departureTime
        val graph = networkGraphRepository.networkGraph()
        val stops = stopRepository.stops()
        val services = activeServicesUseCase(now).map { it.map { it.id } }

        val departureStop = stops.map { it.minBy { distance(departureLocation, it.location) } }.item
        val arrivalStop = stops.map { it.minBy { distance(arrivalLocation, it.location) } }.item

        val departureTime = departureTime + (
                distance(departureLocation, departureStop.location) *
                        graph.item.config.assumedWalkingSecondsPerKilometer
            ).seconds

        val raptor = Raptor(graph.item, services.item)
        val raptorJourney = raptor.calculate(departureTime.epochSeconds, departureStop.id, arrivalStop.id)

        val routes = routeRepository.routes(
            raptorJourney.connections.filterIsInstance<RaptorJourneyConnection.Travel>().map { it.routeId }
        )
        val legs = mutableListOf<JourneyLeg>()
        for (i in raptorJourney.connections.indices) {
            val connectedStops = listOf(
                stops.item.first { it.id == raptorJourney.stops[i] },
                stops.item.first { it.id == raptorJourney.stops[i + 1] }
            )
            legs += when (val connection = raptorJourney.connections[i]) {
                is RaptorJourneyConnection.Travel -> JourneyLeg.Travel(
                    // This should eventually contain all stops visited on travel
                    connectedStops,
                    routes.item.first { it?.id == connection.routeId }!!,
                    connection.heading,
                    connection.startTime.seconds,
                    connection.endTime.seconds
                )
                is RaptorJourneyConnection.Transfer -> JourneyLeg.Transfer(
                    connectedStops,
                    connection.travelTime.seconds
                )
            }
        }

        return Journey(legs)
    }

}