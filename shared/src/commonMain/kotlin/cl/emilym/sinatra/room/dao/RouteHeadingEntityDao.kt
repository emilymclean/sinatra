package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.room.entities.RouteHeadingEntity

@Dao
interface RouteHeadingEntityDao {

    @Insert
    suspend fun insert(vararg headings: RouteHeadingEntity)

    @Query("DELETE FROM routeHeadingEntity WHERE resource = :resource")
    suspend fun clear(resource: ResourceKey)

    @Query("SELECT * FROM routeHeadingEntity WHERE resource = :resource")
    suspend fun get(resource: ResourceKey): List<RouteHeadingEntity>

}