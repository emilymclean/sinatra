package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.models.Heading
import cl.emilym.sinatra.data.models.PlaceId
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopSpecialType
import cl.emilym.sinatra.room.dao.FavouriteDao
import cl.emilym.sinatra.room.entities.FavouriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

enum class FavouriteType {
    ROUTE, STOP, STOP_ON_ROUTE, PLACE;

    companion object {
        fun fromFavourite(favourite: Favourite): FavouriteType {
            return when (favourite) {
                is Favourite.Route -> ROUTE
                is Favourite.Stop -> STOP
                is Favourite.StopOnRoute -> STOP_ON_ROUTE
                is Favourite.Place -> PLACE
            }
        }
    }
}

@Factory
class FavouritePersistence(
    private val favouriteDao: FavouriteDao
) {

    private fun validate(
        type: FavouriteType,
        routeId: RouteId? = null,
        stopId: StopId? = null,
        placeId: PlaceId? = null,
        heading: Heading? = null,
        extra: String? = null,
    ) {
        when (type) {
            FavouriteType.ROUTE -> if (routeId == null || stopId != null || placeId != null || heading != null || extra != null)
                throw IllegalArgumentException("A route must specify route ID and must not specify stop ID")
            FavouriteType.STOP -> if (routeId != null || stopId == null || placeId != null || heading != null)
                throw IllegalArgumentException("A stop must not specify route ID and must specify stop ID")
            FavouriteType.STOP_ON_ROUTE -> if (routeId == null || stopId == null || placeId != null || heading != null)
                throw IllegalArgumentException("A stop on route must specify both a route ID and a stop ID")
            FavouriteType.PLACE -> if (routeId != null || stopId != null || placeId == null || heading != null || extra != null)
                throw IllegalArgumentException("A place must specify a placeId and neither a route ID or a stop ID")
        }
    }

    suspend fun add(
        type: FavouriteType,
        routeId: RouteId? = null,
        stopId: StopId? = null,
        placeId: PlaceId? = null,
        heading: Heading? = null,
        extra: String? = null,
        order: Int = Int.MAX_VALUE
    ) {
        validate(type, routeId, stopId, placeId, heading, extra)
        remove(type, routeId, stopId, placeId)

        val entity = FavouriteEntity(
            0,
            type.name,
            routeId,
            stopId,
            placeId,
            heading,
            extra,
            order
        )
        favouriteDao.insert(entity)
    }

    suspend fun add(favourite: Favourite) {
        add(FavouriteType.fromFavourite(favourite), favourite.routeId, favourite.stopId, favourite.placeId, (favourite as? Favourite.Stop)?.specialType?.name)
    }

    suspend fun remove(favourite: Favourite) {
        remove(FavouriteType.fromFavourite(favourite), favourite.routeId, favourite.stopId, favourite.placeId)
    }

    suspend fun remove(type: FavouriteType, routeId: RouteId? = null, stopId: StopId? = null, placeId: PlaceId? = null) {
        validate(type, routeId, stopId, placeId)
        when (type) {
            FavouriteType.ROUTE -> favouriteDao.deleteRoute(routeId!!)
            FavouriteType.STOP -> favouriteDao.deleteStop(stopId!!)
            FavouriteType.STOP_ON_ROUTE -> favouriteDao.deleteStopOnRoute(stopId!!, routeId!!)
            FavouriteType.PLACE -> favouriteDao.deletePlace(placeId!!)
        }
    }

    fun all(): Flow<List<Favourite>> {
        return favouriteDao.get().map {
            it.sortedWith(
                compareBy({ it.favourite.order }, { -it.favourite.id })
            ).mapNotNull {
                val type = FavouriteType.valueOf(it.favourite.type)
                when (type) {
                    FavouriteType.ROUTE -> it.route?.let { Favourite.Route(
                        it.toModel()
                    ) }
                    FavouriteType.STOP -> it.stop?.let { stop -> Favourite.Stop(
                        stop.toModel(),
                        it.favourite.extra?.let {
                            try {
                                StopSpecialType.valueOf(it)
                            } catch (e: IllegalArgumentException) {
                                null
                            }
                        }
                    ) }
                    FavouriteType.STOP_ON_ROUTE ->
                        it.stop?.let { stop ->
                            it.route?.let { route ->
                                Favourite.StopOnRoute(
                                    stop.toModel(),
                                    route.toModel(),
                                    it.favourite.heading
                                )
                            }
                        }
                    FavouriteType.PLACE -> it.place?.let {
                        Favourite.Place(
                            it.toModel()
                        )
                    }
                }
            }
        }
    }

    fun exists(
        type: FavouriteType,
        routeId: RouteId? = null,
        stopId: StopId? = null,
        placeId: PlaceId? = null,
        heading: Heading? = null,
    ): Flow<Boolean> {
        validate(type, routeId, stopId, placeId, heading)
        return when (type) {
            FavouriteType.ROUTE -> favouriteDao.getRoute(routeId!!)
            FavouriteType.STOP -> favouriteDao.getStop(stopId!!)
            FavouriteType.STOP_ON_ROUTE -> favouriteDao.getStopOnRoute(stopId!!, routeId!!, heading)
            FavouriteType.PLACE -> favouriteDao.getPlace(placeId!!)
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

    private val Favourite.placeId: PlaceId? get() = when (this) {
        is Favourite.Place -> place.id
        else -> null
    }

}