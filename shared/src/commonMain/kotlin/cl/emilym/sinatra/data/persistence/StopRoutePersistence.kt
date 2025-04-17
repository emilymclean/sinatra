package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Heading
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.room.dao.RouteHeadingEntityDao
import cl.emilym.sinatra.room.dao.StopRouteEntityDao
import cl.emilym.sinatra.room.entities.RouteHeadingEntity
import cl.emilym.sinatra.room.entities.StopRouteEntity
import org.koin.core.annotation.Factory

@Factory
class StopRoutePersistence(
    private val stopRouteEntityDao: StopRouteEntityDao
) {

    suspend fun save(routes: List<RouteId>, resource: ResourceKey) {
        stopRouteEntityDao.clear(resource)
        stopRouteEntityDao.insert(*routes.map {
            StopRouteEntity(
                0,
                resource,
                it
            )
        }.toTypedArray())
    }

    suspend fun get(resource: ResourceKey): List<Route> {
        return stopRouteEntityDao.get(resource).map { it.route.toModel() }
    }

    suspend fun clear(resource: ResourceKey) {
        stopRouteEntityDao.clear(resource)
    }

}