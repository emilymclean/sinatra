package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.models.ServiceAlertId
import cl.emilym.sinatra.room.dao.RouteDao
import cl.emilym.sinatra.room.dao.ServiceAlertDao
import cl.emilym.sinatra.room.entities.RouteEntity
import cl.emilym.sinatra.room.entities.ServiceAlertEntity
import org.koin.core.annotation.Factory

@Factory
class ServiceAlertPersistence(
    private val serviceAlertDao: ServiceAlertDao
) {

    suspend fun save(alerts: List<ServiceAlert>) {
        serviceAlertDao.clear()
        serviceAlertDao.insert(*alerts.map { ServiceAlertEntity.fromModel(it) }.toTypedArray())
    }

    suspend fun get(): List<ServiceAlert> {
        return serviceAlertDao.get().map { it.toModel() }
    }

    suspend fun get(id: ServiceAlertId): ServiceAlert? {
        return serviceAlertDao.get(id)?.toModel()
    }

    suspend fun clear() {
        serviceAlertDao.clear()
    }

}