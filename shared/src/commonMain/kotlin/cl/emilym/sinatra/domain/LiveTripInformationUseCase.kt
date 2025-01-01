package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.IRouteTripInformation
import cl.emilym.sinatra.data.models.LiveRouteTripInformation
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteTripStop
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.models.TimetableStationTime
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.models.toTodayTime
import cl.emilym.sinatra.data.repository.LiveServiceRepository
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.e
import com.google.transit.realtime.FeedMessage
import com.google.transit.realtime.TripUpdate
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.seconds

abstract class LiveUseCase {

    protected fun decodeTime(
        specific: TripUpdate.StopTimeEvent?,
        delay: Int?,
        expected: Time,
        scheduleStartOfDay: Instant
    ): StationTime {
        return StationTime.Live(
            specific?.time?.let {
                Instant.fromEpochSeconds(it).toTodayTime(scheduleStartOfDay)
            } ?: specific?.delay?.seconds?.let { it + expected } ?:
            delay?.seconds?.let { it + expected } ?: expected,
            specific?.time?.let {
                Instant.fromEpochSeconds(it).toTodayTime(scheduleStartOfDay) - expected
            } ?: specific?.delay?.seconds ?: delay?.seconds ?: 0.seconds
        )
    }

}

@Factory
class LiveTripInformationUseCase(
    private val liveServiceRepository: LiveServiceRepository,
    private val routeRepository: RouteRepository,
    private val transportMetadataRepository: TransportMetadataRepository
): LiveUseCase() {

    suspend fun invoke(
        liveInformationUrl: String,
        routeId: RouteId,
        serviceId: ServiceId,
        tripId: TripId
    ): Flow<Cachable<IRouteTripInformation>> {
        val scheduledTimetable = routeRepository.tripTimetable(routeId, serviceId, tripId)
        return liveServiceRepository.getRealtimeUpdates(liveInformationUrl).map<FeedMessage, Cachable<IRouteTripInformation>> {
            val scheduleStartOfDay = transportMetadataRepository.scheduleStartOfDay()

            val updates = it.entity
                .filterNot { it.isDeleted == true }
                .mapNotNull { it.tripUpdate }
                .filter { it.trip.tripId == tripId }
            val delay = updates.firstOrNull { it.delay != null }?.delay
            val specific = updates.firstOrNull { it.stopTimeUpdate.isNotEmpty() }
                ?.stopTimeUpdate?.associate { it.stopId to it }

            scheduledTimetable.map {
                LiveRouteTripInformation.fromOther(
                    it.trip,
                    it.trip.stops.map {
                        val s = specific?.get(it.stopId)
                        TimetableStationTime(
                            arrival = decodeTime(
                                s?.arrival,
                                delay,
                                it.arrivalTime!!,
                                scheduleStartOfDay
                            ),
                            departure = decodeTime(
                                s?.departure,
                                delay,
                                it.departureTime!!,
                                scheduleStartOfDay
                            )
                        )
                    }
                )
            }
        }.catch {
            Napier.e(it)
            emit(scheduledTimetable.map { it.trip })
        }
    }

}