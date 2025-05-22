package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.repository.RouteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory

@Factory
class VisibleBrowseRouteUseCase(
    private val routeRepository: RouteRepository,
    private val currentTripForRouteUseCase: CurrentTripForRouteUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<CurrentTripInformation?> {
        return flow {
            val route = routeRepository.showOnBrowse().item.firstOrNull() ?: return@flow emit(null)
            emitAll(
                currentTripForRouteUseCase(route.id).mapLatest {
                    it.item
                }
            )
        }
    }

}