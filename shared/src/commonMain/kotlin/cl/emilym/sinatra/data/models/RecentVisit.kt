package cl.emilym.sinatra.data.models

sealed interface RecentVisit {
    data class Route(
        val route: cl.emilym.sinatra.data.models.Route
    ): RecentVisit
    data class Stop(
        val stop: cl.emilym.sinatra.data.models.Stop
    ): RecentVisit
}