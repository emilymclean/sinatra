package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import cl.emilym.sinatra.room.entities.FavouriteEntity
import cl.emilym.sinatra.room.entities.FavouriteEntityEntityWithStopAndRoute
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {

    @Insert
    suspend fun insert(favourite: FavouriteEntity)

    @Delete
    suspend fun delete(favourite: FavouriteEntity)

    @Query("DELETE FROM favouriteEntity WHERE type = \"ROUTE\" AND routeId = :routeId")
    suspend fun deleteRoute(routeId: String)

    @Query("DELETE FROM favouriteEntity WHERE type = \"STOP\" AND stopId = :stopId")
    suspend fun deleteStop(stopId: String)

    @Query("DELETE FROM favouriteEntity WHERE type = \"STOP_ON_ROUTE\" AND stopId = :stopId AND routeId = :routeId")
    suspend fun deleteStopOnRoute(stopId: String, routeId: String)

    @Query("DELETE FROM favouriteEntity WHERE type = \"PLACE\" AND placeId = :placeId")
    suspend fun deletePlace(placeId: String)

    @Transaction
    @Query("SELECT * FROM favouriteEntity")
    fun get(): Flow<List<FavouriteEntityEntityWithStopAndRoute>>

    @Query("SELECT * FROM favouriteEntity WHERE type = \"ROUTE\" AND routeId = :routeId")
    fun getRoute(routeId: String): Flow<FavouriteEntity?>

    @Query("SELECT * FROM favouriteEntity WHERE type = \"STOP\" AND stopId = :stopId")
    fun getStop(stopId: String): Flow<FavouriteEntity?>

    @Query("SELECT * FROM favouriteEntity WHERE type = \"STOP_ON_ROUTE\" AND routeId = :routeId AND stopId = :stopId AND heading = :heading")
    fun getStopOnRoute(stopId: String, routeId: String, heading: String?): Flow<FavouriteEntity?>

    @Query("SELECT * FROM favouriteEntity WHERE type = \"PLACE\" AND placeId = :placeId")
    fun getPlace(placeId: String): Flow<FavouriteEntity?>

}