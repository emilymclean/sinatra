package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.room.entities.StopTimetableTimeEntity
import cl.emilym.sinatra.room.entities.StopTimetableTimeEntityWithRouteEntity

@Dao
interface StopTimetableTimeEntityDao {

    @Insert
    suspend fun insert(vararg stops: StopTimetableTimeEntity)

    @Query("DELETE FROM stopTimetableTimeEntity WHERE resource = :resource")
    suspend fun clear(resource: ResourceKey)

    @Transaction
    @Query("SELECT * FROM stopTimetableTimeEntity WHERE resource = :resource")
    suspend fun get(resource: ResourceKey): List<StopTimetableTimeEntityWithRouteEntity>

}