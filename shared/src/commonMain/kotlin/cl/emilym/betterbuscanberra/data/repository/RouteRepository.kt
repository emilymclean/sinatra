package cl.emilym.betterbuscanberra.data.repository

import cl.emilym.betterbuscanberra.data.client.RouteClient
import cl.emilym.betterbuscanberra.data.models.OperatorId
import cl.emilym.betterbuscanberra.data.models.Route
import cl.emilym.betterbuscanberra.data.models.RouteDetail
import cl.emilym.betterbuscanberra.data.models.RouteId
import cl.emilym.betterbuscanberra.data.persistence.RoutePersistence
import org.koin.core.annotation.Factory

@Factory
class RouteRepository(
    private val routePersistence: RoutePersistence,
    private val routeClient: RouteClient
) {

    suspend fun routes(): List<Route> {
        return routePersistence.routes ?: routeClient.routes().also {
            routePersistence.routes = it
        }
    }

    suspend fun routeDetails(
        routeId: RouteId,
        operatorId: OperatorId? = null
    ): RouteDetail {
        val resolvedOperatorId = operatorId ?: routes().first { it.id == routeId }.operatorID
        return routeClient.routeDetails(routeId, resolvedOperatorId)
    }

}