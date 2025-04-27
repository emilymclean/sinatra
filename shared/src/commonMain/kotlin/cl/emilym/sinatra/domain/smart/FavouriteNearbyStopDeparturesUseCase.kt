package cl.emilym.sinatra.domain.smart

import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.domain.NearbyStopsUseCase
import cl.emilym.sinatra.domain.UpcomingRoutesForStopUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory

data class StopDepartures(
    val stop: Stop,
    val departures: List<IStopTimetableTime>
)

@Factory
class FavouriteNearbyStopDeparturesUseCase(
    private val nearbyStopsUseCase: NearbyStopsUseCase,
    private val favouriteRepository: FavouriteRepository,
    private val upcomingRoutesForStopUseCase: UpcomingRoutesForStopUseCase,
    private val remoteConfigRepository: RemoteConfigRepository
) {

    companion object {
        const val FAVOURITE_NEARBY_STOPS_FEATURE_FLAG = "favourite_nearby_stop_home_screen"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(currentLocation: MapLocation): Flow<StopDepartures?> {
        return flow {
            if (!remoteConfigRepository.feature(FAVOURITE_NEARBY_STOPS_FEATURE_FLAG)) {
                emit(null)
                return@flow
            }

            val nearby = nearbyStopsUseCase(
                currentLocation,
            )

            emitAll(favouriteRepository.favouritedStops(nearby.map { it.stop.id })
                .mapLatest { stopIds ->
                    nearby.firstOrNull { stopIds.contains(it.stop.id) }?.stop
                }
                .flatMapLatest { stop ->
                    stop ?: return@flatMapLatest flowOf(null)
                    upcomingRoutesForStopUseCase(stop.id).mapLatest {
                        if (it.item.isEmpty()) return@mapLatest null
                        StopDepartures(
                            stop,
                            it.item
                        )
                    }
                }
            )
        }
    }

}