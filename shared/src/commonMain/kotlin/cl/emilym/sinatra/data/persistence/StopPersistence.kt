package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopWithChildren
import cl.emilym.sinatra.room.dao.StopDao
import cl.emilym.sinatra.room.entities.StopEntity
import org.koin.core.annotation.Factory

@Factory
class StopPersistence(
    private val stopDao: StopDao
) {

    suspend fun save(stops: List<Stop>) {
        stopDao.clear()
        stopDao.insert(*stops.map { StopEntity.fromModel(it) }.toTypedArray())
    }

    suspend fun get(): List<Stop> {
        return stopDao.get().map { it.toModel() }
    }

    suspend fun get(stopId: StopId): Stop? {
        return stopDao.get(stopId)?.toModel()
    }

    suspend fun getWithChildren(stopId: StopId): StopWithChildren? {
        val result = stopDao.getWithChildren(stopId) ?: return null
        return StopWithChildren(
            result.stop.toModel(),
            result.children.map { it.toModel() }
        )
    }

    suspend fun children(parentId: StopId): List<Stop> {
        return stopDao.children(parentId).map { it.toModel() }
    }

    suspend fun clear() {
        stopDao.clear()
    }

}