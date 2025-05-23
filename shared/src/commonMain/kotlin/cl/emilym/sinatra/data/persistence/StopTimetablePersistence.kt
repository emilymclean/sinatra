package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.StopTimetable
import cl.emilym.sinatra.room.dao.StopTimetableTimeEntityDao
import cl.emilym.sinatra.room.entities.StopTimetableTimeEntity
import kotlinx.datetime.Instant
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

    suspend fun get(resource: ResourceKey, startOfDay: Instant?): StopTimetable {
        return StopTimetable(
            stopTimetableTimeEntityDao.get(resource).map { it.toModel(startOfDay) }
        )
    }

    suspend fun clear(resource: ResourceKey) {
        stopTimetableTimeEntityDao.clear(resource)
    }

}