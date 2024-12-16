package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.room.entities.RouteServiceEntity

@Dao
interface RouteServiceEntityDao {

    @Insert
    suspend fun insert(vararg services: RouteServiceEntity)

    @Query("DELETE FROM routeServiceEntity WHERE resource = :resource")
    suspend fun clear(resource: ResourceKey)

    @Query("SELECT * FROM routeServiceEntity WHERE resource = :resource")
    suspend fun get(resource: ResourceKey): List<RouteServiceEntity>

}