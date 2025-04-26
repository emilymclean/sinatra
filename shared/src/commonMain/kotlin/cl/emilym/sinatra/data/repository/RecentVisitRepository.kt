package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.models.PlaceId
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.RecentVisitType
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

    fun all(
        type: List<RecentVisitType> = listOf()
    ): Flow<List<RecentVisit>> {
        val type = when {
            type.isEmpty() -> listOf(RecentVisitType.STOP, RecentVisitType.ROUTE, RecentVisitType.PLACE)
            else -> type
        }

        return flow {
            if (type.contains(RecentVisitType.STOP)) stopRepository.stops()
            if (type.contains(RecentVisitType.ROUTE)) routeRepository.routes()
            emitAll(recentVisitPersistence.all(type))
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

    suspend fun add(recentVisit: RecentVisit) {
        when (recentVisit) {
            is RecentVisit.Stop -> addStopVisit(recentVisit.stop.id)
            is RecentVisit.Route -> addRouteVisit(recentVisit.route.id)
            is RecentVisit.Place -> addPlaceVisit(recentVisit.place.id)
        }
    }

}