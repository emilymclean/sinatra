package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.room.entities.RouteTripInformationEntity
import cl.emilym.sinatra.room.entities.RouteTripStopEntity
import cl.emilym.sinatra.room.entities.StopTimetableTimeEntity

@Dao
interface RouteTripStopEntityDao {

    @Insert
    suspend fun insert(info: List<RouteTripStopEntity>)

    @Query("SELECT * FROM routeTripStopEntity WHERE routeTripInformationEntityId = :routeTripInformationEntityId")
    suspend fun get(routeTripInformationEntityId: Long): List<RouteTripStopEntity>

}