package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.DelayInformation
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.LiveRouteTripInformation
import cl.emilym.sinatra.data.models.LiveStopTimetableTime
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.TimetableStationTime
import cl.emilym.sinatra.data.repository.LiveServiceRepository
import cl.emilym.sinatra.data.repository.StopRepository
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
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory
import kotlin.collections.map
import kotlin.time.Duration.Companion.minutes

@Factory
class LiveStopTimetableUseCase(
    private val liveServiceRepository: LiveServiceRepository,
    private val stopRepository: StopRepository,
    private val clock: Clock
): LiveUseCase() {

    companion object {
        val EXPIRE_LEEWAY = 10.minutes
    }

    operator fun invoke(
        stopId: StopId,
        scheduled: List<IStopTimetableTime>
    ): Flow<List<IStopTimetableTime>> {
        return flow {
            if (stopRepository.stop(stopId).item?.hasRealtime != true) return@flow emit(scheduled)
            val realtime = liveServiceRepository.getStopRealtimeUpdates(stopId)
            if (realtime.expire + EXPIRE_LEEWAY < clock.now()) return@flow emit(scheduled)

            emit(
                scheduled.map { stopTimetableTime ->
                    val delay = realtime.updates.firstOrNull { it.tripId == stopTimetableTime.tripId }?.delay
                    if (delay == null || delay is DelayInformation.Unknown) return@map stopTimetableTime
                    LiveStopTimetableTime.fromOther(
                        stopTimetableTime,
                        TimetableStationTime(
                            arrival = decodeTime(
                                delay,
                                stopTimetableTime.arrivalTime,
                            ),
                            departure = decodeTime(
                                delay,
                                stopTimetableTime.departureTime,
                            )
                        )
                    )
                }.sortedBy { it.arrivalTime }
            )
        }.catch {
            Napier.e(it)
            emit(scheduled)
        }
    }

}