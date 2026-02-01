package cl.emilym.sinatra.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.models.isSameDay
import cl.emilym.sinatra.data.models.startOfDay
import cl.emilym.sinatra.ui.text
import cl.emilym.sinatra.ui.widgets.value
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.time_local_timezone
import sinatra.ui.generated.resources.time_minute_less_than_min_short
import sinatra.ui.generated.resources.time_minute_short
import kotlin.time.Duration.Companion.minutes

val LocalScheduleTimeZone = staticCompositionLocalOf<TimeZone> { error("Schedule time zone not provided!") }
val LocalLocalTimeZone = staticCompositionLocalOf<TimeZone> { TimeZone.currentSystemDefault() }
val LocalClock = staticCompositionLocalOf<Clock> { kotlin.time.Clock.System }

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
fun Time.isNowish(): Boolean {
    val today = toTodayInstant()
    val now = LocalClock.current.now()
    return today > (now - 1.minutes) && today < (now + 1.minutes)
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
        localTimeZone.id != scheduleTimeZone.id &&
        FeatureFlag.SPECIFY_TIMEZONE_WHEN_DIFFERENT.value() ->
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

@Composable
fun countdown(time: kotlin.time.Instant, negative: Boolean = false): String {
    val clock = LocalClock.current
    var remaining by remember { mutableStateOf((time - clock.now())) }

    LaunchedEffect(time) {
        while (isActive) {
            delay(
                remaining.inWholeMilliseconds.let {
                    it - (it.floorDiv(60000L) * 60000L)
                }.coerceAtLeast(0) + 1000L
            )
            remaining = time - clock.now()
        }
    }

    val display by derivedStateOf {
        when (negative) {
            true -> -remaining
            else -> remaining
        }
    }

    return when {
        display.isPositive() && display <= 1.minutes ->
            stringResource(Res.string.time_minute_less_than_min_short)
        display.isNegative() && display >= (-1).minutes ->
            pluralStringResource(Res.plurals.time_minute_short, 0, 0)
        else -> display.text(true)
    }
}