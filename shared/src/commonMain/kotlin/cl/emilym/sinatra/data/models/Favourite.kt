package cl.emilym.sinatra.data.models

sealed interface Favourite {
    data class Route(
        val route: cl.emilym.sinatra.data.models.Route
    ): Favourite
    data class Stop(
        val stop: cl.emilym.sinatra.data.models.Stop
    ): Favourite
    data class StopOnRoute(
        val stop: cl.emilym.sinatra.data.models.Stop,
        val route: cl.emilym.sinatra.data.models.Route
    ): Favourite
}