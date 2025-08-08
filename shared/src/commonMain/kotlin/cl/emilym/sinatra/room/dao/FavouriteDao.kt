package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.room.entities.FavouriteEntity
import cl.emilym.sinatra.room.entities.FavouriteEntityEntityWithStopAndRoute
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavouriteDao {

    @Insert
    abstract suspend fun insert(favourite: FavouriteEntity)

    @Delete
    abstract suspend fun delete(favourite: FavouriteEntity)

    @Query("DELETE FROM favouriteEntity WHERE type = \"ROUTE\" AND routeId = :routeId")
    abstract suspend fun deleteRoute(routeId: String)

    @Query("DELETE FROM favouriteEntity WHERE type = \"STOP\" AND stopId = :stopId")
    abstract suspend fun deleteStop(stopId: String)

    @Query("DELETE FROM favouriteEntity WHERE type = \"STOP_ON_ROUTE\" AND stopId = :stopId AND routeId = :routeId")
    abstract suspend fun deleteStopOnRoute(stopId: String, routeId: String)

    @Query("DELETE FROM favouriteEntity WHERE type = \"PLACE\" AND placeId = :placeId")
    abstract suspend fun deletePlace(placeId: String)

    @Query("DELETE FROM favouriteEntity WHERE extra = :specialFavouriteType")
    abstract suspend fun deleteSpecial(specialFavouriteType: String)

    @Transaction
    @Query("SELECT * FROM favouriteEntity")
    abstract fun get(): Flow<List<FavouriteEntityEntityWithStopAndRoute>>

    @Query("SELECT * FROM favouriteEntity WHERE type = \"ROUTE\" AND routeId = :routeId")
    abstract fun getRoute(routeId: String): Flow<FavouriteEntity?>

    @Query("SELECT * FROM favouriteEntity WHERE type = \"STOP\" AND stopId = :stopId")
    abstract fun getStop(stopId: String): Flow<FavouriteEntity?>

    @Query("SELECT stopId FROM favouriteEntity WHERE type = \"STOP\" AND stopId in (:stopIds)")
    abstract fun getStopIdExistence(stopIds: List<String>): Flow<List<String>>

    @Query("SELECT * FROM favouriteEntity WHERE type = \"STOP_ON_ROUTE\" AND routeId = :routeId AND stopId = :stopId AND heading = :heading")
    abstract fun getStopOnRoute(stopId: String, routeId: String, heading: String?): Flow<FavouriteEntity?>

    @Query("SELECT * FROM favouriteEntity WHERE type = \"PLACE\" AND placeId = :placeId")
    abstract fun getPlace(placeId: String): Flow<FavouriteEntity?>

    @Query("UPDATE favouriteEntity SET `order` = :order WHERE id = :id")
    protected abstract suspend fun updateOrder(id: Long, order: Int)

    @Query("UPDATE favouriteEntity SET `order` = `order` + 1 WHERE `order` >= :start")
    protected abstract suspend fun bumpOrder(start: Int)

    @Transaction
    open suspend fun reorder(id: Long, order: Int) {
        bumpOrder(order)
        updateOrder(id, order)
    }

}