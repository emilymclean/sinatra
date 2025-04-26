package cl.emilym.sinatra.data.models

enum class StopSpecialType {
    HOME, WORK
}

sealed interface Favourite {
    data class Route(
        val route: cl.emilym.sinatra.data.models.Route
    ): Favourite
    data class Stop(
        val stop: cl.emilym.sinatra.data.models.Stop,
        val specialType: StopSpecialType? = null
    ): Favourite
    data class StopOnRoute(
        val stop: cl.emilym.sinatra.data.models.Stop,
        val route: cl.emilym.sinatra.data.models.Route,
        val heading: String?
    ): Favourite
    data class Place(
        val place: cl.emilym.sinatra.data.models.Place
    ): Favourite
}