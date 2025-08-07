package cl.emilym.sinatra.data.models

enum class SpecialFavouriteType {
    HOME, WORK
}

sealed interface Favourite {
    val id: Long

    data class Route(
        override val id: Long,
        val route: cl.emilym.sinatra.data.models.Route
    ): Favourite
    data class Stop(
        override val id: Long,
        val stop: cl.emilym.sinatra.data.models.Stop,
        val specialType: SpecialFavouriteType? = null
    ): Favourite
    data class StopOnRoute(
        override val id: Long,
        val stop: cl.emilym.sinatra.data.models.Stop,
        val route: cl.emilym.sinatra.data.models.Route,
        val heading: String?
    ): Favourite
    data class Place(
        override val id: Long,
        val place: cl.emilym.sinatra.data.models.Place,
        val specialType: SpecialFavouriteType? = null
    ): Favourite
}

val Favourite.routeId: RouteId? get() = when (this) {
    is Favourite.Route -> route.id
    is Favourite.StopOnRoute -> route.id
    else -> null
}

val Favourite.stopId: StopId? get() = when (this) {
    is Favourite.Stop -> stop.id
    is Favourite.StopOnRoute -> stop.id
    else -> null
}

val Favourite.placeId: PlaceId? get() = when (this) {
    is Favourite.Place -> place.id
    else -> null
}

val Favourite.heading: Heading? get() = when (this) {
    is Favourite.StopOnRoute -> heading
    else -> null
}

val Favourite.specialType: SpecialFavouriteType? get() = when (this) {
    is Favourite.Stop -> specialType
    is Favourite.Place -> specialType
    else -> null
}