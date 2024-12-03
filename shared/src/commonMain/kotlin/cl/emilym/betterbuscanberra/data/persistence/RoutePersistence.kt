package cl.emilym.betterbuscanberra.data.persistence

import cl.emilym.betterbuscanberra.data.models.RouteBasic
import org.koin.core.annotation.Single

@Single
class RoutePersistence {

    var routes: List<RouteBasic>? = null

}