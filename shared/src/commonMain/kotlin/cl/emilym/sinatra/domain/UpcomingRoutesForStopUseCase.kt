package cl.emilym.sinatra.domain

import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.Cachable.Companion.live
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.RouteId
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
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.isActive
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory
import kotlin.text.Typography.times
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@Factory
class UpcomingRoutesForStopUseCase(
    private val liveStopTimetableUseCase: LiveStopTimetableUseCase,
    private val servicesAndTimesForStopUseCase: ServicesAndTimesForStopUseCase,
    private val clock: Clock,
    private val metadataRepository: TransportMetadataRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        stopId: StopId,
        routeIds: List<RouteId> = emptyList(),
        number: Int? = 10,
        live: Boolean = true
    ): Flow<Cachable<List<IStopTimetableTime>>> {
        return flow {
            val scheduleTimeZone = metadataRepository.timeZone()
            val timesAndServices = servicesAndTimesForStopUseCase(stopId)
            val times = timesAndServices.item.times.run {
                when {
                    routeIds.isEmpty() -> this
                    else -> filter { routeIds.contains(it.routeId) }
                }
            }
            val services = timesAndServices.item.services

            while (currentCoroutineContext().isActive) {
                val now = clock.now()
                val checkTimes = listOf(now - 1.days, now)

                val active = mutableListOf<StopTimetableTime>()

                for (checkTime in checkTimes) {
                    for (time in times) {
                        val time = time.withTimeReference(checkTime.startOfDay(metadataRepository.timeZone()))

                        val service = services.firstOrNull { it.id == time.serviceId } ?: continue
                        if (!service.active(checkTime, scheduleTimeZone)) continue
                        if (time.arrivalTime < now) continue

                        active.add(time)
                        if (active.size == number) break
                    }
                    if (active.size == number) break
                }

                emit(timesAndServices.map { active
                    .distinctBy { it.routeId to it.arrivalTime.instant }
                    .map {
                        when {
                            it.childStopId == stopId || (
                                FeatureFlags.STOP_DETAIL_HIDE_PLATFORM_FOR_SYNTHETIC &&
                                stopId.endsWith("-synthetic")
                            ) -> it.copy(
                                childStop = null,
                                childStopId = null
                            )
                            else -> it
                        }
                    }
                    .toList()
                })
                delay(1.minutes)
            }
        }
        .flowOn(Dispatchers.IO)
        .flatMapLatest { original ->
            when {
                original.item.isEmpty() || !live -> flowOf(original.map { it })
                else -> liveStopTimetableUseCase(
                    stopId,
                    original.item
                ).map { live -> original.map { live } }
            }
        }
        .flowOn(Dispatchers.IO)
        // Literally does nothing but change Cachable<out List<IStopTimetableTime>> to not out
        .map {
            it.map { it.map { it } }
        }
    }

}