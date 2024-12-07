package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.room.dao.RouteDao
import cl.emilym.sinatra.room.dao.StopDao
import cl.emilym.sinatra.room.entities.RouteEntity
import cl.emilym.sinatra.room.entities.StopEntity
import org.koin.core.annotation.Factory

@Factory
class RoutePersistence(
    private val routeDao: RouteDao
) {

    suspend fun save(routes: List<Route>) {
        routeDao.clear()
        routeDao.insert(*routes.map { RouteEntity.fromModel(it) }.toTypedArray())
    }

    suspend fun get(): List<Route> {
        return routeDao.get().map { it.toModel() }
    }

    suspend fun get(id: RouteId): Route? {
        return routeDao.get(id)?.toModel()
    }

}