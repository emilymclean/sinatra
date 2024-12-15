package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.room.entities.RouteTripStopEntity
import cl.emilym.sinatra.room.entities.RouteTripStopEntityWithStop

@Dao
interface RouteTripStopEntityDao {

    @Insert
    suspend fun insert(info: List<RouteTripStopEntity>)

    @Transaction
    @Query("SELECT * FROM routeTripStopEntity WHERE routeTripInformationEntityId = :routeTripInformationEntityId AND resource = :resource")
    suspend fun get(routeTripInformationEntityId: Long, resource: ResourceKey): List<RouteTripStopEntityWithStop>

}