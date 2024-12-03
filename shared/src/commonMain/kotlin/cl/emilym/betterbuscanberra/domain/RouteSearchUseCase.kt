package cl.emilym.betterbuscanberra.domain

import cl.emilym.betterbuscanberra.data.models.Route
import cl.emilym.betterbuscanberra.data.models.RouteId
import cl.emilym.betterbuscanberra.data.repository.RouteRepository
import org.koin.core.annotation.Factory

data class RouteFilter(
    val id: RouteId?,
    val shortName: String?
)

@Factory
class RouteSearchUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(filter: RouteFilter): List<Route> {
        return routeRepository.routes().filter {
            listOf(
                if (filter.id == null) true else filter.id == it.id,
                if (filter.shortName == null) true else filter.shortName == filter.shortName
            ).fold(true) { c,n -> c && n }
        }
    }

}