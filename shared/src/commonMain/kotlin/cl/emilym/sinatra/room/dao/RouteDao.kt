package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.room.entities.RouteEntity
import cl.emilym.sinatra.room.entities.StopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    @Insert
    suspend fun insert(vararg routes: RouteEntity)

    @Query("DELETE FROM routeEntity")
    suspend fun clear()

    @Query("SELECT * FROM routeEntity")
    suspend fun get(): List<RouteEntity>

    @Query("SELECT * FROM routeEntity WHERE id = :id")
    suspend fun get(id: String): RouteEntity?

}