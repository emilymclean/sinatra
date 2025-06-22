package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory

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

        val timesAndServices = servicesAndTimesForStopUseCase(stopId)
        val activeService = timesAndServices.item.services.firstOrNull { it.active(
            now,
            scheduleTimeZone
        ) } ?: return@flow emit(emptyList())

        emit(
            timesAndServices.item.times
                .filter { it.serviceId == activeService.id }
                .groupBy { it.routeId to it.heading }
                .values
                .map { it.last() }
        )
    }

}