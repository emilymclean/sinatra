package cl.emilym.sinatra.data.models

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

data class Service(
    val id: ServiceId,
    val regular: List<TimetableServiceRegular>,
    val exception: List<TimetableServiceException>
) {

    fun active(instant: Instant, timeZone: TimeZone, ignoreDates: Boolean = false): Boolean {
        val exceptions = exception.filter { it.relevant(instant) }
        return (regular.any { it.relevant(instant, timeZone, ignoreDates) }
                && !exceptions.any { it.type == TimetableServiceExceptionType.REMOVED })
                || exceptions.any { it.type == TimetableServiceExceptionType.ADDED }
    }

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.Service, timeZone: TimeZone): Service {
            return Service(
                pb.id,
                pb.regular.map { TimetableServiceRegular.fromPB(it, timeZone) },
                pb.exception.map { TimetableServiceException.fromPB(it, timeZone) },
            )
        }
    }

}

data class TimetableServiceRegular(
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean,
    val startDate: Instant,
    val endDate: Instant
) {

    private fun lookupDayOfWeek(d: DayOfWeek): Boolean {
        return when (d) {
            DayOfWeek.MONDAY -> monday
            DayOfWeek.TUESDAY -> tuesday
            DayOfWeek.WEDNESDAY -> wednesday
            DayOfWeek.THURSDAY -> thursday
            DayOfWeek.FRIDAY -> friday
            DayOfWeek.SATURDAY -> saturday
            DayOfWeek.SUNDAY -> sunday
            // Just in case we decide to add tenesday
            else -> sunday
        }
    }

    fun relevant(instant: Instant, timeZone: TimeZone, ignoreDates: Boolean = false): Boolean {
        return ((startDate <= instant && instant < (endDate + 1.days)) || ignoreDates) &&
                lookupDayOfWeek(instant.toLocalDateTime(timeZone).dayOfWeek)
    }

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.TimetableServiceRegular, timeZone: TimeZone): TimetableServiceRegular {
            return TimetableServiceRegular(
                pb.monday,
                pb.tuesday,
                pb.wednesday,
                pb.thursday,
                pb.friday,
                pb.saturday,
                pb.sunday,
                LocalDateTime.parse(pb.startDate).toInstant(timeZone),
                LocalDateTime.parse(pb.endDate).toInstant(timeZone)
            )
        }
    }

}

enum class TimetableServiceExceptionType {
    ADDED, REMOVED;

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.TimetableServiceExceptionType): TimetableServiceExceptionType {
            return when (pb) {
                is cl.emilym.gtfs.TimetableServiceExceptionType.ADDED -> ADDED
                is cl.emilym.gtfs.TimetableServiceExceptionType.REMOVED -> REMOVED
                else -> REMOVED
            }
        }
    }
}

data class TimetableServiceException(
    val date: Instant,
    val type: TimetableServiceExceptionType,
) {

    fun relevant(instant: Instant): Boolean {
        return instant in date..<(date + 1.days)
    }

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.TimetableServiceException, timeZone: TimeZone): TimetableServiceException {
            return TimetableServiceException(
                LocalDateTime.parse(pb.date).toInstant(timeZone),
                TimetableServiceExceptionType.fromPB(pb.type)
            )
        }
    }

}
