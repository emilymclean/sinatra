package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.room.entities.ServiceAlertEntity

@Dao
interface ServiceAlertDao {

    @Insert
    suspend fun insert(vararg routes: ServiceAlertEntity)

    @Query("UPDATE serviceAlertEntity SET viewed = true WHERE id = :id")
    suspend fun markViewed(id: String)

    @Query("DELETE FROM serviceAlertEntity")
    suspend fun clear()

    @Query("SELECT * FROM serviceAlertEntity")
    suspend fun get(): List<ServiceAlertEntity>

    @Query("SELECT * FROM serviceAlertEntity WHERE id = :id")
    suspend fun get(id: String): ServiceAlertEntity?

    @Query("SELECT id FROM serviceAlertEntity WHERE viewed = true")
    suspend fun getViewed(): List<String>
    
}