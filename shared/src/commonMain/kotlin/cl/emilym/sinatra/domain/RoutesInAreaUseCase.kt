package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
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
        val routes = try {
            routeRepository.routesInBox(area)
        } catch (e: Exception) {
            Napier.e(e)
            emitAll(getFilteredRoutesUseCase().map {
                RoutesInArea(
                    it.item,
                    false
                )
            })
            return@flow
        }
        emitAll(
            getFilteredRoutesUseCase.filterRoutes(routes).map {
                RoutesInArea(
                    it,
                    true
                )
            }
        )
    }

}