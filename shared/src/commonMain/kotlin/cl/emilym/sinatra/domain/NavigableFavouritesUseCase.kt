package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory

@Factory
class NavigableFavouritesUseCase(
    private val favouriteRepository: FavouriteRepository
) {

    operator fun invoke(
        limit: Int = Int.MAX_VALUE
    ): Flow<List<Favourite>> {
        return favouriteRepository.all().mapLatest {
            it.filter {
                it is Favourite.Stop || it is Favourite.Place
            }.take(limit)
        }
    }

}