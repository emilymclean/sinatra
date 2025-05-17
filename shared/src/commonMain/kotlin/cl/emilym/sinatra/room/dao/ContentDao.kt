package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import cl.emilym.sinatra.room.entities.ContentEntity
import cl.emilym.sinatra.room.entities.ContentEntityWithContentLinkEntity
import cl.emilym.sinatra.room.entities.ContentLinkEntity

@Dao
interface ContentDao {

    @Insert
    suspend fun insert(vararg content: ContentEntity)

    @Query("DELETE FROM contentEntity")
    suspend fun clear()

    @Query("SELECT * FROM contentEntity")
    @Transaction
    suspend fun get(): List<ContentEntityWithContentLinkEntity>

    @Query("SELECT * FROM contentEntity WHERE id = :id")
    @Transaction
    suspend fun get(id: String): ContentEntityWithContentLinkEntity?

}

@Dao
interface ContentLinkDao {

    @Insert
    suspend fun insert(vararg contentLink: ContentLinkEntity)

    @Query("SELECT * FROM contentLinkEntity WHERE contentId = :contentId")
    suspend fun get(contentId: String): List<ContentLinkEntity>

}