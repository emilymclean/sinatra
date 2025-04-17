package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Heading
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.room.dao.RouteHeadingEntityDao
import cl.emilym.sinatra.room.entities.RouteHeadingEntity
import org.koin.core.annotation.Factory

@Factory
class RouteHeadingPersistence(
    private val routeHeadingEntityDao: RouteHeadingEntityDao
) {

    suspend fun save(services: List<Heading>, resource: ResourceKey) {
        routeHeadingEntityDao.clear(resource)
        routeHeadingEntityDao.insert(*services.map {
            RouteHeadingEntity(
                0,
                resource,
                it
            )
        }.toTypedArray())
    }

    suspend fun get(resource: ResourceKey): List<Heading> {
        return routeHeadingEntityDao.get(resource).map { it.heading }
    }

    suspend fun clear(resource: ResourceKey) {
        routeHeadingEntityDao.clear(resource)
    }

}