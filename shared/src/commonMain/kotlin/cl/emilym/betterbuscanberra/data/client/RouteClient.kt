package cl.emilym.betterbuscanberra.data.client

import cl.emilym.betterbuscanberra.data.models.OperatorId
import cl.emilym.betterbuscanberra.data.models.RouteBasic
import cl.emilym.betterbuscanberra.data.models.RouteDetail
import cl.emilym.betterbuscanberra.data.models.RouteDetailRequest
import cl.emilym.betterbuscanberra.data.models.RouteId
import cl.emilym.betterbuscanberra.data.models.RoutesRequest
import org.koin.core.annotation.Factory

@Factory
class RouteClient(
    private val tripGoApi: TripGoApi
) {

    suspend fun routes(): List<RouteBasic> {
        return tripGoApi.routes(
            RoutesRequest()
        )
    }

    suspend fun routeDetails(
        routeId: RouteId,
        operatorId: OperatorId
    ): RouteDetail {
        return tripGoApi.routeDetails(
            RouteDetailRequest(
                routeID = routeId,
                operatorID = operatorId
            )
        )
    }

}