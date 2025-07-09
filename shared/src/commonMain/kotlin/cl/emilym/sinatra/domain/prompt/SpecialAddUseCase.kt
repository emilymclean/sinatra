package cl.emilym.sinatra.domain.prompt

import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.data.models.specialType
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory

@Factory
class SpecialAddUseCase(
    private val favouriteRepository: FavouriteRepository,
    private val remoteConfigRepository: RemoteConfigRepository
) {

    companion object {
        const val QUICK_ADD_NAVIGATION_FEATURE_FLAG = "quick_add_favourite_home_screen"
    }

    operator fun invoke(): Flow<List<SpecialFavouriteType>> {
        return flow {
            if (!remoteConfigRepository.feature(FeatureFlag.QUICK_ADD_FAVOURITE_HOME_SCREEN)) {
                emit(listOf())
                return@flow
            }

            emitAll(
                favouriteRepository.all().mapLatest { q ->
                    SpecialFavouriteType.entries.filter { e -> !q.any { it.specialType == e } }
                }
            )
        }
    }
}