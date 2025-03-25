package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.IRouteTripInformation
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.data.models.flatMap
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.models.merge
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory

data class CurrentTripInformation(
    val tripInformation: IRouteTripInformation?,
    val route: Route,
)

@Factory
class CurrentTripForRouteUseCase(
    private val routeRepository: RouteRepository,
    private val serviceRepository: ServiceRepository,
    private val transportMetadataRepository: TransportMetadataRepository,
    private val clock: Clock,
    private val liveTripInformationUseCase: LiveTripInformationUseCase,
    private val metadataRepository: TransportMetadataRepository
) {

    suspend operator fun invoke(
        routeId: RouteId,
        serviceId: ServiceId? = null,
        tripId: TripId? = null,
        referenceTime: Instant? = null
    ): Flow<Cachable<CurrentTripInformation?>> {
        val now = (referenceTime ?: clock.now()).startOfDay(metadataRepository.timeZone())

        val rc = routeRepository.route(routeId)
        val route = rc.item ?: return flowOf(rc.map { null })

        val activeServices = if (serviceId == null) {
            val services = routeRepository.servicesForRoute(routeId).flatMap {
                serviceRepository.services(it)
            }
            services.map {
                it.firstOrNull { it.active(now, transportMetadataRepository.timeZone()) } ?:
                    it.firstOrNull { it.active(now, transportMetadataRepository.timeZone(), ignoreDates = true) }
            }
        } else {
            serviceRepository.services(listOf(serviceId)).map { it.firstOrNull() }
        }
        if (activeServices.item == null) return flowOf(activeServices.map { null })

        suspend fun fallback(): Flow<Cachable<CurrentTripInformation?>> {
            val timetable = activeServices.flatMap {
                routeRepository.tripTimetable(
                    routeId,
                    it!!.id,
                    tripId!!,
                    now
                )
            }
            return flowOf(timetable.map { CurrentTripInformation(it.trip, route) })
        }

        return when {
            tripId == null -> {
                val timetable = activeServices.flatMap { routeRepository.canonicalServiceTimetable(routeId, it!!.id) }
                flowOf(timetable.map { CurrentTripInformation(it.trip, route) })
            }
            route.realTimeUrl == null -> {
                fallback()
            }
            else -> {
                try {
                    liveTripInformationUseCase.invoke(
                        route.realTimeUrl,
                        routeId,
                        activeServices.item!!.id,
                        tripId,
                        now
                    ).map { it.map { CurrentTripInformation(it, route) }
                        .merge(activeServices, { i1, i2 -> i1 }) }
                } catch (e: Exception) {
                    Napier.e(e)
                    fallback()
                }
            }
        }

    }

}