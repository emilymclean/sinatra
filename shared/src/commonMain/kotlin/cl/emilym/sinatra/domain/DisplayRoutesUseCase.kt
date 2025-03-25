package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.RouteRepository
import org.koin.core.annotation.Factory

@Factory
class DisplayRoutesUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(): Cachable<List<Route>> {
        return routeRepository.routes().map {
            it.filterNot { it.routeVisibility.hidden }.sortedWith(compareBy(
                { it.designation == null }, { it.code.toIntOrNull() }
            ))
        }
    }

}