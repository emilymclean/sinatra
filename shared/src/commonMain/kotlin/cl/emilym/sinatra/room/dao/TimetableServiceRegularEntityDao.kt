package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.TimetableServiceRegular
import cl.emilym.sinatra.room.entities.StopEntity
import cl.emilym.sinatra.room.entities.StopTimetableTimeEntity
import cl.emilym.sinatra.room.entities.TimetableServiceExceptionEntity
import cl.emilym.sinatra.room.entities.TimetableServiceRegularEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableServiceRegularEntityDao {

    @Insert
    suspend fun insert(vararg exceptions: TimetableServiceRegularEntity)

    @Query("DELETE FROM timetableServiceRegularEntity")
    suspend fun clear()

    @Query("SELECT * FROM timetableServiceRegularEntity WHERE serviceId = :serviceId")
    suspend fun get(serviceId: ServiceId): List<TimetableServiceRegularEntity>

    @Query("SELECT * FROM timetableServiceRegularEntity")
    suspend fun get(): List<TimetableServiceRegularEntity>

}