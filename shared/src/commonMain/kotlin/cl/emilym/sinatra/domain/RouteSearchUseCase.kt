package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.RouteId

data class RouteFilter(
    val id: RouteId?,
    val shortName: String?
)

//@Factory
//class RouteSearchUseCase(
//    private val routeRepository: RouteRepository
//) {
//
//    suspend operator fun invoke(filter: RouteFilter): List<Route> {
//        return routeRepository.routes().filter {
//            listOf(
//                if (filter.id == null) true else filter.id == it.id,
//                if (filter.shortName == null) true else filter.shortName == filter.shortName
//            ).fold(true) { c,n -> c && n }
//        }
//    }
//
//}