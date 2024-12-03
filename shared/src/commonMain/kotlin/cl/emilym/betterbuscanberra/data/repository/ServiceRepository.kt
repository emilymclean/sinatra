package cl.emilym.betterbuscanberra.data.repository

import cl.emilym.betterbuscanberra.data.client.ServiceClient
import cl.emilym.betterbuscanberra.data.models.StopCode
import cl.emilym.betterbuscanberra.data.models.StopService
import org.koin.core.annotation.Factory

@Factory
class ServiceRepository(
    private val serviceClient: ServiceClient
) {

    suspend fun timetable(stop: StopCode): List<StopService> {
        return serviceClient.timetable(stop)
    }

}