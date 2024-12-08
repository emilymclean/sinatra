package cl.emilym.sinatra.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Factory

@Factory
class TransportMetadataRepository(
    private val clock: Clock
) {

    suspend fun timeZone(): TimeZone {
        return TimeZone.of("Australia/Sydney")
    }

    suspend fun scheduleStartOfDay(): Instant {
        val timeZone = timeZone()
        return clock.startOfDay(timeZone)
    }

}

fun Clock.startOfDay(timeZone: TimeZone): Instant {
    return now().startOfDay(timeZone)
}

fun Instant.startOfDay(timeZone: TimeZone): Instant {
    val inTz = toLocalDateTime(timeZone)

    return LocalDateTime(inTz.year, inTz.month, inTz.dayOfMonth, 0, 0, 0, 0).toInstant(timeZone)
}

fun LocalDateTime.isSameDay(other: LocalDateTime): Boolean {
    return year == other.year && month == other.month && dayOfMonth == other.dayOfMonth
}