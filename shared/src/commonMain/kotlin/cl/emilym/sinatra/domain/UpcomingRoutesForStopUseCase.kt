package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.flatMap
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.data.repository.startOfDay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.minutes

@Factory
class UpcomingRoutesForStopUseCase(
    private val liveStopTimetableUseCase: LiveStopTimetableUseCase,
    private val stopRepository: StopRepository,
    private val serviceRepository: ServiceRepository,
    private val clock: Clock,
    private val metadataRepository: TransportMetadataRepository
) {

    operator fun invoke(
        stopId: StopId,
        number: Int = 10
    ): Flow<Cachable<List<IStopTimetableTime>>> {
        return flow {
            val scheduleTimeZone = metadataRepository.timeZone()
            val timetable = stopRepository.timetable(stopId)
            val times = timetable.item.times.sortedBy { it.arrivalTime }
            val services = serviceRepository.services(timetable.item.times.map { it.serviceId }.distinct())

            while (true) {
                val now = clock.now()
                val active = mutableListOf<StopTimetableTime>()
                for (time in times) {
                    val service = services.item.firstOrNull { it.id == time.serviceId } ?: continue
                    if (!service.active(now, scheduleTimeZone)) continue
                    if (time.arrivalTime(now.startOfDay(scheduleTimeZone))!! < now) continue
                    active.add(time)
                    if (active.size == number) break
                }

                emit(timetable.flatMap { services.map { active.toList() } })
                delay(1.minutes)
            }
        }.flatMapLatest { original ->
            liveStopTimetableUseCase(
                stopId,
                original.item
            ).map { live -> original.map { live } }
        }
    }

}