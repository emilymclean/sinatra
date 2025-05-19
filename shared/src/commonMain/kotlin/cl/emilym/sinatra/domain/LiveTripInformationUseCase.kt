package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.DelayInformation
import cl.emilym.sinatra.data.models.IRouteTripInformation
import cl.emilym.sinatra.data.models.LiveRouteTripInformation
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteRealtimeInformation
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.models.TimetableStationTime
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.data.models.isSameDay
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.models.toTime
import cl.emilym.sinatra.data.repository.LiveServiceRepository
import cl.emilym.sinatra.data.repository.RouteRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.e
import com.google.transit.realtime.FeedMessage
import com.google.transit.realtime.TripUpdate
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.seconds

abstract class LiveUseCase {

    protected fun decodeTime(
        specific: TripUpdate.StopTimeEvent?,
        delay: DelayInformation,
        expected: Time,
        scheduleStartOfDay: Instant,
        scheduleTimeZone: TimeZone
    ): StationTime {
        val delay = delay.let {
            when (it) {
                is DelayInformation.Unknown -> null
                is DelayInformation.Fixed -> it.delay
            }
        }
        if (delay == null) return StationTime.Scheduled(expected)

        return StationTime.Live(
            expected + delay,
            delay
        )
    }

}

@Factory
class LiveTripInformationUseCase(
    private val liveServiceRepository: LiveServiceRepository,
    private val routeRepository: RouteRepository,
    private val metadataRepository: TransportMetadataRepository
): LiveUseCase() {

    suspend operator fun invoke(
        routeId: RouteId,
        serviceId: ServiceId,
        tripId: TripId,
        startOfDay: Instant
    ): Flow<Cachable<IRouteTripInformation>> {
        val scheduleTimeZone = metadataRepository.timeZone()
        val scheduledTimetable = routeRepository.tripTimetable(routeId, serviceId, tripId, startOfDay)
        return liveServiceRepository.getRouteRealtimeUpdates(routeId).map<RouteRealtimeInformation, Cachable<IRouteTripInformation>> { realtime ->
            val tripRealtime = realtime.updates.firstOrNull { it.tripId == tripId }
            scheduledTimetable.map { scheduledTimetable ->
                LiveRouteTripInformation.fromOther(
                    scheduledTimetable.trip,
                    scheduledTimetable.trip.stops.map {
                        TimetableStationTime(
                            arrival = decodeTime(
                                null,
                                tripRealtime?.delay ?: DelayInformation.Unknown,
                                it.arrivalTime!!,
                                startOfDay,
                                scheduleTimeZone
                            ),
                            departure = decodeTime(
                                null,
                                tripRealtime?.delay ?: DelayInformation.Unknown,
                                it.departureTime!!,
                                startOfDay,
                                scheduleTimeZone
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