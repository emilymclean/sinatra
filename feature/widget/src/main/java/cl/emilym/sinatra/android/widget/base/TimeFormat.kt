package cl.emilym.sinatra.android.widget.base

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

fun Instant.widgetFormat(): String {
    val tz = TimeZone.currentSystemDefault()
    val time = toLocalDateTime(tz)
    val timeFormat = timeFormat

    return time.format(LocalDateTime.Format {
        time(timeFormat)
    })
}

val timeFormat: DateTimeFormat<LocalTime> = LocalTime.Format {
    amPmHour(Padding.NONE)
    char(':')
    minute()
    amPmMarker("am", "pm")
}