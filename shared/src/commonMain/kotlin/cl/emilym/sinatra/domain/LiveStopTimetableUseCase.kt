package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.DelayInformation
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.LiveRouteTripInformation
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
import kotlin.collections.map

@Factory
class LiveStopTimetableUseCase(
    private val liveServiceRepository: LiveServiceRepository,
): LiveUseCase() {

    operator fun invoke(
        stopId: StopId,
        scheduled: List<IStopTimetableTime>
    ): Flow<List<IStopTimetableTime>> {
        return flow {
            val out = liveServiceRepository.getStopRealtimeUpdates(stopId).map { realtime ->
                scheduled.map { stopTimetableTime ->
                    val delay = realtime.updates.firstOrNull { it.tripId == stopTimetableTime.tripId }?.delay
                    LiveStopTimetableTime.fromOther(
                        stopTimetableTime,
                        TimetableStationTime(
                            arrival = decodeTime(
                                delay ?: DelayInformation.Unknown,
                                stopTimetableTime.arrivalTime,
                            ),
                            departure = decodeTime(
                                delay ?: DelayInformation.Unknown,
                                stopTimetableTime.departureTime,
                            )
                        )
                    )
                }.sortedBy { it.arrivalTime }
            }
            emitAll(out)
        }
    }

}