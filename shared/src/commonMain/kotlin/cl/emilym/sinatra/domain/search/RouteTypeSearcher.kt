package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Route
import org.koin.core.annotation.Factory

@Factory
class RouteTypeSearcher: TypeSearcher<Route>() {

    override fun fields(t: Route) = listOf(t.name, t.displayCode)
}