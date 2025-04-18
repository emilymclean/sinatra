package cl.emilym.sinatra.android.widget.data.models

import cl.emilym.sinatra.data.models.Heading
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId

data class UpcomingVehiclesWidgetConfiguration(
    val appWidgetId: Int,
    val stopId: StopId,
    val routeId: RouteId?,
    val heading: Heading?
)