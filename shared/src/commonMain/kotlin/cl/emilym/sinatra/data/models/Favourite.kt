package cl.emilym.sinatra.data.models

sealed interface Favourite {
    data class FavouriteRoute(
        val route: Route
    ): Favourite
    data class FavouriteStop(
        val stop: Stop
    ): Favourite
    data class FavouriteStopOnRoute(
        val stop: Stop,
        val route: Route
    ): Favourite
}