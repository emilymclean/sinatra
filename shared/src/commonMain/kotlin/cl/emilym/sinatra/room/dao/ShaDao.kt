package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.room.entities.ShaEntity

@Dao
interface ShaDao {

    @Insert
    suspend fun save(shaEntity: ShaEntity)

    @Delete
    suspend fun delete(shaEntity: ShaEntity)

    @Query("SELECT * FROM shaEntity WHERE type = :type AND resource = :resource")
    suspend fun shaByTypeAndResource(type: String, resource: String): ShaEntity?

}