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
import kotlin.math.abs
import kotlin.time.Duration
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
fun rememberCountdown(instant: kotlin.time.Instant): Duration {
    val clock = LocalClock.current
    var countdown by remember(instant, clock) { mutableStateOf(instant - clock.now()) }

    LaunchedEffect(instant, clock) {
        while (isActive) {
            delay(
                countdown.inWholeMilliseconds.let {
                    when (abs(it) > 60000) {
                        true -> it - (it.floorDiv(60000L) * 60000L)
                        else -> 0L
                    }
                }.coerceAtLeast(0) + 1000L
            )
            countdown = instant - clock.now()
        }
    }

    return countdown
}

@Composable
fun Time.toTodayInstant(): Instant {
    val scheduleStartOfDay = scheduleStartOfDay()
    return remember(scheduleStartOfDay, this) {
        addReference(scheduleStartOfDay).instant
    }
}

@Composable
fun Time.isInPast(): Boolean {
    val instant = toTodayInstant()
    val clock = LocalClock.current
    val countdown = rememberCountdown(instant)
    return remember(countdown, clock, instant) {
        instant < clock.now()
    }
}

@Composable
fun Time.isNowish(): Boolean {
    val instant = toTodayInstant()
    val clock = LocalClock.current
    val countdown = rememberCountdown(instant)
    return remember(countdown, clock, instant) {
        val now = clock.now()
        instant > (now - 1.minutes) && instant < (now + 1.minutes)
    }
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
    val clock = LocalClock.current
    return remember(this, timeZone, clock) {
        toLocalDateTime(timeZone).isSameDay(clock.now().toLocalDateTime(timeZone))
    }
}

@Composable
private fun Instant.format(timeZone: TimeZone): String {
    val inTz = remember(this, timeZone) { toLocalDateTime(timeZone) }
    val sameDay = isSameDay(timeZone)
    val timeFormat = timeFormat
    val dayOfWeekFormat = dayOfWeekDateTimeFormat

    return remember(inTz, this, sameDay, timeZone, timeFormat) {
        when {
            sameDay -> inTz.format(LocalDateTime.Format {
                time(timeFormat)
            })
            else -> inTz.format(dayOfWeekFormat)
        }
    }
}

@Composable
fun Time.format(): String {
    return toTodayInstant().format()
}

@Composable
fun countdown(time: kotlin.time.Instant, negative: Boolean = false): String {
    val remaining = rememberCountdown(time)

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