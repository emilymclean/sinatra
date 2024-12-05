package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopTimetable
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.room.dao.StopDao
import cl.emilym.sinatra.room.dao.StopTimetableTimeEntityDao
import cl.emilym.sinatra.room.entities.StopEntity
import cl.emilym.sinatra.room.entities.StopTimetableTimeEntity
import org.koin.core.annotation.Factory

@Factory
class StopTimetablePersistence(
    private val stopTimetableTimeEntityDao: StopTimetableTimeEntityDao
) {

    suspend fun save(stops: StopTimetable, resource: ResourceKey) {
        stopTimetableTimeEntityDao.clear(resource)
        stopTimetableTimeEntityDao.insert(*stops.times.map {
            StopTimetableTimeEntity.fromModel(it, resource)
        }.toTypedArray())
    }

    suspend fun get(resource: ResourceKey): StopTimetable {
        return StopTimetable(
            stopTimetableTimeEntityDao.get(resource).map { it.toModel() }
        )
    }

}