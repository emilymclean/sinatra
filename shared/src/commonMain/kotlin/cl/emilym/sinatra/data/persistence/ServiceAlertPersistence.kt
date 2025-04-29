package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.models.ServiceAlertId
import cl.emilym.sinatra.room.dao.RouteDao
import cl.emilym.sinatra.room.dao.ServiceAlertDao
import cl.emilym.sinatra.room.entities.RouteEntity
import cl.emilym.sinatra.room.entities.ServiceAlertEntity
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory

@Factory
class ServiceAlertPersistence(
    private val serviceAlertDao: ServiceAlertDao
) {

    suspend fun save(alerts: List<ServiceAlert>) {
        val viewed = serviceAlertDao.getViewed()
        serviceAlertDao.clear()
        serviceAlertDao.insert(*alerts.map { alert ->
            ServiceAlertEntity.fromModel(alert)
                .copy(viewed = viewed.contains(alert.id))
        }.toTypedArray())
    }

    suspend fun get(): List<ServiceAlert> {
        return serviceAlertDao.get().map { it.toModel() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLive(): Flow<List<ServiceAlert>> {
        return serviceAlertDao.getLive().mapLatest { it.map { it.toModel() } }
    }

    suspend fun get(id: ServiceAlertId): ServiceAlert? {
        return serviceAlertDao.get(id)?.toModel()
    }

    suspend fun clear() {
        serviceAlertDao.clear()
    }

    suspend fun markViewed(id: ServiceAlertId) {
        serviceAlertDao.markViewed(id)
        Napier.d("${get(id)}")
    }

}