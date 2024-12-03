package cl.emilym.betterbuscanberra.data.models

import cl.emilym.betterbuscanberra.data.client.CANBERRA_REGION
import kotlinx.serialization.SerialName

typealias RegionName = String
typealias RouteId = String
typealias OperatorId = String
typealias StopCode = String
typealias StopName = String
typealias StopType = String
typealias ServiceId = String
typealias ServiceDirection = String
typealias POIClass = String
typealias Mode = String
typealias EpochSeconds = Long
typealias Meters = Int
typealias Latitude = Double
typealias Longitude = Double
typealias LatitudeOrLongitude = Double

data class RoutesRequest(
    val regions: List<RegionName> = listOf(CANBERRA_REGION)
)

data class RouteDetailRequest(
    val regions: List<RegionName> = listOf(CANBERRA_REGION),
    val operatorID: OperatorId,
    val routeID: RouteId
)

data class ServicesRequest(
    val regions: List<RegionName> = listOf(CANBERRA_REGION),
    val operatorID: OperatorId,
    val routeID: RouteId
)

data class NearbyRequest(
    override val lat: Latitude,
    override val lng: Longitude,
    val radius: Meters,
): Location

data class TimetableRequest(
    val region: RegionName = CANBERRA_REGION,
    val embarkationStops: List<StopCode>
)

data class RegionsResponse(
    val regions: List<Region>
)

data class RouteDetail(
    override val region: RegionName,
    override val id: RouteId,
    override val shortName: String,
    override val mode: Mode,
    override val routeName: String,
    override val routeDescription: String,
    override val operatorID: OperatorId,
    override val operatorName: String,
    val directions: List<RouteDirection>
): Route

data class RouteDirection(
    val id: Int,
    val name: ServiceDirection,
    val encodedShape: String,
    val shapeIsDetailed: String,
    val stops: List<RouteStop>
)

data class TimetableResponse(
    val embarkationStops: List<StopServiceDetails>
)

data class StopServiceDetails(
    val stopCode: StopCode,
    val services: List<StopService>
)

data class StopService(
    val startTime: EpochSeconds,
    val serviceTripID: ServiceId,
    val serviceName: String,
    val serviceDirection: ServiceDirection,
    val serviceNumber: String,
    val routeID: RouteId,
    val operatorID: OperatorId,
    val operatorName: String,
    val mode: Mode,
)

data class RouteStop(
    override val stopCode: StopCode,
    override val name: StopName,
    override val lat: Latitude,
    override val lng: Longitude
): Location, Stop

data class RouteBasic(
    override val region: RegionName,
    override val id: RouteId,
    override val shortName: String,
    override val mode: Mode,
    override val routeName: String,
    override val routeDescription: String,
    override val operatorID: OperatorId,
    override val operatorName: String
): Route

data class Region(
    val name: RegionName,
    val area: List<LatitudeOrLongitude>,
    val center: Location
)

data class SearchResultResponse(
    val query: String,
    val choices: List<SearchResultChoice>
)

data class SearchResultChoice(
    override val lat: Latitude,
    override val lng: Longitude,
    val name: String,
    val stopCode: StopCode?,
    val stopType: StopType?,
    val publicTransportMode: Mode?,
    @SerialName("class")
    val clazz: POIClass
): Location

data class StopDetail(
    override val stopCode: StopCode,
    override val name: StopName,
    override val lat: Latitude,
    override val lng: Longitude,
    val mode: Mode,
    val stopType: StopType
): Stop

data class NearbyResult(
    val groups: List<>
)

data class NearbyGroup(
    val key: String,
)