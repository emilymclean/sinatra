package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.room.dao.RecentVisitDao
import cl.emilym.sinatra.room.entities.RecentVisitEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

enum class RecentVisitType {
    ROUTE, STOP;

    companion object {
        fun fromRecentVisit(recentVisit: RecentVisit): RecentVisitType {
            return when (recentVisit) {
                is RecentVisit.Route -> ROUTE
                is RecentVisit.Stop -> STOP
            }
        }
    }
}

@Factory
class RecentVisitPersistence(
    private val recentVisitDao: RecentVisitDao
) {

    companion object {
        private const val RECENT_VISIT_LIMIT = 20
    }

    fun all(): Flow<List<RecentVisit>> {
        return recentVisitDao.getFlow().map {
            it.mapNotNull {
                val type = RecentVisitType.valueOf(it.recentVisit.type)
                when (type) {
                    RecentVisitType.ROUTE -> it.route?.let { RecentVisit.Route(it.toModel()) }
                    RecentVisitType.STOP -> it.stop?.let { RecentVisit.Stop(it.toModel()) }
                }
            }
        }
    }

    suspend fun addRouteVisit(routeId: RouteId) {
        recentVisitDao.deleteRouteVisit(routeId)
        val dbId = recentVisitDao.insert(
            RecentVisitEntity(
                0,
                RecentVisitType.ROUTE.name,
                routeId,
                null
            )
        )
        limit(dbId)
    }

    suspend fun addStopVisit(stopId: StopId) {
        recentVisitDao.deleteStopVisit(stopId)
        val dbId = recentVisitDao.insert(
            RecentVisitEntity(
                0,
                RecentVisitType.STOP.name,
                null,
                stopId
            )
        )
        limit(dbId)
    }

    suspend fun limit(recentId: Long) {
        val all = recentVisitDao.all()
        if (all.size <= RECENT_VISIT_LIMIT) return

        recentVisitDao.deleteBelowId(all[RECENT_VISIT_LIMIT - 1].id)
    }

}