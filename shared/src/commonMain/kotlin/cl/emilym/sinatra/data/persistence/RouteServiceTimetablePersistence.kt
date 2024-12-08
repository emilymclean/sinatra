package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.RouteServiceTimetable
import cl.emilym.sinatra.data.models.RouteTripInformation
import cl.emilym.sinatra.room.dao.RouteTripInformationEntityDao
import cl.emilym.sinatra.room.dao.RouteTripStopEntityDao
import cl.emilym.sinatra.room.entities.RouteTripInformationEntity
import cl.emilym.sinatra.room.entities.RouteTripStopEntity
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory

@Factory
class RouteServiceTimetablePersistence(
    private val routeTripInformationEntityDao: RouteTripInformationEntityDao,
    private val routeTripStopEntityDao: RouteTripStopEntityDao,
) {

    suspend fun save(timetable: RouteServiceTimetable, resource: ResourceKey) {
        routeTripInformationEntityDao.clear(resource)
        for (info in timetable.trips) {
            val id = routeTripInformationEntityDao.insert(
                RouteTripInformationEntity.fromModel(info, resource)
            )
            routeTripStopEntityDao.insert(info.stops.map {
                RouteTripStopEntity.fromModel(it, id, resource)
            })
        }
    }

    suspend fun get(resource: ResourceKey): RouteServiceTimetable {
        val infos = routeTripInformationEntityDao.get(resource)
        var out = mutableListOf<RouteTripInformation>()

        for (info in infos) {
            out.add(
                info.toModel(
                    routeTripStopEntityDao.get(info.id).map { it.toModel() }.also {
                        Napier.d("Stops related to route = ${it.map { it.stopId }}")
                    }
                )
            )
        }

        return RouteServiceTimetable(out)
    }

}