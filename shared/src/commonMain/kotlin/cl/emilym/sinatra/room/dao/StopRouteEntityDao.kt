package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.room.entities.RouteHeadingEntity
import cl.emilym.sinatra.room.entities.StopRouteEntity
import cl.emilym.sinatra.room.entities.StopRouteEntityWithRoute

@Dao
interface StopRouteEntityDao {

    @Insert
    suspend fun insert(vararg routes: StopRouteEntity)

    @Query("DELETE FROM stopRouteEntity WHERE resource = :resource")
    suspend fun clear(resource: ResourceKey)

    @Query("SELECT * FROM stopRouteEntity WHERE resource = :resource")
    suspend fun get(resource: ResourceKey): List<StopRouteEntityWithRoute>

}