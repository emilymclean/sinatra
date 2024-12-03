package cl.emilym.betterbuscanberra.data.repository

import cl.emilym.betterbuscanberra.data.client.RouteClient
import cl.emilym.betterbuscanberra.data.client.StopClient
import cl.emilym.betterbuscanberra.data.models.OperatorId
import cl.emilym.betterbuscanberra.data.models.Route
import cl.emilym.betterbuscanberra.data.models.RouteDetail
import cl.emilym.betterbuscanberra.data.models.RouteId
import cl.emilym.betterbuscanberra.data.models.StopCode
import cl.emilym.betterbuscanberra.data.models.StopDetail
import cl.emilym.betterbuscanberra.data.persistence.RoutePersistence
import org.koin.core.annotation.Factory

@Factory
class StopRepository(
    private val stopClient: StopClient
) {

    suspend fun findStop(nameOrId: String): List<StopDetail> {
        return stopClient.findStop(nameOrId)
    }

    suspend fun stopById(id: StopCode): StopDetail {
        return stopClient.findStop(id).first { it.stopCode == id }
    }

}