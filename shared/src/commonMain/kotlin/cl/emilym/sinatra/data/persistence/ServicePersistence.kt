package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.TimetableServiceException
import cl.emilym.sinatra.data.models.TimetableServiceRegular
import cl.emilym.sinatra.room.dao.TimetableServiceExceptionEntityDao
import cl.emilym.sinatra.room.dao.TimetableServiceRegularEntityDao
import cl.emilym.sinatra.room.entities.TimetableServiceExceptionEntity
import cl.emilym.sinatra.room.entities.TimetableServiceRegularEntity
import org.koin.core.annotation.Factory

@Factory
class ServicePersistence(
    private val timetableServiceRegularEntityDao: TimetableServiceRegularEntityDao,
    private val timetableServiceExceptionEntityDao: TimetableServiceExceptionEntityDao
) {

    suspend fun save(services: List<Service>) {
        timetableServiceRegularEntityDao.clear()
        timetableServiceExceptionEntityDao.clear()
        for (service in services) {
            timetableServiceRegularEntityDao.insert(
                *service.regular.map { TimetableServiceRegularEntity.fromModel(it, service.id) }.toTypedArray()
            )
            timetableServiceExceptionEntityDao.insert(
                *service.exception.map { TimetableServiceExceptionEntity.fromModel(it, service.id) }.toTypedArray()
            )
        }
    }

    suspend fun get(): List<Service> {
        val regular = mutableMapOf<ServiceId, MutableList<TimetableServiceRegular>>()
        val exception = mutableMapOf<ServiceId, MutableList<TimetableServiceException>>()

        val rdbs = timetableServiceRegularEntityDao.get()
        for (rdb in rdbs) {
            regular.getOrPut(rdb.serviceId) { mutableListOf() }.add(rdb.toModel())
        }

        val edbs = timetableServiceExceptionEntityDao.get()
        for (edb in edbs) {
            exception.getOrPut(edb.serviceId) { mutableListOf() }.add(edb.toModel())
        }

        val keys = (regular.keys + exception.keys).distinct()

        return keys.map {
            Service(
                it,
                regular.getOrElse(it) { listOf() },
                exception.getOrElse(it) { listOf() }
            )
        }
    }

}