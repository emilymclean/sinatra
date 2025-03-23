package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.models.CacheCategory
import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.ShaDigest
import cl.emilym.sinatra.room.dao.ShaDao
import cl.emilym.sinatra.room.entities.ShaEntity
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

internal val DEFAULT_EXPIRE_TIME = 24.hours

sealed interface CacheInformation {
    data object Unavailable: CacheInformation
    data class Available(
        val digest: ShaDigest,
        val added: Instant
    ): CacheInformation {

        fun expired(expireTime: Duration, now: Instant): Boolean {
            return (added + expireTime) < now
        }

    }
}

data class CachedResource(
    val resource: ResourceKey,
    val added: Instant
)

@Factory
class ShaRepository(
    private val shaDao: ShaDao,
    private val clock: Clock
) {

    suspend fun save(digest: ShaDigest, category: CacheCategory, resource: ResourceKey) {
        shaDao.shaByTypeAndResource(category.db, resource)?.let { shaDao.delete(it) }
        shaDao.save(ShaEntity(
            0,
            digest,
            category.db,
            resource,
            clock.now().toEpochMilliseconds(),
        ))
    }

    suspend fun cached(category: CacheCategory, resource: ResourceKey): CacheInformation {
        return shaDao.shaByTypeAndResource(category.db, resource).also {
            Napier.d("State of sha = ${it} (category = $category, resource = $resource)")
        }?.let {
            CacheInformation.Available(it.sha, Instant.fromEpochMilliseconds(it.added))
        } ?: CacheInformation.Unavailable
    }

    suspend fun cached(category: CacheCategory): List<CachedResource> {
        return shaDao.shaByType(category.db).map {
            CachedResource(
                it.resource,
                Instant.fromEpochMilliseconds(it.added)
            )
        }
    }

    suspend fun remove(category: CacheCategory, resource: ResourceKey) {
        shaDao.deleteByTypeAndResource(category.db, resource)
    }

    suspend fun invalidateAll() {
        shaDao.deleteAll()
    }

}