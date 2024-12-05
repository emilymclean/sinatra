package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.room.entities.RouteTripInformationEntity
import cl.emilym.sinatra.room.entities.StopTimetableTimeEntity

@Dao
interface RouteTripInformationEntityDao {

    @Insert
    suspend fun insert(info: RouteTripInformationEntity): Long

    @Query("DELETE FROM routeTripInformationEntity WHERE resource = :resource")
    suspend fun clear(resource: ResourceKey)

    @Query("SELECT * FROM routeTripInformationEntity WHERE resource = :resource")
    suspend fun get(resource: ResourceKey): List<RouteTripInformationEntity>

}