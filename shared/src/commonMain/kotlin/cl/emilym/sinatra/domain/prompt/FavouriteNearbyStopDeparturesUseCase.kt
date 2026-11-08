package cl.emilym.sinatra.domain.prompt

import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.domain.NearbyStopsUseCase
import cl.emilym.sinatra.domain.UpcomingRoutesForStopUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transformLatest
import org.koin.core.annotation.Factory

sealed interface NearbyStopInformation {
    data object None: NearbyStopInformation

    data class Single(
        val departures: StopDepartures
    ): NearbyStopInformation

    data class Multiple(
        val departures: List<StopDepartures>
    ): NearbyStopInformation
}

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

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(currentLocation: MapLocation): Flow<NearbyStopInformation> {
        return flow {
            if (!remoteConfigRepository.feature(FeatureFlag.FAVOURITE_NEARBY_STOP_HOME_SCREEN)) {
                emit(NearbyStopInformation.None)
                return@flow
            }

            val nearby = nearbyStopsUseCase(
                currentLocation,
                limit = 30
            )

            emitAll(favouriteRepository.favouritedStops(nearby.map { it.stop.id })
                .transformLatest { stopIds ->
                    val favourites = nearby.filter { it.stop.id in stopIds }
                    val closest = favourites.firstOrNull()

                    if (closest == null) {
                        emit(emptyList())
                        return@transformLatest
                    }

                    if (!remoteConfigRepository.feature(FeatureFlag.FAVOURITE_NEARBY_STOP_HOME_SCREEN_MULTIPLE)){
                        emit(listOf(closest.stop))
                        return@transformLatest
                    }

                    emit(
                        buildList {
                            add(closest.stop)
                            addAll(
                                favourites
                                    .drop(1)
                                    .filter { it.distance <= INNER_RING_RADIUS }
                                    .map { it.stop }
                            )
                        }
                    )
                }
                .flatMapLatest { stops ->
                    if (stops.isEmpty()) return@flatMapLatest flowOf(NearbyStopInformation.None)
                    combine(stops.map { upcomingRoutesForStopUseCase(it.id) }) {
                        val departures = it.mapIndexed { index, it ->
                            StopDepartures(stops[index], it.item)
                        }.filter {
                            it.departures.isNotEmpty()
                        }.take(MAXIMUM_STOP_COUNT)

                        when (departures.size) {
                            0 -> NearbyStopInformation.None
                            1 -> NearbyStopInformation.Single(departures.first())
                            else -> NearbyStopInformation.Multiple(departures)
                        }
                    }
                }
            )
        }
    }

    companion object {
        const val INNER_RING_RADIUS = 0.35 // 350 meters
        const val MAXIMUM_STOP_COUNT = 5
    }

}