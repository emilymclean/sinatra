package cl.emilym.sinatra.data.repository

import kotlinx.datetime.TimeZone
import org.koin.core.annotation.Factory

@Factory
class TransportMetadataRepository {

    suspend fun timeZone(): TimeZone {
        return TimeZone.of("Australia/Sydney")
    }

}