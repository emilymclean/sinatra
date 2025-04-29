package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.room.entities.ServiceAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceAlertDao {

    @Insert
    suspend fun insert(vararg routes: ServiceAlertEntity)

    @Query("UPDATE serviceAlertEntity SET viewed = :viewed WHERE id = :id")
    suspend fun markViewed(id: String, viewed: Boolean = true): Int

    @Query("DELETE FROM serviceAlertEntity")
    suspend fun clear()

    @Query("SELECT * FROM serviceAlertEntity")
    suspend fun get(): List<ServiceAlertEntity>

    @Query("SELECT * FROM serviceAlertEntity")
    fun getLive(): Flow<List<ServiceAlertEntity>>

    @Query("SELECT * FROM serviceAlertEntity WHERE id = :id")
    suspend fun get(id: String): ServiceAlertEntity?

    @Query("SELECT id FROM serviceAlertEntity WHERE viewed = :viewed")
    suspend fun getViewed(viewed: Boolean = true): List<String>
    
}