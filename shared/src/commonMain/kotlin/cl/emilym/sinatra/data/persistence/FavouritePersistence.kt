package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.room.dao.FavouriteDao
import cl.emilym.sinatra.room.entities.FavouriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

enum class FavouriteType {
    ROUTE, STOP, STOP_ON_ROUTE;

    companion object {
        fun fromFavourite(favourite: Favourite): FavouriteType {
            return when (favourite) {
                is Favourite.Route -> ROUTE
                is Favourite.Stop -> STOP
                is Favourite.StopOnRoute -> STOP_ON_ROUTE
            }
        }
    }
}

@Factory
class FavouritePersistence(
    private val favouriteDao: FavouriteDao
) {

    private fun validate(type: FavouriteType, routeId: RouteId?, stopId: StopId?) {
        when (type) {
            FavouriteType.ROUTE -> if (routeId == null || stopId != null)
                throw IllegalArgumentException("A route must specify route ID and must not specify stop ID")
            FavouriteType.STOP -> if (routeId != null || stopId == null)
                throw IllegalArgumentException("A stop must not specify route ID and must specify stop ID")
            FavouriteType.STOP_ON_ROUTE -> if (routeId == null || stopId == null)
                throw IllegalArgumentException("A stop on route must specify both a route ID and a stop ID")
        }
    }

    suspend fun add(type: FavouriteType, routeId: RouteId? = null, stopId: StopId? = null) {
        validate(type, routeId, stopId)
        remove(type, routeId, stopId)

        val entity = FavouriteEntity(
            0,
            type.name,
            routeId,
            stopId
        )
        favouriteDao.insert(entity)
    }

    suspend fun add(favourite: Favourite) {
        add(FavouriteType.fromFavourite(favourite), favourite.routeId, favourite.stopId)
    }

    suspend fun remove(favourite: Favourite) {
        remove(FavouriteType.fromFavourite(favourite), favourite.routeId, favourite.stopId)
    }

    suspend fun remove(type: FavouriteType, routeId: RouteId? = null, stopId: StopId? = null) {
        validate(type, routeId, stopId)
        when (type) {
            FavouriteType.ROUTE -> favouriteDao.deleteRoute(routeId!!)
            FavouriteType.STOP -> favouriteDao.deleteStop(stopId!!)
            FavouriteType.STOP_ON_ROUTE -> favouriteDao.deleteStopOnRoute(stopId!!, routeId!!)
        }
    }

    fun all(): Flow<List<Favourite>> {
        return favouriteDao.get().map {
            it.sortedByDescending { it.favourite.id }.mapNotNull {
                val type = FavouriteType.valueOf(it.favourite.type)
                when (type) {
                    FavouriteType.ROUTE -> it.route?.let { Favourite.Route(it.toModel()) }
                    FavouriteType.STOP -> it.stop?.let { Favourite.Stop(it.toModel()) }
                    FavouriteType.STOP_ON_ROUTE ->
                        it.stop?.let { stop ->
                            it.route?.let { route ->
                                Favourite.StopOnRoute(stop.toModel(), route.toModel())
                            }
                        }
                }
            }
        }
    }

    fun exists(type: FavouriteType, routeId: RouteId? = null, stopId: StopId? = null): Flow<Boolean> {
        validate(type, routeId, stopId)
        return when (type) {
            FavouriteType.ROUTE -> favouriteDao.getRoute(routeId!!)
            FavouriteType.STOP -> favouriteDao.getStop(stopId!!)
            FavouriteType.STOP_ON_ROUTE -> favouriteDao.getStopOnRoute(stopId!!, routeId!!)
        }.map {
            it != null
        }
    }

    private val Favourite.routeId: RouteId? get() = when (this) {
        is Favourite.Route -> route.id
        is Favourite.StopOnRoute -> route.id
        else -> null
    }

    private val Favourite.stopId: StopId? get() = when (this) {
        is Favourite.Stop -> stop.id
        is Favourite.StopOnRoute -> stop.id
        else -> null
    }

}