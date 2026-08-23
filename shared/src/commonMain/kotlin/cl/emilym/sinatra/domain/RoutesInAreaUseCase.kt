package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.repository.RouteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory

data class RoutesInArea(
    val routes: List<Route>,
    val isRelevantToRegion: Boolean
)

@Factory
class RoutesInAreaUseCase(
    private val routeRepository: RouteRepository,
    private val getFilteredRoutesUseCase: GetFilteredRoutesUseCase
) {

    operator fun invoke(area: MapRegion): Flow<RoutesInArea> = flow {
        emitAll(
            getFilteredRoutesUseCase.filterRoutes(routeRepository.routesInBox(area)).map {
                RoutesInArea(
                    it,
                    true
                )
            }.catch {
                emitAll(getFilteredRoutesUseCase().map {
                    RoutesInArea(
                        it.item,
                        false
                    )
                })
            }
        )
    }

}