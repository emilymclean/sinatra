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

    suspend fun startOfToday(): Instant {
        val timeZone = timeZone()
        val current = clock.now().toLocalDateTime(timeZone)
        val start = LocalDateTime(current.year, current.month, current.dayOfMonth, 0, 0, 0, 0)
        return start.toInstant(timeZone)
    }

}