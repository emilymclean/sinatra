package cl.emilym.sinatra.data.models

enum class CacheCategory(
    val db: String
) {

    STOP("stop"),
    STOP_TIMETABLE("stop-timetable")

}