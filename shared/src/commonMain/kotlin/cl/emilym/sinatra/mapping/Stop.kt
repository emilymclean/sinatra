package cl.emilym.sinatra.mapping

import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopAccessibility
import cl.emilym.sinatra.data.models.StopVisibility
import tech.mappie.api.ObjectMappie

object GtfsStopToStop: ObjectMappie<cl.emilym.gtfs.Stop, Stop>() {
    override fun map(from: cl.emilym.gtfs.Stop) = mapping {  }
}

object GtfsStopAccessibilityToStopAccessibility: ObjectMappie<cl.emilym.gtfs.StopAccessibility, StopAccessibility>() {
    override fun map(from: cl.emilym.gtfs.StopAccessibility) = mapping {  }
}

object GtfsStopVisibilityToStopVisibility: ObjectMappie<cl.emilym.gtfs.StopVisibility, StopVisibility>() {
    override fun map(from: cl.emilym.gtfs.StopVisibility) = mapping {

    }
}