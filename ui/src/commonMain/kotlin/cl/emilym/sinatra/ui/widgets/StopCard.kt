package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.ServiceAccessibility
import cl.emilym.sinatra.data.models.ServiceBikesAllowed
import cl.emilym.sinatra.data.models.ServiceWheelchairAccessible
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopAccessibility
import cl.emilym.sinatra.data.models.StopWheelchairAccessibility
import cl.emilym.sinatra.data.models.TimetableStationTime
import cl.emilym.sinatra.ui.text
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.estimated_arrival
import sinatra.ui.generated.resources.estimated_arrival_early
import sinatra.ui.generated.resources.estimated_arrival_late
import sinatra.ui.generated.resources.future_estimated_departure
import sinatra.ui.generated.resources.future_estimated_departure_early
import sinatra.ui.generated.resources.future_estimated_departure_late
import sinatra.ui.generated.resources.future_scheduled_departure
import sinatra.ui.generated.resources.past_departure
import sinatra.ui.generated.resources.past_departure_early
import sinatra.ui.generated.resources.past_departure_late
import sinatra.ui.generated.resources.scheduled_arrival

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
fun TimetableStationTime.pick(): StopStationTime {
    return when (arrival.time.isInPast()) {
        true -> StopStationTime.Departure(departure)
        false -> StopStationTime.Arrival(arrival)
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
    ListCard(
        {
            if (showStopIcon) {
                RandleScaffold {
                    BusIcon()
                }
            }
        },
        Modifier
            .semantics {
                contentDescription = "Stop listing"
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
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(0.25.rdp)
//            ) {
//                stop.accessibility.icons()
//            }
        }

        stopStationTime?.let {
            Text(
                it.text,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall
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
        return when (stationTime) {
            is StationTime.Scheduled -> stringResource(when (this) {
                is StopStationTime.Arrival -> Res.string.scheduled_arrival
                is StopStationTime.Departure -> when (isInPast) {
                    true -> Res.string.past_departure
                    false -> Res.string.future_scheduled_departure
                }
            }, time)
            is StationTime.Live -> when {
                stationTime.delay.inWholeSeconds < -60L -> stringResource(
                    when (this) {
                        is StopStationTime.Arrival -> Res.string.estimated_arrival_early
                        is StopStationTime.Departure -> when (isInPast) {
                            true -> Res.string.past_departure_early
                            else -> Res.string.future_estimated_departure_early
                        }
                    },
                    time,
                    (-stationTime.delay).text
                )
                stationTime.delay.inWholeSeconds > 60L -> stringResource(
                    when (this) {
                        is StopStationTime.Arrival -> Res.string.estimated_arrival_late
                        is StopStationTime.Departure -> when (isInPast) {
                            true -> Res.string.past_departure_late
                            else -> Res.string.future_estimated_departure_late
                        }
                    },
                    time,
                    stationTime.delay.text
                )
                else -> stringResource(
                    when (this) {
                        is StopStationTime.Arrival -> Res.string.estimated_arrival
                        is StopStationTime.Departure -> when (isInPast) {
                            true -> Res.string.past_departure
                            else -> Res.string.future_estimated_departure
                        }
                    },
                    time
                )
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