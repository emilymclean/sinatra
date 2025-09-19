package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.StopRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class RoutesVisitingStopUseCase(
    private val stopRepository: StopRepository,
    private val getFilteredRoutesUseCase: GetFilteredRoutesUseCase
) {

    suspend operator fun invoke(
        stopId: StopId
    ): Flow<List<Route>> {
        val timetable = stopRepository.timetable(stopId).item
        return getFilteredRoutesUseCase.filterRoutes(
            timetable.times
                .mapNotNull { it.route }
                .distinctBy { it.id }
        )
    }

}