package cl.emilym.sinatra.domain.navigation

import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.RouteServiceAccessibility
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.router.RaptorJourney
import cl.emilym.sinatra.router.RaptorJourneyConnection
import cl.emilym.sinatra.router.data.NetworkGraph
import org.koin.core.annotation.Factory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@Factory
class ReconstructJourneyUseCase(
    private val routeRepository: RouteRepository,
    private val transportMetadataRepository: TransportMetadataRepository,
    private val stopRepository: StopRepository
) {

    suspend operator fun invoke(
        raptorJourney: RaptorJourney,
        departureLocation: JourneyLocation,
        arrivalLocation: JourneyLocation,
        anchorTime: JourneyCalculationTime,
        graph: NetworkGraph
    ): Journey {
        val connections = raptorJourney.connections
        val startOfDay = anchorTime.time.startOfDay(transportMetadataRepository.timeZone())
        val stops = stopRepository.stops()

        val routes = routeRepository.routes(
            connections.filterIsInstance<RaptorJourneyConnection.Travel>().map { it.routeId }
        )
        var legs = mutableListOf<JourneyLeg>()
        var lastEndTime: Duration? = when (val connection = connections.first()) {
            is RaptorJourneyConnection.Travel -> connection.endTime.seconds
            else -> null
        }
        var lastStartOfDay = startOfDay

        for (i in connections.indices) {
            val stops = connections[i].stops.mapNotNull { s -> stops.item.firstOrNull { it.id == s } }
            legs += when (val connection = connections[i]) {
                is RaptorJourneyConnection.Travel -> {
                    lastStartOfDay = startOfDay + connection.dayIndex.days
                    lastEndTime = connection.endTime.seconds
                    JourneyLeg.Travel(
                        stops,
                        (connection.endTime - connection.startTime).seconds,
                        routes.item.first { it?.id == connection.routeId }!!,
                        connection.heading,
                        Time.create(connection.startTime.seconds, lastStartOfDay),
                        Time.create(connection.endTime.seconds, lastStartOfDay),
                        RouteServiceAccessibility(
                            if (connection.bikesAllowed) ServiceBikesAllowed.ALLOWED else ServiceBikesAllowed.DISALLOWED,
                            if (connection.wheelchairAccessible) ServiceWheelchairAccessible.ACCESSIBLE else ServiceWheelchairAccessible.INACCESSIBLE,
                        )
                    )
                }
                is RaptorJourneyConnection.Transfer -> when {
                    lastEndTime != null -> JourneyLeg.Transfer(
                        stops,
                        connection.travelTime.seconds,
                        Time.create(lastEndTime, lastStartOfDay),
                        Time.create(lastEndTime + connection.travelTime.seconds, lastStartOfDay)
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
                        lastStartOfDay = startOfDay + (connections
                            .drop(i)
                            .first { it is RaptorJourneyConnection.Travel } as RaptorJourneyConnection.Travel)
                            .dayIndex.days

                        JourneyLeg.Transfer(
                            stops,
                            connection.travelTime.seconds,
                            Time.create(
                                concreteTime - connection.travelTime.seconds,
                                lastStartOfDay
                            ),
                            Time.create(concreteTime, lastStartOfDay)
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
                val time = distance(departureLocation.location, attachedStop.location) * graph.metadata.assumedWalkingSecondsPerKilometer.toLong()
                legs.add(0, JourneyLeg.TransferPoint(
                    time.seconds,
                    departureTime = legs.first().departureTime - time.seconds,
                    arrivalTime = legs.first().departureTime
                )
                )
            }
        }

        if (!arrivalLocation.exact) {
            run {
                legs = legs.dropLastWhile { it is JourneyLeg.Transfer }.toMutableList()
                if (legs.isEmpty()) return@run
                val lastLeg = legs.last { it is JourneyLeg.RouteJourneyLeg } as JourneyLeg.RouteJourneyLeg
                val attachedStop = lastLeg.stops.last()
                val time = distance(arrivalLocation.location, attachedStop.location) * graph.metadata.assumedWalkingSecondsPerKilometer.toLong()
                legs.add(
                    JourneyLeg.TransferPoint(
                        time.seconds,
                        departureTime = lastLeg.arrivalTime,
                        arrivalTime = lastLeg.arrivalTime + time.seconds
                    )
                )
            }
        }

        return Journey(legs)
    }

}