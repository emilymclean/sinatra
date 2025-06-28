package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.PlaceId
import cl.emilym.sinatra.room.dao.PlaceDao
import cl.emilym.sinatra.room.entities.PlaceEntity
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Factory

@Factory
class PlacePersistence(
    private val placeDao: PlaceDao
) {

    suspend fun save(places: List<Place>) {
        placeDao.save(places.map { PlaceEntity.fromModel(it) })
    }

    suspend fun get(id: PlaceId): Place? {
        return placeDao.get(id)?.toModel()
    }

    suspend fun find(lat: Double, lng: Double, latRange: Double, lngRange: Double): Place? {
        return placeDao.find(lat, lng, latRange, lngRange)?.toModel()
    }

}