package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.ServiceAccessibility
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopAccessibility
import cl.emilym.sinatra.data.models.StopWheelchairAccessibility
import cl.emilym.sinatra.data.models.TimetableStationTime
import cl.emilym.sinatra.data.models.merge
import cl.emilym.sinatra.ui.localization.LocalScheduleTimeZone
import cl.emilym.sinatra.ui.localization.format
import cl.emilym.sinatra.ui.localization.isInPast
import cl.emilym.sinatra.ui.localization.isSameDay
import cl.emilym.sinatra.ui.text
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.approximate_arrival
import sinatra.ui.generated.resources.approximate_arrival_day
import sinatra.ui.generated.resources.estimated_arrival
import sinatra.ui.generated.resources.estimated_arrival_day
import sinatra.ui.generated.resources.estimated_arrival_early
import sinatra.ui.generated.resources.estimated_arrival_early_day
import sinatra.ui.generated.resources.estimated_arrival_late
import sinatra.ui.generated.resources.estimated_arrival_late_day
import sinatra.ui.generated.resources.future_approximate_departure
import sinatra.ui.generated.resources.future_approximate_departure_day
import sinatra.ui.generated.resources.future_estimated_departure
import sinatra.ui.generated.resources.future_estimated_departure_day
import sinatra.ui.generated.resources.future_estimated_departure_early
import sinatra.ui.generated.resources.future_estimated_departure_early_day
import sinatra.ui.generated.resources.future_estimated_departure_late
import sinatra.ui.generated.resources.future_estimated_departure_late_day
import sinatra.ui.generated.resources.future_scheduled_departure
import sinatra.ui.generated.resources.future_scheduled_departure_day
import sinatra.ui.generated.resources.generic_arrival
import sinatra.ui.generated.resources.generic_arrival_day
import sinatra.ui.generated.resources.generic_departure
import sinatra.ui.generated.resources.generic_departure_day
import sinatra.ui.generated.resources.generic_departure_past
import sinatra.ui.generated.resources.generic_departure_past_day
import sinatra.ui.generated.resources.past_departure
import sinatra.ui.generated.resources.past_departure_approximate
import sinatra.ui.generated.resources.past_departure_approximate_day
import sinatra.ui.generated.resources.past_departure_day
import sinatra.ui.generated.resources.past_departure_early
import sinatra.ui.generated.resources.past_departure_early_day
import sinatra.ui.generated.resources.past_departure_late
import sinatra.ui.generated.resources.past_departure_late_day
import sinatra.ui.generated.resources.scheduled_arrival
import sinatra.ui.generated.resources.scheduled_arrival_day
import sinatra.ui.generated.resources.semantics_stop_listing

sealed interface StopStationTime {
    val stationTime: StationTime

    data class Arrival(
        override val stationTime: StationTime
    ): StopStationTime

    data class Departure(
        override val stationTime: StationTime
    ): StopStationTime
}

@Composable
fun TimetableStationTime.pick(
    route: Route? = null,
    isFirst: Boolean = false
): StopStationTime {
    return if (isFirst || arrival.time.isInPast()) {
        StopStationTime.Departure(route?.let { departure.merge(route) } ?: departure)
    } else {
        StopStationTime.Arrival(route?.let { arrival.merge(route) } ?: arrival)
    }
}

@Composable
fun DefaultStopCardIcon(stop: Stop) {
    BusIcon()
}

@Composable
fun IconStopCard(
    stop: Stop,
    modifier: Modifier = Modifier,
    stopStationTime: StopStationTime? = null,
    onClick: () -> Unit,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)?
) {
    val stopListingSemantics = stringResource(Res.string.semantics_stop_listing, stop.name)
    ListCard(
        icon?.let {
            { RandleScaffold { it() } }
        },
        Modifier
            .semantics {
                contentDescription = stopListingSemantics
            }
            .then(modifier),
        onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.5.rdp)
        ) {
            Text(
                stop.name,
                modifier = Modifier.weight(1f, fill = false),
            )
            // Every stop is marked as not wheelchair accessible, so there isn't any point having this :/, thanks Transport Canberra
            if (
                FeatureFlag.STOP_CARD_SHOW_ACCESSIBILITY.value() &&
                !FeatureFlag.GLOBAL_HIDE_TRANSPORT_ACCESSIBILITY.value()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.25.rdp)
                ) {
                    stop.accessibility.icons()
                }
            }
        }

        stopStationTime?.let {
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.25.rdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LivenessIcon(it)
                Text(
                    it.text,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun StopCard(
    stop: Stop,
    modifier: Modifier = Modifier,
    stopStationTime: StopStationTime? = null,
    onClick: () -> Unit,
    subtitle: String? = null,
    showStopIcon: Boolean = false,
) {
    IconStopCard(
        stop,
        modifier,
        stopStationTime,
        onClick,
        subtitle,
        when (showStopIcon) {
            true -> { { DefaultStopCardIcon(stop) } }
            else -> null
        }
    )
}

@Composable
fun LivenessIcon(
    stopStationTime: StopStationTime,
    modifier: Modifier = Modifier
) {
    if (!FeatureFlag.STOP_DETAIL_LIVENESS_ICON.value()) return
    when (stopStationTime.stationTime) {
        !is StationTime.Live -> return
        else -> {
            ClockIcon(
                Modifier.size(16.dp).then(modifier),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

val StopStationTime.text: String
    @Composable
    get() {
        val time = stationTime.time.format()
        val stationTime = stationTime
        val isInPast = stationTime.time.isInPast()
        val hasDay = !stationTime.time.isSameDay(LocalScheduleTimeZone.current)
        return when (FeatureFlag.STOP_DETAIL_CONCEAL_LIVENESS_STRING.value()) {
            true -> stringResource(when (this) {
                is StopStationTime.Arrival -> when (hasDay) {
                    true -> Res.string.generic_arrival_day
                    else -> Res.string.generic_arrival
                }
                is StopStationTime.Departure -> when (isInPast) {
                    true -> when (hasDay) {
                        true -> Res.string.generic_departure_past_day
                        else -> Res.string.generic_departure_past
                    }
                    else -> when (hasDay) {
                        true -> Res.string.generic_departure_day
                        else -> Res.string.generic_departure
                    }
                }
            }, time)
            else -> when (stationTime) {
                is StationTime.Scheduled -> stringResource(when (this) {
                    is StopStationTime.Arrival -> when (stationTime.approximate) {
                        true -> when (hasDay) {
                            true -> Res.string.approximate_arrival_day
                            else -> Res.string.approximate_arrival
                        }
                        else -> when (hasDay) {
                            true -> Res.string.scheduled_arrival_day
                            else -> Res.string.scheduled_arrival
                        }
                    }
                    is StopStationTime.Departure -> when (isInPast) {
                        true -> when(stationTime.approximate) {
                            true -> when (hasDay) {
                                true -> Res.string.past_departure_approximate_day
                                else -> Res.string.past_departure_approximate
                            }
                            else -> when (hasDay) {
                                true -> Res.string.past_departure_day
                                else -> Res.string.past_departure
                            }
                        }
                        else -> when(stationTime.approximate) {
                            true -> when (hasDay) {
                                true -> Res.string.future_approximate_departure_day
                                else -> Res.string.future_approximate_departure
                            }
                            else -> when (hasDay) {
                                true -> Res.string.future_scheduled_departure_day
                                else -> Res.string.future_scheduled_departure
                            }
                        }
                    }
                }, time)
                is StationTime.Live -> when {
                    stationTime.delay.inWholeSeconds < -60L -> stringResource(
                        when (this) {
                            is StopStationTime.Arrival -> when (hasDay) {
                                true -> Res.string.estimated_arrival_early_day
                                else -> Res.string.estimated_arrival_early
                            }
                            is StopStationTime.Departure -> when (isInPast) {
                                true -> when (hasDay) {
                                    true -> Res.string.past_departure_early_day
                                    else -> Res.string.past_departure_early
                                }
                                else -> when (hasDay) {
                                    true -> Res.string.future_estimated_departure_early_day
                                    else -> Res.string.future_estimated_departure_early
                                }
                            }
                        },
                        time,
                        (-stationTime.delay).text
                    )
                    stationTime.delay.inWholeSeconds > 60L -> stringResource(
                        when (this) {
                            is StopStationTime.Arrival -> when (hasDay) {
                                true -> Res.string.estimated_arrival_late_day
                                else -> Res.string.estimated_arrival_late
                            }
                            is StopStationTime.Departure -> when (isInPast) {
                                true -> when (hasDay) {
                                    true -> Res.string.past_departure_late_day
                                    else -> Res.string.past_departure_late
                                }
                                else -> when (hasDay) {
                                    true -> Res.string.future_estimated_departure_late_day
                                    else -> Res.string.future_estimated_departure_late
                                }
                            }
                        },
                        time,
                        stationTime.delay.text
                    )
                    else -> stringResource(
                        when (this) {
                            is StopStationTime.Arrival -> when (hasDay) {
                                true -> Res.string.estimated_arrival_day
                                else -> Res.string.estimated_arrival
                            }
                            is StopStationTime.Departure -> when (isInPast) {
                                true -> when (hasDay) {
                                    true -> Res.string.past_departure_day
                                    else -> Res.string.past_departure
                                }
                                else -> when (hasDay) {
                                    true -> Res.string.future_estimated_departure_day
                                    else -> Res.string.future_estimated_departure
                                }
                            }
                        },
                        time
                    )
                }
            }
        }
    }

@Composable
fun StopAccessibility.icons() {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
        WheelchairAccessibleIcon(wheelchair == StopWheelchairAccessibility.FULL)
    }
}

@Composable
fun ServiceAccessibility.icons() {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
        WheelchairAccessibleIcon(wheelchairAccessible == ServiceWheelchairAccessible.ACCESSIBLE)
        if (bikesAllowed == ServiceBikesAllowed.ALLOWED) {
            BikeIcon()
        }
    }
}