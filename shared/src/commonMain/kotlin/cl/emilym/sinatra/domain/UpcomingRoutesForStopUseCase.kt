package cl.emilym.sinatra.domain

import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@Factory
class UpcomingRoutesForStopUseCase(
    private val liveStopTimetableUseCase: LiveStopTimetableUseCase,
    private val servicesAndTimesForStopUseCase: ServicesAndTimesForStopUseCase,
    private val clock: Clock,
    private val metadataRepository: TransportMetadataRepository,
    private val remoteConfigRepository: RemoteConfigRepository
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
                val startOfDay = now.startOfDay(scheduleTimeZone)
                val checkTimes = listOfNotNull(
                    startOfDay - 1.days,
                    startOfDay,
                    // If it is after 10pm (and the feature flag is enabled) search next day
                    if(
                        now.toLocalDateTime(scheduleTimeZone).hour >= 22 &&
                        remoteConfigRepository.feature(FeatureFlag.UPCOMING_ROUTES_INCLUDE_NEXT_DAY)
                    ) startOfDay + 1.days else null
                )

                val active = mutableListOf<StopTimetableTime>()

                for (checkTime in checkTimes) {
                    for (time in times) {
                        val time = time.withTimeReference(checkTime)

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
                                remoteConfigRepository.feature(FeatureFlag.STOP_DETAIL_HIDE_PLATFORM_FOR_SYNTHETIC) &&
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
        .mapLatest {
            it.map {
                it
                    .sortedBy { it.arrivalTime }
                    .filter { it.departureTime > clock.now() }
            }
        }
    }

}