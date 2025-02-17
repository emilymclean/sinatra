package cl.emilym.sinatra.ui.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.StationTime
import cl.emilym.sinatra.data.models.StopTimetableTime
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.estimated_arrival
import sinatra.ui.generated.resources.route_code_name
import sinatra.ui.generated.resources.route_with_heading
import sinatra.ui.generated.resources.scheduled_arrival

@Composable
fun UpcomingRouteCard(
    timetableTime: IStopTimetableTime,
    stopStationTime: StopStationTime?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ListCard(
        {
            timetableTime.route?.let { RouteRandle(it) }
        },
        modifier,
        onClick
    ) {
        Text(
            stringResource(
                Res.string.route_with_heading,
                timetableTime.route?.name ?: stringResource(
                    Res.string.route_code_name,
                    timetableTime.routeCode
                ), timetableTime.heading
            )
        )
        stopStationTime?.let {
            Text(
                it.text,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}