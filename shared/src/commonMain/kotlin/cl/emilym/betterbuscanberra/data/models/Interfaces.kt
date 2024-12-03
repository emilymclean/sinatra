package cl.emilym.betterbuscanberra.data.models

interface Location {
    val lat: Latitude
    val lng: Longitude
}

interface Route {
    val region: RegionName
    val id: RouteId
    val shortName: String
    val mode: String
    val routeName: String
    val routeDescription: String
    val operatorID: OperatorId
    val operatorName: String
}

interface Stop: Location {
    val stopCode: StopCode
    val name: StopName
}