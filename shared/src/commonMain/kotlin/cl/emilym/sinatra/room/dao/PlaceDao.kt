package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.emilym.sinatra.room.entities.PlaceEntity
import cl.emilym.sinatra.room.entities.StopEntity

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(places: List<PlaceEntity>)

    @Query("SELECT * FROM placeEntity WHERE id = :id")
    suspend fun get(id: String): PlaceEntity?

}