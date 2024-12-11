package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.room.dao.RouteDao
import cl.emilym.sinatra.room.dao.RouteServiceEntityDao
import cl.emilym.sinatra.room.dao.StopDao
import cl.emilym.sinatra.room.entities.RouteEntity
import cl.emilym.sinatra.room.entities.RouteServiceEntity
import cl.emilym.sinatra.room.entities.StopEntity
import org.koin.core.annotation.Factory

@Factory
class RouteServicePersistence(
    private val routeServiceEntityDao: RouteServiceEntityDao
) {

    suspend fun save(services: List<ServiceId>, resource: ResourceKey) {
        routeServiceEntityDao.clear(resource)
        routeServiceEntityDao.insert(*services.map {
            RouteServiceEntity(
                0,
                resource,
                it
            )
        }.toTypedArray())
    }

    suspend fun get(resource: ResourceKey): List<ServiceId> {
        return routeServiceEntityDao.get(resource).map { it.serviceId }
    }

    suspend fun clear(resource: ResourceKey) {
        routeServiceEntityDao.clear(resource)
    }

}