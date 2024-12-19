package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.models.PlaceId
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.persistence.RecentVisitPersistence
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class RecentVisitRepository(
    private val stopRepository: StopRepository,
    private val routeRepository: RouteRepository,
    private val recentVisitPersistence: RecentVisitPersistence,
) {

    fun all(): Flow<List<RecentVisit>> {
        return flow {
            stopRepository.stops()
            routeRepository.routes()
            emitAll(recentVisitPersistence.all())
        }
    }

    suspend fun addRouteVisit(routeId: RouteId) {
        recentVisitPersistence.addRouteVisit(routeId)
    }

    suspend fun addStopVisit(stopId: StopId) {
        recentVisitPersistence.addStopVisit(stopId)
    }

    suspend fun addPlaceVisit(placeId: PlaceId) {
        recentVisitPersistence.addPlaceVisit(placeId)
    }

}