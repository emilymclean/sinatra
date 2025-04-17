package cl.emilym.sinatra.data.models

enum class CacheCategory(
    val db: String
) {

    STOP("stop"),
    STOP_TIMETABLE("stop-timetable"),
    ROUTE("route"),
    SERVICE("service"),
    ROUTE_SERVICE("route_service"),
    ROUTE_HEADING("route_heading"),
    ROUTE_SERVICE_TIMETABLE("route_service_timetable"),
    ROUTE_SERVICE_CANONICAL_TIMETABLE("route_service_CANONICAL_timetable"),
    ROUTE_TRIP_TIMETABLE("route_trip_timetable"),
    NETWORK_GRAPH("network_graph")

}