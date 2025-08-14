package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.Preference
import cl.emilym.sinatra.data.repository.PreferencesRepository
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.StopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import org.koin.core.annotation.Factory

@Factory
class GetFilteredRoutesUseCase(
    private val routeRepository: RouteRepository,
    private val preferencesRepository: PreferencesRepository
) {

    operator fun invoke(): Flow<Cachable<List<Route>>> = flow {
        val routes = routeRepository.routes()
        emitAll(
            filterRoutes(routes.item).mapLatest { r -> routes.map { r } }
        )
    }

    fun filterRoutes(routes: List<Route>): Flow<List<Route>> {
        return preferencesRepository.preference(Preference.ShowSchoolServices).flow.mapLatest {
            when (it) {
                true -> routes
                else -> routes.filter { !it.schoolService }
            }.filterAndSort()
        }
    }

}