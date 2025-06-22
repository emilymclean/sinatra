package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.IStopTimetableTime
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
        stopId: StopId
    ): Flow<List<IStopTimetableTime>> = flow {
        val scheduleTimeZone = metadataRepository.timeZone()
        val now = clock.now()
        val days = listOf(now - 1.days, now)

        val timesAndServices = servicesAndTimesForStopUseCase(stopId)
        val activeServices = days.map { now ->
            timesAndServices.item.services.firstOrNull { it.active(
                now,
                scheduleTimeZone
            ) }
        }

        if (activeServices.all { it == null }) return@flow emit(emptyList())

        val lastByDay = activeServices.mapIndexed { i, activeService ->
            activeService?.let {
                timesAndServices.item.times
                    .filter { it.serviceId == activeService.id }
                    .groupBy { it.routeId to it.heading }
                    .values
                    .map { it.last().withTimeReference(days[i].startOfDay(scheduleTimeZone)) }
            } ?: emptyList()
        }.flatten()

        emit(
            lastByDay
                .groupBy { it.routeId to it.heading }
                .values
                .mapNotNull {
                    it.firstOrNull { it.departureTime >= now }
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