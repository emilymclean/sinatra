package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.RouteLocationIndex
import cl.emilym.sinatra.room.dao.RouteLocationIndexDao
import cl.emilym.sinatra.room.entities.RouteLocationIndexEntity
import org.koin.core.annotation.Factory

@Factory
class RouteLocationIndexPersistence(
    private val routeLocationIndexDao: RouteLocationIndexDao
) {

    suspend fun save(routeLocationIndices: List<RouteLocationIndex>) {
        routeLocationIndexDao.clearAndInsert(*routeLocationIndices.map {
            RouteLocationIndexEntity.fromModel(it)
        }.toTypedArray())
    }

    suspend fun clear() {
        routeLocationIndexDao.clear()
    }

    suspend fun get(): List<RouteLocationIndex> {
        return routeLocationIndexDao.get().map {
            it.toModel()
        }
    }

    suspend fun getInBox(
        southLat: Double,
        westLng: Double,
        northLat: Double,
        eastLng: Double
    ): List<RouteLocationIndex> {
        return routeLocationIndexDao.getInBox(
            southLat, westLng, northLat, eastLng
        ).map { it.toModel() }
    }

}