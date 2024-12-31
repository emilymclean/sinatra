package cl.emilym.sinatra.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.emilym.sinatra.room.entities.RecentVisitEntity
import cl.emilym.sinatra.room.entities.RecentVisitEntityWithStopAndRoute
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentVisitDao {

    @Insert
    suspend fun insert(visit: RecentVisitEntity): Long

    @Query("DELETE FROM recentVisitEntity WHERE id < :upToId")
    suspend fun deleteBelowId(upToId: Long)

    @Query("DELETE FROM recentVisitEntity WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM recentVisitEntity WHERE type = \"ROUTE\" AND routeId = :routeId")
    suspend fun deleteRouteVisit(routeId: String)

    @Query("DELETE FROM recentVisitEntity WHERE type = \"STOP\" AND stopId = :stopId")
    suspend fun deleteStopVisit(stopId: String)

    @Query("DELETE FROM recentVisitEntity WHERE type = \"PLACE\" AND placeId = :placeId")
    suspend fun deletePlaceVisit(placeId: String)

    @Query("SELECT * FROM recentVisitEntity ORDER BY id DESC LIMIT 10")
    fun getFlow(): Flow<List<RecentVisitEntityWithStopAndRoute>>

    @Query("SELECT * FROM recentVisitEntity ORDER BY id DESC")
    suspend fun all(): List<RecentVisitEntity>

}