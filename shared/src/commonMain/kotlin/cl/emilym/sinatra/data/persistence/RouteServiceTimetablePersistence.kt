package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.RouteServiceCanonicalTimetable
import cl.emilym.sinatra.data.models.RouteServiceTimetable
import cl.emilym.sinatra.data.models.RouteTripInformation
import cl.emilym.sinatra.data.models.RouteTripTimetable
import cl.emilym.sinatra.room.dao.RouteTripInformationEntityDao
import cl.emilym.sinatra.room.dao.RouteTripStopEntityDao
import cl.emilym.sinatra.room.entities.RouteTripInformationEntity
import cl.emilym.sinatra.room.entities.RouteTripStopEntity
import org.koin.core.annotation.Factory

@Factory
class RouteTripInformationPersistence(
    private val routeTripInformationEntityDao: RouteTripInformationEntityDao,
    private val routeTripStopEntityDao: RouteTripStopEntityDao
) {
    suspend fun save(timetable: List<RouteTripInformation>, resource: ResourceKey) {
        routeTripInformationEntityDao.clear(resource)
        for (info in timetable) {
            val id = routeTripInformationEntityDao.insert(
                RouteTripInformationEntity.fromModel(info, resource)
            )
            routeTripStopEntityDao.insert(info.stops.map {
                RouteTripStopEntity.fromModel(it, id, resource)
            })
        }
    }

    suspend fun get(resource: ResourceKey): List<RouteTripInformation> {
        val infos = routeTripInformationEntityDao.get(resource)
        val out = mutableListOf<RouteTripInformation>()

        for (info in infos) {
            out.add(
                info.toModel(
                    routeTripStopEntityDao.get(info.id, resource).map { it.toModel() }
                )
            )
        }

        return out
    }

    suspend fun clear(resource: ResourceKey) {
        routeTripInformationEntityDao.clear(resource)
    }
}

@Factory
class RouteServiceTimetablePersistence(
    private val routeTripInformationPersistence: RouteTripInformationPersistence
) {

    suspend fun save(timetable: RouteServiceTimetable, resource: ResourceKey) {
        routeTripInformationPersistence.save(timetable.trips, resource)
    }

    suspend fun get(resource: ResourceKey): RouteServiceTimetable {
        return RouteServiceTimetable(routeTripInformationPersistence.get(resource))
    }

    suspend fun clear(resource: ResourceKey) {
        routeTripInformationPersistence.clear(resource)
    }

}

@Factory
class RouteServiceCanonicalTimetablePersistence(
    private val routeTripInformationPersistence: RouteTripInformationPersistence
) {

    suspend fun save(timetable: RouteServiceCanonicalTimetable, resource: ResourceKey) {
        routeTripInformationPersistence.save(listOf(timetable.trip), resource)
    }

    suspend fun get(resource: ResourceKey): RouteServiceCanonicalTimetable? {
        return routeTripInformationPersistence.get(resource).firstOrNull()?.let { RouteServiceCanonicalTimetable(it) }
    }

    suspend fun clear(resource: ResourceKey) {
        routeTripInformationPersistence.clear(resource)
    }

}

@Factory
class RouteTripTimetablePersistence(
    private val routeTripInformationPersistence: RouteTripInformationPersistence
) {

    suspend fun save(timetable: RouteTripTimetable, resource: ResourceKey) {
        routeTripInformationPersistence.save(listOf(timetable.trip), resource)
    }

    suspend fun get(resource: ResourceKey): RouteTripTimetable? {
        return routeTripInformationPersistence.get(resource).firstOrNull()?.let { RouteTripTimetable(it) }
    }

    suspend fun clear(resource: ResourceKey) {
        routeTripInformationPersistence.clear(resource)
    }

}