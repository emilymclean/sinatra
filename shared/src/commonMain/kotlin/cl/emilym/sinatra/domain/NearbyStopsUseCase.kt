package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.repository.StopRepository
import cl.emilym.sinatra.nullIfEmpty
import org.koin.core.annotation.Factory

const val NEAREST_STOP_RADIUS = 1.0
const val NEARBY_STOPS_LIMIT = 5

@Factory
class NearbyStopsUseCase(
    private val stopRepository: StopRepository
) {

    suspend operator fun invoke(
        location: MapLocation,
        radius: Double = NEAREST_STOP_RADIUS,
        limit: Int = NEARBY_STOPS_LIMIT
    ): List<StopWithDistance> {
        val stops = stopRepository.stops().item
        return stops.filter(location, radius, limit)
    }

    fun List<Stop>.filter(
        location: MapLocation,
        radius: Double = NEAREST_STOP_RADIUS,
        limit: Int = NEARBY_STOPS_LIMIT
    ): List<StopWithDistance> {
        return map { StopWithDistance(it, distance(location, it.location)) }
            .filter { it.distance < radius && it.stop.parentStation == null }
            .sortedBy { it.distance }
            .take(limit)
    }

}