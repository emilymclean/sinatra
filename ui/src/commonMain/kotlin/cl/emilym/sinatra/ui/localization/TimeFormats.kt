package cl.emilym.sinatra.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import cl.emilym.sinatra.data.models.Time24HSetting
import cl.emilym.sinatra.ui.LanguageConsts
import cl.emilym.sinatra.ui.widgets.override24HTimeSetting
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
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

@Composable
fun is24HourTimeFormat(): Boolean {
    val override = override24HTimeSetting()
    return when (override) {
        Time24HSetting.OVERRIDE_12 -> false
        Time24HSetting.OVERRIDE_24 -> true
        Time24HSetting.AUTOMATIC -> is24HourTimeFormatInternal()
    }
}

@Composable
internal expect fun is24HourTimeFormatInternal(): Boolean

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

val dayOfWeekDateTimeFormat: DateTimeFormat<LocalDateTime>
    @Composable
    get() {
        val dayOfWeekNames = dayOfWeekNames
        val timeFormat = timeFormat
        return when(Locale.current.toLanguageTag()) {
            LanguageConsts.MAINLAND_CHINESE_BCP -> LocalDateTime.Format {
                dayOfWeek(dayOfWeekNames)
                time(timeFormat)
            }
            else -> LocalDateTime.Format {
                dayOfWeek(dayOfWeekNames)
                chars(", ")
                time(timeFormat)
            }
        }
    }

val dateFormat: DateTimeFormat<LocalDate>
    @Composable
    get() {
        val locale = Locale.current
        return when(locale.toLanguageTag()) {
            LanguageConsts.MAINLAND_CHINESE_BCP -> LocalDate.Format {
                year()
                char('年')
                monthNumber()
                char('月')
                dayOfMonth()
                char('日')
            }
            LanguageConsts.US_BCP -> LocalDate.Format {
                monthNumber()
                char('/')
                dayOfMonth()
                char('/')
                year()
            }
            else -> LocalDate.Format {
                dayOfMonth()
                char('/')
                monthNumber()
                char('/')
                year()
            }
        }
    }