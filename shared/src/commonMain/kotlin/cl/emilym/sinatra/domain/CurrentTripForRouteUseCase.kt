package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteTripInformation
import cl.emilym.sinatra.data.models.flatMap
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory

data class CurrentTripInformation(
    val tripInformation: RouteTripInformation
)

@Factory
class CurrentTripForRouteUseCase(
    private val routeRepository: RouteRepository,
    private val serviceRepository: ServiceRepository,
    private val transportMetadataRepository: TransportMetadataRepository,
    private val clock: Clock
) {

    suspend operator fun invoke(routeId: RouteId): Cachable<CurrentTripInformation?> {
        val now = clock.now()
        val startOfDay = transportMetadataRepository.startOfToday()

        val services = routeRepository.servicesForRoute(routeId).flatMap {
            serviceRepository.services(it)
        }
        val activeServices = services.map {
            it.firstOrNull { it.active(now, transportMetadataRepository.timeZone()) }
        }
        if (activeServices.item == null) return activeServices.map { null }

        val timetable = activeServices.flatMap { routeRepository.serviceTimetable(routeId, it!!.id) }
        val activeTimetable = timetable.map { it.trips.firstOrNull { it.active(now, startOfDay) } }

        return if (activeTimetable.item != null) {
            activeTimetable.map { CurrentTripInformation(it!!) }
        } else {
            timetable.map { it.trips.minByOrNull { now - it.startTime(startOfDay) }?.let { CurrentTripInformation(it) } }
        }
    }

}