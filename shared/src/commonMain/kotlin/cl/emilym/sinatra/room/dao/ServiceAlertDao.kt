package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.room.entities.ServiceAlertEntity

@Dao
interface ServiceAlertDao {

    @Insert
    suspend fun insert(vararg routes: ServiceAlertEntity)

    @Query("DELETE FROM serviceAlertEntity")
    suspend fun clear()

    @Query("SELECT * FROM serviceAlertEntity")
    suspend fun get(): List<ServiceAlertEntity>

    @Query("SELECT * FROM serviceAlertEntity WHERE id = :id")
    suspend fun get(id: String): ServiceAlertEntity?
    
}