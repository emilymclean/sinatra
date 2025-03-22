package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.ui.localization.format
import cl.emilym.sinatra.ui.text
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.journey_card_period

@Composable
fun JourneyOptionCard(
    journey: Journey,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    ListCard(
        null,
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.5.rdp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(1.rdp)
            ) {
                JourneyLine(
                    journey,
                    Modifier.weight(1f)
                )
                Text(journey.duration.text(true))
            }
            Text(
                stringResource(
                    Res.string.journey_card_period,
                    journey.departureTime.instant.format(),
                    journey.arrivalTime.instant.format()
                ),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JourneyLine(
    journey: Journey,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.spacedBy(0.5.rdp),
        modifier = Modifier
            .then(modifier)
    ) {
        for (i in journey.legs.indices) {
            val leg = journey.legs[i]

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.5.rdp),
                modifier = Modifier.align(Alignment.CenterVertically),
            ) {
                if (i != 0) {
                    ForwardIcon(
                        modifier = Modifier.size(1.rdp)
                    )
                }

                when (leg) {
                    is JourneyLeg.Transfer, is JourneyLeg.TransferPoint -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WalkIcon(
                                modifier = Modifier.size(1.rdp),
                            )
                            Text(
                                "${leg.travelTime.inWholeMinutes.coerceAtLeast(1)}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    is JourneyLeg.Travel -> {
                        RouteRandle(
                            leg.route,
                            size = 1.5.rdp
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}