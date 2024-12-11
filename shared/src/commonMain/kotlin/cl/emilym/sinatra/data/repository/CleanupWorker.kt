package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

abstract class CleanupWorker {
    abstract val shaRepository: ShaRepository
    abstract val clock: Clock

    abstract val cacheCategory: CacheCategory
    open val deleteTime: Duration = 3.days

    abstract suspend fun delete(resource: ResourceKey)

    suspend operator fun invoke() {
        val now = clock.now()
        val items = shaRepository.cached(cacheCategory).filter {
            (it.added + deleteTime) < now
        }.map { it.resource }
        for (item in items) {
            delete(item)
            shaRepository.remove(cacheCategory, item)
        }
    }
}