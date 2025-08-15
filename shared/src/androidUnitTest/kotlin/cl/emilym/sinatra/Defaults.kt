package cl.emilym.sinatra

import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteType
import cl.emilym.sinatra.data.models.RouteVisibility
import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopAccessibility
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.StopVisibility
import cl.emilym.sinatra.data.models.StopWheelchairAccessibility
import cl.emilym.sinatra.data.models.TimetableServiceRegular
import cl.emilym.sinatra.data.models.toTime
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

val DefaultRoute get() = Route(
    "r1",
    "R1",
    "R1",
    null,
    "Route 1",
    null,
    false,
    false,
    RouteType.LIGHT_RAIL,
    null,
    RouteVisibility(
        false,
        null,
        true
    ),
    false,
    null,
    false,
)

val DefaultTimetableServiceRegular get() = TimetableServiceRegular(
    true,
    true,
    true,
    true,
    true,
    true,
    true,
    Instant.DISTANT_PAST,
    Instant.DISTANT_FUTURE
)

val DefaultService get() = Service(
    "test-service",
    listOf(DefaultTimetableServiceRegular),
    emptyList()
)

val DefaultStopTimetableTime get() = StopTimetableTime(
    null,
    DefaultRoute.id,
    DefaultRoute.code,
    DefaultService.id,
    "trip-1",
    Instant.DISTANT_FUTURE.toTime(Instant.DISTANT_FUTURE),
    Instant.DISTANT_FUTURE.toTime(Instant.DISTANT_FUTURE),
    "somewhere",
    0,
    false,
    DefaultRoute,
    null,
)

val DefaultStopAccessibility = StopAccessibility(
    wheelchair = StopWheelchairAccessibility.FULL
)

val DefaultStopVisibility = StopVisibility(
    visibleZoomedOut = false,
    visibleZoomedIn = true,
    showChildren = false,
    searchWeight = null
)

val DefaultStop get() = Stop(
    id = "default-stop",
    parentStation = null,
    name = "Default Stop",
    _simpleName = "Default Stop",
    location = MapLocation(100.0, 100.0),
    accessibility = DefaultStopAccessibility,
    visibility = DefaultStopVisibility,
    hasRealtime = true,
    schoolServiceOnly = false
)