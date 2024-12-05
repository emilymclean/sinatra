package cl.emilym.sinatra.data.models

enum class CacheCategory(
    val db: String
) {

    STOP("stop"),
    STOP_TIMETABLE("stop-timetable"),
    ROUTE("route"),
    SERVICE("service"),
    ROUTE_SERVICE("route_service"),
    ROUTE_SERVICE_TIMETABLE("route_service_timetable")

}