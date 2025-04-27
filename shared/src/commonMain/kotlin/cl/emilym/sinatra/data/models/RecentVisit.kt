package cl.emilym.sinatra.data.models

enum class RecentVisitType {
    ROUTE, STOP, PLACE;

    companion object {
        fun fromRecentVisit(recentVisit: RecentVisit): RecentVisitType {
            return when (recentVisit) {
                is RecentVisit.Route -> ROUTE
                is RecentVisit.Stop -> STOP
                is RecentVisit.Place -> PLACE
            }
        }
    }
}

sealed interface RecentVisit {
    data class Route(
        val route: cl.emilym.sinatra.data.models.Route
    ): RecentVisit
    data class Stop(
        val stop: cl.emilym.sinatra.data.models.Stop
    ): RecentVisit
    data class Place(
        val place: cl.emilym.sinatra.data.models.Place
    ): RecentVisit
}