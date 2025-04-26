package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.models.PlaceId
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.data.persistence.FavouritePersistence
import cl.emilym.sinatra.data.persistence.FavouriteType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class FavouriteRepository(
    private val stopRepository: StopRepository,
    private val routeRepository: RouteRepository,
    private val favouritePersistence: FavouritePersistence
) {

    suspend fun remove(favourite: Favourite) {
        favouritePersistence.remove(favourite)
    }

    fun all(): Flow<List<Favourite>> {
        return flow {
            stopRepository.stops()
            routeRepository.routes()
            emitAll(favouritePersistence.all())
        }
    }

    suspend fun setRouteFavourite(routeId: RouteId, favourited: Boolean) {
        when (favourited) {
            true -> favouritePersistence.add(FavouriteType.ROUTE, routeId = routeId)
            false -> favouritePersistence.remove(FavouriteType.ROUTE, routeId = routeId)
        }
    }

    fun routeIsFavourited(routeId: RouteId): Flow<Boolean> {
        return favouritePersistence.exists(
            FavouriteType.ROUTE,
            routeId = routeId
        )
    }

    suspend fun setStopFavourite(
        stopId: StopId,
        favourited: Boolean,
        specialFavourite: SpecialFavouriteType? = null,
    ) {
        when (favourited) {
            true -> favouritePersistence.add(FavouriteType.STOP, stopId = stopId, extra = specialFavourite?.name)
            false -> favouritePersistence.remove(FavouriteType.STOP, stopId = stopId)
        }
    }

    fun stopIsFavourited(stopId: StopId): Flow<Boolean> {
        return favouritePersistence.exists(
            FavouriteType.STOP,
            stopId = stopId
        )
    }

    suspend fun setPlaceFavourite(placeId: PlaceId, favourited: Boolean) {
        when (favourited) {
            true -> favouritePersistence.add(FavouriteType.PLACE, placeId = placeId)
            false -> favouritePersistence.remove(FavouriteType.PLACE, placeId = placeId)
        }
    }

    fun placeIsFavourited(placeId: PlaceId): Flow<Boolean> {
        return favouritePersistence.exists(
            FavouriteType.PLACE,
            placeId = placeId
        )
    }

}