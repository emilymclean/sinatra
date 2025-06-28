package cl.emilym.sinatra.mapping

import cl.emilym.gtfs.Location
import cl.emilym.sinatra.data.models.MapLocation
import tech.mappie.api.ObjectMappie

object GtfsLocationToMapLocation: ObjectMappie<cl.emilym.gtfs.Location, MapLocation>() {
    override fun map(from: Location): MapLocation = mapping {  }
}