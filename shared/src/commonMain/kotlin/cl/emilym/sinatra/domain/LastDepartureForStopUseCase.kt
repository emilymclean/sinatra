package cl.emilym.sinatra.domain

import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.models.Heading
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.data.models.toTodayTime
import cl.emilym.sinatra.data.repository.Preference
import cl.emilym.sinatra.data.repository.PreferencesRepository
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory
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
    private val clock: Clock,
    private val preferencesRepository: PreferencesRepository
) {

    companion object {
        private val CUTOFF_TIME = 3.hours // 3am
    }

    operator fun invoke(
        stopId: StopId,
        routeId: RouteId? = null
    ): Flow<List<IStopTimetableTime>> = flow {
        val scheduleTimeZone = metadataRepository.timeZone()
        val now = clock.now()
        val days = listOf(now - 1.days, now, now + 1.days)

        val timesAndServices = servicesAndTimesForStopUseCase(stopId)
        val activeServicesByDay = days.map { day ->
            timesAndServices.item.services.filter { it.active(
                day,
                scheduleTimeZone
            ) }
        }

        if (activeServicesByDay.all { it.isEmpty() }) return@flow emit(emptyList())

        val lasts = mutableMapOf<RouteAndHeading, Array<StopTimetableTime?>>()

        activeServicesByDay.forEachIndexed { dayIndex, activeServices ->
            val startOfDay = days[dayIndex].startOfDay(scheduleTimeZone)
            activeServices.forEach { activeService ->
                val relevant = timesAndServices.item.times
                    .filter { it.serviceId == activeService.id }
                    .filterNot { it.last }
                    .run {
                        // Filter for specific routes when provided
                        when (routeId) {
                            null -> this
                            else -> filter { it.routeId == routeId }
                        }
                    }
                    .run {
                        // Only look for departures before 3am on next day
                        when (dayIndex) {
                            2 -> filter { it.departureTime.durationThroughDay < CUTOFF_TIME }
                            else -> this
                        }
                    }

                relevant.forEach { stopTime ->
                    val key = RouteAndHeading(stopTime.routeId, stopTime.heading)
                    val referenced = stopTime.withTimeReference(startOfDay)
                    val current = lasts.getOrPut(key){ Array(3) { null } }

                    if (current[dayIndex] == null || current[dayIndex]!!.departureTime < referenced.departureTime)
                        current[dayIndex] = referenced
                }
            }
        }

        emit(
            lasts
                .values
                .mapNotNull {
                    val yesterday = it[0]
                    val today = it[1]
                    val tomorrow = it[2]
                    when {
                        yesterday != null && yesterday.departureTime >= now -> yesterday
                        tomorrow != null -> tomorrow
                        else -> today
                    }
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
    }.combine(preferencesRepository.preference(Preference.ShowSchoolServices).flow) { last, school ->
        when (school) {
            true -> last
            else -> last.filter { it.route?.schoolServiceOnly == false }
        }
    }

}