package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.StopRepository
import org.koin.core.annotation.Factory

data class ServicesAndTimes(
    val services: List<Service>,
    val times: List<StopTimetableTime>
)

@Factory
class ServicesAndTimesForStopUseCase(
    private val stopRepository: StopRepository,
    private val serviceRepository: ServiceRepository
) {

    suspend operator fun invoke(
        stopId: StopId
    ): Cachable<ServicesAndTimes> {
        val timetable = stopRepository.timetable(stopId)
        val times = timetable.item.times.sortedBy { it.arrivalTime }
        val services = serviceRepository.services(timetable.item.times.map { it.serviceId }.distinct())

        return services.map { ServicesAndTimes(
            it,
            times
        ) }
    }

}