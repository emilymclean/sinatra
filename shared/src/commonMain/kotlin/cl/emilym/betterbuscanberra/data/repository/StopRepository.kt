package cl.emilym.betterbuscanberra.data.repository

import cl.emilym.betterbuscanberra.data.client.StopClient
import cl.emilym.betterbuscanberra.data.models.Stop
import org.koin.core.annotation.Factory

@Factory
class StopRepository(
    private val stopClient: StopClient
) {

    suspend fun stops(): List<Stop> {
        return stopClient.stops()
    }

}