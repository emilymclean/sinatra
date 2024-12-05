package cl.emilym.betterbuscanberra.data.models

data class Route(
    val id: RouteId,
    val code: RouteCode,
    val displayCode: String,
    val colors: ColorPair?,
    val name: String,
    val realTimeUrl: String?,
    val type: RouteType
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.Route): Route {
            return Route(
                pb.id,
                pb.code,
                pb.displayCode ?: pb.code,
                pb.colors?.let { ColorPair.fromPB(it) },
                pb.name,
                pb.realTimeUrl,
                RouteType.fromPB(pb.type)
            )
        }
    }

}

enum class RouteType {
    LIGHT_RAIL, BUS, OTHER;

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.RouteType): RouteType {
            return when(pb) {
                is cl.emilym.gtfs.RouteType.BUS -> BUS
                is cl.emilym.gtfs.RouteType.TRAM -> LIGHT_RAIL
                else -> OTHER
            }
        }
    }
}
