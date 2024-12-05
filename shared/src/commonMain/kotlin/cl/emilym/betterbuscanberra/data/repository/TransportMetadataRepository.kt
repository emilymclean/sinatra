package cl.emilym.betterbuscanberra.data.repository

import kotlinx.datetime.TimeZone
import org.koin.core.annotation.Factory

@Factory
class TransportMetadataRepository {

    suspend fun timeZone(): TimeZone {
        return TimeZone.of("Australia/Sydney")
    }

}