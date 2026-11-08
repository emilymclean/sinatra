package cl.emilym.sinatra.ui.presentation.screens.maps.search.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.ui.asInstants
import cl.emilym.sinatra.ui.presentation.screens.maps.stop.StopDetailScreen
import cl.emilym.sinatra.ui.widgets.SpecificRecomposeOnInstants
import cl.emilym.sinatra.ui.widgets.StopStationTime
import cl.emilym.sinatra.ui.widgets.UpcomingRouteToStationCard
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.browse_option_upcoming_routes_multiple

@Composable
fun MultipleNearbyStopDeparturesWidget(
    option: BrowsePrompt.MultipleNearbyStopDepartures,
) {
    val navigator = LocalNavigator.currentOrThrow
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 1.rdp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Column {
            Text(
                stringResource(Res.string.browse_option_upcoming_routes_multiple),
                modifier = Modifier.padding(1.rdp)
            )
            val triggers = option.stops.flatMap {
                listOfNotNull(it.departures.firstOrNull()?.stationTime)
                    .asInstants()
            }
            SpecificRecomposeOnInstants(triggers) { _ ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(0.25.rdp)
                ) {
                    for (upcoming in option.stops) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            )
                        ) {
                            val time = remember(upcoming) { upcoming.departures.first() }
                            UpcomingRouteToStationCard(
                                upcoming.stop,
                                time,
                                StopStationTime.Departure(time.stationTime.departure),
                                modifier = Modifier.fillMaxWidth(),
                                short = true,
                                onClick = {
                                    navigator.push(
                                        StopDetailScreen(
                                            upcoming.stop.id
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}