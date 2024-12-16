package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteTripInformation
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.data.models.flatMap
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory

data class CurrentTripInformation(
    val tripInformation: RouteTripInformation?,
    val route: Route,
)

@Factory
class CurrentTripForRouteUseCase(
    private val routeRepository: RouteRepository,
    private val serviceRepository: ServiceRepository,
    private val transportMetadataRepository: TransportMetadataRepository,
    private val clock: Clock
) {

    suspend operator fun invoke(
        routeId: RouteId,
        serviceId: ServiceId? = null,
        tripId: TripId? = null
    ): Cachable<CurrentTripInformation?> {
        val now = clock.now()

        val rc = routeRepository.route(routeId)
        val route = rc.item ?: return rc.map { null }

        val activeServices = if (serviceId == null) {
            val services = routeRepository.servicesForRoute(routeId).flatMap {
                serviceRepository.services(it)
            }
            services.map {
                it.firstOrNull { it.active(now, transportMetadataRepository.timeZone()) }
            }
        } else {
            serviceRepository.services(listOf(serviceId)).map { it.firstOrNull() }
        }
        if (activeServices.item == null) return activeServices.map { null }

        if (tripId == null) {
            val timetable = activeServices.flatMap { routeRepository.canonicalServiceTimetable(routeId, it!!.id) }
            return timetable.map { CurrentTripInformation(it.trip, route) }
        } else {
            val timetable = activeServices.flatMap {
                routeRepository.tripTimetable(
                    routeId,
                    it!!.id,
                    tripId
                )
            }
            return timetable.map { CurrentTripInformation(it.trip, route) }
        }
    }

}