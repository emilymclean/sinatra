package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.repository.RouteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

data class RoutesInArea(
    val routes: List<Route>,
    val isRelevantToRegion: Boolean
)

@Factory
class RoutesInAreaUseCase(
    private val routeRepository: RouteRepository
) {

    operator fun invoke(area: MapRegion): Flow<RoutesInArea> = flow {
        if (area.diagonalDistance > MAX_DIAGONAL_DISTANCE) {
            emit(RoutesInArea(
                routeRepository.routes().item,
                false
            ))
            return@flow
        }

        emit(RoutesInArea(
            routeRepository.routesInBox(area),
            true
        ))
    }

    companion object {
        const val MAX_DIAGONAL_DISTANCE = 10.0
    }

}