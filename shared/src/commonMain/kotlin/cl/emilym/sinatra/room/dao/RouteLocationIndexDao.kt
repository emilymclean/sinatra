package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import cl.emilym.sinatra.room.entities.RouteLocationIndexEntity

@Dao
interface RouteLocationIndexDao {

    @Insert
    suspend fun insert(vararg indicies: RouteLocationIndexEntity)

    @Query("DELETE FROM routeLocationIndexEntity")
    suspend fun clear()

    @Query("SELECT * FROM routeLocationIndexEntity")
    suspend fun get(): List<RouteLocationIndexEntity>

    @Query("SELECT * FROM routeLocationIndexEntity WHERE id = :id")
    suspend fun get(id: String): RouteLocationIndexEntity?

    @Query(
        "SELECT * FROM routeLocationIndexEntity WHERE " +
                "lat BETWEEN :southLat AND :northLat AND " +
                "(lng BETWEEN :westLng AND :eastLng OR " +
                "(:westLng > :eastLng AND (lng BETWEEN :westLng AND 180 OR " +
                "lng BETWEEN -180 AND :eastLng)))"
    )
    suspend fun getInBox(
        southLat: Double,
        westLng: Double,
        northLat: Double,
        eastLng: Double
    ): List<RouteLocationIndexEntity>

    @Transaction
    suspend fun clearAndInsert(vararg indicies: RouteLocationIndexEntity) {
        clear()
        insert(*indicies)
    }

}