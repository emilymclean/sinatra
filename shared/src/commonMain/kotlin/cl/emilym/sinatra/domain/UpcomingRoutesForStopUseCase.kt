package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.flatMap
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.data.repository.ServiceRepository
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@Factory
class UpcomingRoutesForStopUseCase(
    private val liveStopTimetableUseCase: LiveStopTimetableUseCase,
    private val stopRepository: StopRepository,
    private val serviceRepository: ServiceRepository,
    private val clock: Clock,
    private val metadataRepository: TransportMetadataRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        stopId: StopId,
        number: Int = 10,
        live: Boolean = true
    ): Flow<Cachable<List<IStopTimetableTime>>> {
        return flow {
            val scheduleTimeZone = metadataRepository.timeZone()
            val timetable = stopRepository.timetable(stopId)
            val times = timetable.item.times.sortedBy { it.arrivalTime }
            val services = serviceRepository.services(timetable.item.times.map { it.serviceId }.distinct())

            while (true) {
                val now = clock.now()
                val checkTimes = listOf(now - 1.days, now)

                val active = mutableListOf<StopTimetableTime>()

                for (checkTime in checkTimes) {
                    for (time in times) {
                        val time = time.withTimeReference(checkTime.startOfDay(metadataRepository.timeZone()))

                        val service = services.item.firstOrNull { it.id == time.serviceId } ?: continue
                        if (!service.active(checkTime, scheduleTimeZone)) continue
                        if (time.arrivalTime < now) continue
                        active.add(time)
                        if (active.size == number) break
                    }
                    if (active.size == number) break
                }

                emit(timetable.flatMap { services.map { active.toList() } })
                delay(1.minutes)
            }
        }
        .flowOn(Dispatchers.IO)
//        .flatMapLatest { original ->
//            when {
//                original.item.isEmpty() || !live -> flowOf(original.map { it })
//                else -> liveStopTimetableUseCase(
//                    stopId,
//                    original.item
//                ).map { live -> original.map { live } }
//            }
//        }
//        .flowOn(Dispatchers.IO)
        // Literally does nothing but change Cachable<out List<IStopTimetableTime>> to not out
        .map {
            it.map { it.map { it } }
        }
    }

}