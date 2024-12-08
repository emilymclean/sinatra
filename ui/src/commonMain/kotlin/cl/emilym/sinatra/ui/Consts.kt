package cl.emilym.sinatra.ui

import cl.emilym.sinatra.data.models.Location
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

val canberra = Location(
    -35.2802, 149.1310
)

val timeFormat = LocalTime.Format {
    amPmHour(Padding.NONE)
    char(':')
    minute()
    char(' ')
    amPmMarker("am", "pm")
}

val dayOfWeekDateTimeFormat = LocalDateTime.Format {
    dayOfWeek(DayOfWeekNames.ENGLISH_FULL)
    chars(", ")
    time(timeFormat)
}