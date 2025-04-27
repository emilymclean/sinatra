package cl.emilym.sinatra.domain.smart

import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.NavigationObject
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.models.specialType
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory

data class QuickNavigation(
    val navigation: NavigationObject,
    val specialType: SpecialFavouriteType?,
    val important: Boolean
)

@Factory
class QuickNavigateUseCase(
    private val favouriteRepository: FavouriteRepository,
    private val remoteConfigRepository: RemoteConfigRepository
) {

    companion object {
        const val DISTANCE_THRESHOLD = 1f
        const val QUICK_NAVIGATION_FEATURE_FLAG = "quick_navigation_home_screen"
    }

    operator fun invoke(currentLocation: MapLocation?): Flow<List<QuickNavigation>> {
        return flow {
            if (!remoteConfigRepository.feature(QUICK_NAVIGATION_FEATURE_FLAG)) {
                emit(listOf())
                return@flow
            }

            emitAll(
                favouriteRepository.all().mapLatest {
                    it
                        .filter { it.specialType != null }
                        .filter {
                            currentLocation ?: return@filter true
                            val location = when (it) {
                                is Favourite.Stop -> it.stop.location
                                is Favourite.Place -> it.place.location
                                else -> null
                            } ?: return@filter false
                            distance(location, currentLocation) > DISTANCE_THRESHOLD
                        }
                        .sortedBy { it.specialType?.ordinal }
                        .mapNotNull {
                            QuickNavigation(
                                when (it) {
                                    is Favourite.Stop -> it.stop
                                    is Favourite.Place -> it.place
                                    else -> return@mapNotNull null
                                },
                                it.specialType,
                                false
                            )
                        }
                }
            )
        }
    }

}