package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.AlertSeverity
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.TranslatedStringLocalizableString
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class AlertRepository(
    private val liveServiceRepository: LiveServiceRepository,
    private val routeRepository: RouteRepository,
    private val contentRepository: ContentRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
) {

    companion object {
        const val GTFS_ALERTS_ENABLED = "gtfs_alerts"
    }

    fun alerts(
        routeId: RouteId? = null,
        tripId: TripId? = null,
        stopId: StopId? = null
    ): Flow<List<Alert>> {
        return flow {
            val banner = listOfNotNull(when {
                routeId == null && tripId == null && stopId == null -> try {
                    contentRepository.banner(
                        ContentRepository.HOME_BANNER_ID
                    )
                } catch (e: Exception) {
                    Napier.e(e)
                    null
                }
                else -> null
            })

            val routeFeeds = when {
                !remoteConfigRepository.feature(GTFS_ALERTS_ENABLED, false) -> emptyArray()
                routeId == null -> routeRepository.routes().item.mapNotNull { it.realTimeUrl }.distinct().toTypedArray()
                else -> routeRepository.route(routeId).item?.realTimeUrl?.let { arrayOf(it) } ?: emptyArray()
            }
            if (routeFeeds.isEmpty()) return@flow emit(banner)

            val out = liveServiceRepository.getMultipleRealtimeUpdates(
                *routeFeeds
            ).map {
                banner + it.asSequence().flatMap { it.entity }
                    .filter { it.isDeleted != true && it.alert != null }
                    .map { it.alert!! }
                    .filter {
                        when {
                            tripId != null -> it.informedEntity.any { it.trip?.tripId == tripId }
                            routeId != null || stopId != null ->
                                it.informedEntity.any {
                                    (it.routeId == routeId && routeId != null) ||
                                            (it.stopId == stopId && stopId != null)
                                }
                            else -> it.informedEntity.any { it.agencyId != null && it.stopId == null && it.trip == null && it.routeId == null }
                        }
                    }
                    .map {
                        Alert.Realtime(
                            it.effect,
                            it.cause,
                            it.headerText?.let { TranslatedStringLocalizableString(it) },
                            it.url?.let { TranslatedStringLocalizableString(it) },
                            AlertSeverity.fromPB(it.severityLevel)
                        )
                    }.toList()
            }

            emitAll(out)
        }
    }

}