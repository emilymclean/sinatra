package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.models.startOfDay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
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