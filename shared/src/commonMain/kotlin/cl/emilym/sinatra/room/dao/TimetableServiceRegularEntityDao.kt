package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.room.entities.TimetableServiceRegularEntity

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