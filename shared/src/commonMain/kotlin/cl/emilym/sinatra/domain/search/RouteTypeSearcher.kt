package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.repository.RouteRepository
import org.koin.core.annotation.Factory

@Factory
class RouteTypeSearcher(
    private val routeRepository: RouteRepository
): LocalTypeSearcher<Route>() {

    override fun fields(t: Route) = listOf(t.name, t.displayCode)

    override fun scoreMultiplier(item: Route): Double {
        return when {
            item.colors != null -> 1.7
            item.name == "NIS" -> 0.2
            else -> 1.1
        }
    }

    override suspend fun load(): List<Route> {
        return routeRepository.routes().item
    }

    override fun wrap(item: Route): SearchResult {
        return SearchResult.RouteResult(item)
    }

}