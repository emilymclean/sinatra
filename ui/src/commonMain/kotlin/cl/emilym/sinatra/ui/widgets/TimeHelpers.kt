package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.repository.isSameDay
import cl.emilym.sinatra.data.repository.startOfDay
import cl.emilym.sinatra.ui.dayOfWeekDateTimeFormat
import cl.emilym.sinatra.ui.timeFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

val LocalScheduleTimeZone = staticCompositionLocalOf<TimeZone> { error("Schedule time zone not provided!") }
val LocalLocalTimeZone = staticCompositionLocalOf<TimeZone> { TimeZone.currentSystemDefault() }
val LocalClock = staticCompositionLocalOf<Clock> { Clock.System }

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
    return scheduleStartOfDay() + this
}

@Composable
fun Time.isInPast(): Boolean {
    return toTodayInstant() < LocalClock.current.now()
}

@Composable
fun Instant.format(): String {
    val tz = LocalLocalTimeZone.current
    val inTz = toLocalDateTime(tz)
    val startInTz = startOfDay(tz).toLocalDateTime(tz)

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