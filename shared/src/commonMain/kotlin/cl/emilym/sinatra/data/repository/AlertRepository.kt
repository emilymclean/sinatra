package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.AlertSeverity
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.TripId
import cl.emilym.sinatra.pick
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class AlertRepository(
    private val liveServiceRepository: LiveServiceRepository,
    private val routeRepository: RouteRepository
) {

    fun alerts(
        routeId: RouteId? = null,
        tripId: TripId? = null,
        stopId: StopId? = null
    ): Flow<List<Alert>> {
        return flow {
            val routeFeeds = when (routeId) {
                null -> routeRepository.routes().item.mapNotNull { it.realTimeUrl }.distinct().toTypedArray()
                else -> routeRepository.route(routeId).item?.realTimeUrl?.let { arrayOf(it) } ?: emptyArray()
            }
            if (routeFeeds.isEmpty()) return@flow emit(emptyList())

            val out = liveServiceRepository.getMultipleRealtimeUpdates(
                *routeFeeds
            ).map {
                it.asSequence().flatMap { it.entity }
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
                    .mapNotNull {
                        // Todo move this into strings or something
                        val effect = when (it.effect) {
                            is com.google.transit.realtime.Alert.Effect.ACCESSIBILITY_ISSUE -> "accessibility issue"
                            is com.google.transit.realtime.Alert.Effect.DETOUR -> "detour"
                            is com.google.transit.realtime.Alert.Effect.ADDITIONAL_SERVICE -> "additional service"
                            is com.google.transit.realtime.Alert.Effect.MODIFIED_SERVICE -> "modified service"
                            is com.google.transit.realtime.Alert.Effect.REDUCED_SERVICE -> "reduced service"
                            is com.google.transit.realtime.Alert.Effect.SIGNIFICANT_DELAYS -> "significant delays"
                            is com.google.transit.realtime.Alert.Effect.STOP_MOVED -> "stop moved"
                            else -> null
                        }

                        val cause = when (it.cause) {
                            is com.google.transit.realtime.Alert.Cause.STRIKE -> "industrial action"
                            is com.google.transit.realtime.Alert.Cause.ACCIDENT -> "accident"
                            is com.google.transit.realtime.Alert.Cause.CONSTRUCTION -> "construction"
                            is com.google.transit.realtime.Alert.Cause.DEMONSTRATION -> "demonstration"
                            is com.google.transit.realtime.Alert.Cause.HOLIDAY -> "holiday"
                            is com.google.transit.realtime.Alert.Cause.MAINTENANCE -> "maintenance"
                            is com.google.transit.realtime.Alert.Cause.MEDICAL_EMERGENCY -> "medical emergency"
                            is com.google.transit.realtime.Alert.Cause.POLICE_ACTIVITY -> "police activity"
                            is com.google.transit.realtime.Alert.Cause.TECHNICAL_PROBLEM -> "technical issues"
                            is com.google.transit.realtime.Alert.Cause.WEATHER -> "weather"
                            else -> null
                        }

                        val title = when {
                            cause != null && effect != null -> "${effect.capitalize()} due to $cause"
                            effect != null -> "${effect.capitalize()}"
                            cause != null -> "${cause.capitalize()}"
                            else -> if (it.headerText == null) {
                                return@mapNotNull null
                            } else {
                                "Alert"
                            }
                        }

                        Alert(
                            title,
                            it.headerText?.pick(),
                            AlertSeverity.fromPB(it.severityLevel),
                            it.url?.pick()?.let {
                                ContentLink.external(
                                    "Find out more",
                                    it
                                )
                            }
                        )
                    }.toList()
            }

            emitAll(out)
        }
    }

}