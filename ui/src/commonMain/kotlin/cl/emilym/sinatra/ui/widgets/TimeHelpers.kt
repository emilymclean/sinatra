package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.models.isSameDay
import cl.emilym.sinatra.data.models.startOfDay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.day_of_week_friday
import sinatra.ui.generated.resources.day_of_week_monday
import sinatra.ui.generated.resources.day_of_week_saturday
import sinatra.ui.generated.resources.day_of_week_sunday
import sinatra.ui.generated.resources.day_of_week_thursday
import sinatra.ui.generated.resources.day_of_week_tuesday
import sinatra.ui.generated.resources.day_of_week_wednesday
import sinatra.ui.generated.resources.time_am
import sinatra.ui.generated.resources.time_pm

val LocalScheduleTimeZone = staticCompositionLocalOf<TimeZone> { error("Schedule time zone not provided!") }
val LocalLocalTimeZone = staticCompositionLocalOf<TimeZone> { TimeZone.currentSystemDefault() }
val LocalClock = staticCompositionLocalOf<Clock> { Clock.System }

@Composable
expect fun is24HourTimeFormat(): Boolean

val timeFormat: DateTimeFormat<LocalTime>
    @Composable
    get() {
        val amMarker = stringResource(Res.string.time_am)
        val pmMarker = stringResource(Res.string.time_pm)
        return when (is24HourTimeFormat()) {
            true -> LocalTime.Format {
                hour()
                char(':')
                minute()
            }
            false -> when {
                else -> LocalTime.Format {
                    amPmHour(Padding.NONE)
                    char(':')
                    minute()
                    amPmMarker(amMarker, pmMarker)
                }
            }
        }
    }

val dayOfWeekNames: DayOfWeekNames
    @Composable
    get() {
        return DayOfWeekNames(
            listOf(
                stringResource(Res.string.day_of_week_monday),
                stringResource(Res.string.day_of_week_tuesday),
                stringResource(Res.string.day_of_week_wednesday),
                stringResource(Res.string.day_of_week_thursday),
                stringResource(Res.string.day_of_week_friday),
                stringResource(Res.string.day_of_week_saturday),
                stringResource(Res.string.day_of_week_sunday),
            )
        )
    }

val dayOfWeekDateTimeFormat: DateTimeFormat<LocalDateTime>
    @Composable
    get() {
        val dayOfWeekNames = dayOfWeekNames
        val timeFormat = timeFormat
        return LocalDateTime.Format {
            dayOfWeek(dayOfWeekNames)
            chars(", ")
            time(timeFormat)
        }
    }

@Composable
fun scheduleStartOfDay(): Instant {
    return startOfDay(LocalScheduleTimeZone.current)
}

@Composable
fun startOfDay(timeZone: TimeZone): Instant {
    val clock = LocalClock.current
    return clock.startOfDay(timeZone)
}

@Composable
fun Time.toTodayInstant(): Instant {
    return addReference(scheduleStartOfDay()).instant
}

@Composable
fun Time.isInPast(): Boolean {
    return toTodayInstant() < LocalClock.current.now()
}

@Composable
fun Instant.format(): String {
    val localTimeZone = LocalLocalTimeZone.current
    val scheduleTimeZone = LocalScheduleTimeZone.current

    val scheduleTime = format(scheduleTimeZone)
    return when {
        localTimeZone.id != scheduleTimeZone.id -> "$scheduleTime (${scheduleTimeZone.id})"
        else -> scheduleTime
    }
}

@Composable
private fun Instant.format(timeZone: TimeZone): String {
    val inTz = toLocalDateTime(timeZone)
    val startInTz = startOfDay(timeZone).toLocalDateTime(timeZone)
    val timeFormat = timeFormat

    return when {
        inTz.isSameDay(startInTz) -> inTz.format(LocalDateTime.Format {
            time(timeFormat)
        })
        else -> inTz.format(dayOfWeekDateTimeFormat)
    }
}

@Composable
fun Time.format(): String {
    return toTodayInstant().format()
}