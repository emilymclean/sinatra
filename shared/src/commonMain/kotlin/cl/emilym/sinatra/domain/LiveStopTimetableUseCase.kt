package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.DelayInformation
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.LiveStopTimetableTime
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.TimetableStationTime
import cl.emilym.sinatra.data.repository.LiveServiceRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class LiveStopTimetableUseCase(
    private val liveServiceRepository: LiveServiceRepository,
    private val transportMetadataRepository: TransportMetadataRepository
): LiveUseCase() {

    operator fun invoke(
        stopId: StopId,
        scheduled: List<IStopTimetableTime>
    ): Flow<List<IStopTimetableTime>> {
        return flow {
            val scheduleStartOfDay = transportMetadataRepository.scheduleStartOfDay()
            val scheduleTimeZone = transportMetadataRepository.timeZone()

            val out = scheduled.groupBy { it.route }.map { groups ->
                when {
                    groups.key?.realTimeUrl == null -> flowOf(groups.value)
                    else -> liveServiceRepository.getRealtimeUpdates(groups.key!!.realTimeUrl!!).map {
                        val updates = it.entity
                            .filterNot { it.isDeleted == true }
                            .mapNotNull { it.tripUpdate }

                        groups.value.map { stopTimetableTime ->
                            val update = updates.firstOrNull {
                                it.trip.tripId == stopTimetableTime.tripId
                            } ?: return@map stopTimetableTime
                            val delay = update.delay
                            val s = update.stopTimeUpdate.firstOrNull { it.stopId == stopId }

                            LiveStopTimetableTime.fromOther(
                                stopTimetableTime,
                                TimetableStationTime(
                                    arrival = decodeTime(
                                        s?.arrival,
                                        DelayInformation.Unknown,
                                        stopTimetableTime.arrivalTime,
                                        scheduleStartOfDay,
                                        scheduleTimeZone
                                    ),
                                    departure = decodeTime(
                                        s?.departure,
                                        DelayInformation.Unknown,
                                        stopTimetableTime.departureTime,
                                        scheduleStartOfDay,
                                        scheduleTimeZone
                                    )
                                )
                            )
                        }
                    }.catch {
                        Napier.e(it)
                        emit(groups.value)
                    }
                }
            }.let { combine(*it.toTypedArray()) { it.toList().flatten() } }.map {
                it.sortedBy { it.arrivalTime }
            }
            emitAll(out)
        }
    }

}