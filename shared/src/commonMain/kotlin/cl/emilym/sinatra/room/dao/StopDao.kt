package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.room.entities.StopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StopDao {

    @Insert
    suspend fun insert(vararg stops: StopEntity)

    @Query("DELETE FROM stopEntity")
    suspend fun clear()

    @Query("SELECT * FROM stopEntity")
    fun getFlow(): Flow<List<StopEntity>>

    @Query("SELECT * FROM stopEntity")
    suspend fun get(): List<StopEntity>

    @Query("SELECT * FROM stopEntity WHERE id = :id")
    suspend fun get(id: String): StopEntity?

}