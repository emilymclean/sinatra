package cl.emilym.sinatra.domain

import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.models.Heading
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import io.github.aakira.napier.Napier.i
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory
import kotlin.text.Typography.times
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

private data class RouteAndHeading(
    val routeId: RouteId,
    val heading: Heading
)

@Factory
class LastDepartureForStopUseCase(
    private val servicesAndTimesForStopUseCase: ServicesAndTimesForStopUseCase,
    private val metadataRepository: TransportMetadataRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val clock: Clock
) {

    operator fun invoke(
        stopId: StopId,
        routeId: RouteId? = null
    ): Flow<List<IStopTimetableTime>> = flow {
        val scheduleTimeZone = metadataRepository.timeZone()
        val now = clock.now()
        val days = listOf(now - 1.days, now + 1.days, now)

        val timesAndServices = servicesAndTimesForStopUseCase(stopId)
        val activeServices = days.map { now ->
            timesAndServices.item.services.filter { it.active(
                now,
                scheduleTimeZone
            ) }
        }

        if (activeServices.all { it.isEmpty() }) return@flow emit(emptyList())

        val lasts = mutableMapOf<RouteAndHeading, Array<StopTimetableTime?>>()

        activeServices.forEachIndexed { i, activeServices ->
            val startOfDay = days[i].startOfDay(scheduleTimeZone)
            activeServices.forEach { activeService ->
                val relevant = timesAndServices.item.times
                    .filter { it.serviceId == activeService.id }
                    .filterNot { it.last }
                    .run {
                        when (routeId) {
                            null -> this
                            else -> filter { it.routeId == routeId }
                        }
                    }
                    .run {
                        when (i) {
                            1 -> filter { it.departureTime.durationThroughDay < 3.hours }
                            else -> this
                        }
                    }

                relevant.forEach { stopTime ->
                    val key = RouteAndHeading(stopTime.routeId, stopTime.heading)
                    val referenced = stopTime.withTimeReference(startOfDay)
                    val current = lasts.getOrPut(key){ Array(3) { null } }

                    if (current[i] == null || current[i]!!.departureTime < referenced.departureTime)
                        current[i] = referenced
                }
            }
        }

        emit(
            lasts
                .values
                .mapNotNull {
                    it.filterNotNull().firstOrNull { it.departureTime >= now } ?: it[2]
                }
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
                .sortedWith(compareBy(
                    { it.route?.eventRoute == false },
                    { it.route?.designation == null },
                    { it.routeCode.toIntOrNull() },
                    { it.heading }
                ))
        )
    }

}