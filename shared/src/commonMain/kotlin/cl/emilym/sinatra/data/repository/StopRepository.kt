package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.StopClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetable
import cl.emilym.sinatra.data.models.flatMap
import cl.emilym.sinatra.data.models.map
import cl.emilym.sinatra.data.persistence.StopPersistence
import cl.emilym.sinatra.data.persistence.StopTimetablePersistence
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory

@Factory
class StopsCacheWorker(
    private val stopPersistence: StopPersistence,
    private val stopClient: StopClient,
    override val shaRepository: ShaRepository,
    override val clock: Clock,
): CacheWorker<List<Stop>>() {
    override val cacheCategory: CacheCategory = CacheCategory.STOP

    override suspend fun saveToPersistence(data: List<Stop>, resource: ResourceKey) = stopPersistence.save(data)
    override suspend fun getFromPersistence(resource: ResourceKey): List<Stop> = stopPersistence.get()

    suspend fun get(): Cachable<List<Stop>> {
        return run(stopClient.stopsEndpointPair, "stops")
    }
}

@Factory
class StopTimetableCacheWorker(
    private val stopTimetablePersistence: StopTimetablePersistence,
    private val stopClient: StopClient,
    override val shaRepository: ShaRepository,
    override val clock: Clock,
): CacheWorker<StopTimetable>() {
    override val cacheCategory: CacheCategory = CacheCategory.STOP_TIMETABLE

    override suspend fun saveToPersistence(data: StopTimetable, resource: ResourceKey) {
        stopTimetablePersistence.save(data, resource)
    }
    override suspend fun getFromPersistence(resource: ResourceKey) =
        stopTimetablePersistence.get(resource)

    suspend fun get(stopId: StopId): Cachable<StopTimetable> {
        return run(stopClient.timetableEndpointPair(stopId), "stop/${stopId}/timetable")
    }
}

@Factory
class StopRepository(
    private val routesCacheWorker: RoutesCacheWorker,
    private val stopsCacheWorker: StopsCacheWorker,
    private val stopTimetableCacheWorker: StopTimetableCacheWorker,
    private val stopPersistence: StopPersistence
) {

    suspend fun stops() = stopsCacheWorker.get()
    suspend fun stop(stopId: StopId): Cachable<Stop?> {
        val all = stopsCacheWorker.get()
        return all.map { stopPersistence.get(stopId) }
    }
    suspend fun timetable(stopId: StopId): Cachable<StopTimetable> {
        val routes = routesCacheWorker.get()
        return routes.flatMap { stopTimetableCacheWorker.get(stopId) }
    }

}