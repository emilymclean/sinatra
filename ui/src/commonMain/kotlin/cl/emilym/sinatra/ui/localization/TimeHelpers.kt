package cl.emilym.sinatra.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.models.isSameDay
import cl.emilym.sinatra.data.models.startOfDay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.time_local_timezone

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
    return addReference(scheduleStartOfDay()).instant
}

@Composable
fun Time.isInPast(): Boolean {
    return toTodayInstant() < LocalClock.current.now()
}

@Composable
fun Time.isSameDay(timeZone: TimeZone = LocalLocalTimeZone.current): Boolean {
    return toTodayInstant().isSameDay(timeZone)
}

@Composable
fun Instant.format(): String {
    val localTimeZone = LocalLocalTimeZone.current
    val scheduleTimeZone = LocalScheduleTimeZone.current

    val scheduleTime = format(scheduleTimeZone)
    return when {
        localTimeZone.id != scheduleTimeZone.id && FeatureFlags.SPECIFY_TIMEZONE_WHEN_DIFFERENT ->
            stringResource(Res.string.time_local_timezone, scheduleTime)
        else -> scheduleTime
    }
}

@Composable
fun Instant.isSameDay(timeZone: TimeZone): Boolean {
    return toLocalDateTime(timeZone).isSameDay(LocalClock.current.now().toLocalDateTime(timeZone))
}

@Composable
private fun Instant.format(timeZone: TimeZone): String {
    val inTz = toLocalDateTime(timeZone)
    val timeFormat = timeFormat

    return when {
        isSameDay(timeZone) -> inTz.format(LocalDateTime.Format {
            time(timeFormat)
        })
        else -> inTz.format(dayOfWeekDateTimeFormat)
    }
}

@Composable
fun Time.format(): String {
    return toTodayInstant().format()
}