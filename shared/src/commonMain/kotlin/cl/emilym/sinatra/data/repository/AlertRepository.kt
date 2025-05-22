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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

sealed interface AlertDisplayContext {
    data class Page(
        val pageId: String
    ): AlertDisplayContext
    data class Route(
        val routeId: RouteId,
        val tripId: TripId?
    ): AlertDisplayContext
    data class Stop(
        val stopId: StopId
    ): AlertDisplayContext
}

@Factory
class AlertRepository(
    private val contentRepository: ContentRepository,
) {

    companion object {
        const val GTFS_ALERTS_ENABLED = "gtfs_alerts"
    }

    fun alerts(
        context: AlertDisplayContext
    ): Flow<List<Alert>> {
        return when (context) {
            is AlertDisplayContext.Page -> {
                val id = when (context.pageId) {
                    ContentRepository.NATIVE_BROWSE_ID -> ContentRepository.HOME_BANNER_ID
                    else -> context.pageId
                }

                flow {
                    emit(listOfNotNull(contentRepository.banner(
                        id
                    )))
                }
            }
            else -> flowOf(emptyList())
        }
    }

    @Deprecated("Use new AlertDisplayContext")
    fun alerts(
        routeId: RouteId? = null,
        tripId: TripId? = null,
        stopId: StopId? = null
    ): Flow<List<Alert>> {
        return alerts(
            when {
                routeId != null -> AlertDisplayContext.Route(
                    routeId,
                    tripId
                )
                stopId != null -> AlertDisplayContext.Stop(
                    stopId
                )
                else -> AlertDisplayContext.Page(
                    ContentRepository.HOME_BANNER_ID
                )
            }
        )
    }

}