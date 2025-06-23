package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.StopRepository
import org.koin.core.annotation.Factory

@Factory
class RoutesVisitingStopUseCase(
    private val stopRepository: StopRepository,
) {

    suspend operator fun invoke(
        stopId: StopId
    ): Cachable<List<Route>> {
        return stopRepository.timetable(stopId).map {
            it.times.mapNotNull { it.route }.distinct()
        }
    }

}