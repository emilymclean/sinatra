package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.room.entities.TimetableServiceExceptionEntity

@Dao
interface TimetableServiceExceptionEntityDao {

    @Insert
    suspend fun insert(vararg exceptions: TimetableServiceExceptionEntity)

    @Query("DELETE FROM timetableServiceExceptionEntity")
    suspend fun clear()

    @Query("SELECT * FROM timetableServiceExceptionEntity WHERE serviceId = :serviceId")
    suspend fun get(serviceId: ServiceId): List<TimetableServiceExceptionEntity>

    @Query("SELECT * FROM timetableServiceExceptionEntity")
    suspend fun get(): List<TimetableServiceExceptionEntity>

}