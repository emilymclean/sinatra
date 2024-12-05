package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.client.StopClient
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopTimetable
import cl.emilym.sinatra.data.persistence.StopPersistence
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
class StopRepository(
    private val stopsCacheWorker: StopsCacheWorker,
    private val stopClient: StopClient,
) {

    suspend fun stops(): Cachable<List<Stop>>  = stopsCacheWorker.get()

    suspend fun timetable(stopId: StopId): Cachable<StopTimetable> {
        return Cachable.live(stopClient.timetable(stopId))
    }

}