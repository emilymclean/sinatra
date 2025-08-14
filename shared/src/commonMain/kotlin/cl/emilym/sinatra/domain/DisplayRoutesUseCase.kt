package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.RouteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class DisplayRoutesUseCase(
    private val getFilteredRoutesUseCase: GetFilteredRoutesUseCase
) {

    operator fun invoke(): Flow<Cachable<List<Route>>> {
        return getFilteredRoutesUseCase().map {
            it.map { it.filterAndSort() }
        }
    }

}

@Deprecated("Use GetFilteredRoutesUseCase")
internal fun List<Route>.filterAndSort(): List<Route> =
    filterNot { it.routeVisibility.hidden }.sortedWith(compareBy(
        { !it.eventRoute }, { it.designation == null }, { it.code.toIntOrNull() }
    ))