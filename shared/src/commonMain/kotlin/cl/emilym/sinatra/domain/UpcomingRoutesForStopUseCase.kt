package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.flatMap
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.data.repository.startOfDay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import org.koin.core.annotation.Factory

@Factory
class UpcomingRoutesForStopUseCase(
    private val stopRepository: StopRepository,
    private val serviceRepository: ServiceRepository,
    private val clock: Clock,
    private val metadataRepository: TransportMetadataRepository
) {

    suspend operator fun invoke(
        stopId: StopId,
        number: Int = 10
    ): Cachable<List<StopTimetableTime>> {
        val now = clock.now()
        val scheduleTimeZone = metadataRepository.timeZone()
        val timetable = stopRepository.timetable(stopId)
        val services = serviceRepository.services(timetable.item.times.map { it.serviceId }.distinct())

        val active = mutableListOf<StopTimetableTime>()

        for (time in timetable.item.times) {
            val service = services.item.firstOrNull { it.id == time.serviceId } ?: continue
            if (!service.active(now, scheduleTimeZone)) continue
            if (time.arrivalTime(now.startOfDay(scheduleTimeZone)) > now) continue
            active.add(time)
            if (active.size == number) break
        }

        return timetable.flatMap { services.map { active } }
    }

}