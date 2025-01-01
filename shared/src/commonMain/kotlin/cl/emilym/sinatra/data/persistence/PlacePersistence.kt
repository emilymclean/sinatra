package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.room.dao.PlaceDao
import cl.emilym.sinatra.room.entities.PlaceEntity
import org.koin.core.annotation.Factory

@Factory
class PlacePersistence(
    private val placeDao: PlaceDao
) {

    suspend fun save(places: List<Place>) {
        placeDao.save(places.map { PlaceEntity.fromModel(it) })
    }

}