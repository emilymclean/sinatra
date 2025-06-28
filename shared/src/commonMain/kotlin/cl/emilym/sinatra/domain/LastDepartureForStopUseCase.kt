package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import io.github.aakira.napier.Napier.i
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory
import kotlin.text.Typography.times
import kotlin.time.Duration.Companion.days

@Factory
class LastDepartureForStopUseCase(
    private val servicesAndTimesForStopUseCase: ServicesAndTimesForStopUseCase,
    private val metadataRepository: TransportMetadataRepository,
    private val clock: Clock
) {

    operator fun invoke(
        stopId: StopId,
        routeId: RouteId? = null
    ): Flow<List<IStopTimetableTime>> = flow {
        val scheduleTimeZone = metadataRepository.timeZone()
        val now = clock.now()
        val days = listOf(now - 1.days, now)

        val timesAndServices = servicesAndTimesForStopUseCase(stopId)
        val activeServices = days.map { now ->
            timesAndServices.item.services.filter { it.active(
                now,
                scheduleTimeZone
            ) }
        }

        if (activeServices.all { it.isEmpty() }) return@flow emit(emptyList())

        val lastByDay = activeServices.mapIndexed { i, activeServices ->
            activeServices.map { activeService ->
                timesAndServices.item.times
                    .filter { it.serviceId == activeService.id }
                    .filterNot { it.last }
                    .run {
                        when (routeId) {
                            null -> this
                            else -> filter { it.routeId == routeId }
                        }
                    }
                    .groupBy { it.routeId to it.heading }
                    .values
                    .map { it.last().withTimeReference(days[i].startOfDay(scheduleTimeZone)) }
            }.flatten()
        }.flatten()

        emit(
            lastByDay
                .groupBy { it.routeId to it.heading }
                .values
                .mapNotNull {
                    it.firstOrNull { it.departureTime >= now } ?: when {
                        it.size > 1 -> it[1]
                        else -> null
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