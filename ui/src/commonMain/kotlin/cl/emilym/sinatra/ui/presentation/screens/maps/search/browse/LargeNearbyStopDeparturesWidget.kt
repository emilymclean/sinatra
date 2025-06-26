package cl.emilym.sinatra.ui.presentation.screens.maps.search.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.ReferencedTime
import cl.emilym.sinatra.ui.asInstants
import cl.emilym.sinatra.ui.presentation.screens.maps.route.RouteDetailScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.stop.StopDetailScreen
import cl.emilym.sinatra.ui.widgets.SpecificRecomposeOnInstants
import cl.emilym.sinatra.ui.widgets.StopStationTime
import cl.emilym.sinatra.ui.widgets.UpcomingRouteCard
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.browse_option_upcoming_routes

@Composable
fun LargeNearbyStopDeparturesWidget(
    option: BrowsePrompt.LargeNearbyStopDepartures,
    refresh: () -> Unit
) {
    val navigator = LocalNavigator.currentOrThrow
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 1.rdp)
            .clickable { navigator.push(StopDetailScreen(option.stop.stop.id)) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Column {
            Text(
                stringResource(Res.string.browse_option_upcoming_routes, option.stop.stop.name),
                modifier = Modifier.padding(1.rdp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Column {
                    val triggers = option.stop.departures.map { it.stationTime }.asInstants()
                    SpecificRecomposeOnInstants(triggers) { trigger ->
                        for (upcoming in option.stop.departures.take(2)) {
                            UpcomingRouteCard(
                                upcoming,
                                StopStationTime.Departure(upcoming.stationTime.departure),
                                short = true,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigator.push(
                                        RouteDetailScreen(
                                            upcoming.routeId,
                                            upcoming.serviceId,
                                            upcoming.tripId,
                                            option.stop.stop.id,
                                            (upcoming.stationTime.arrival.time as? ReferencedTime)?.startOfDay
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