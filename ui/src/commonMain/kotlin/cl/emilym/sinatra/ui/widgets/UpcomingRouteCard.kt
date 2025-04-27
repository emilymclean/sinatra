package cl.emilym.sinatra.ui.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.data.models.IStopTimetableTime
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.route_code_name
import sinatra.ui.generated.resources.route_with_heading

@Composable
fun UpcomingRouteCard(
    timetableTime: IStopTimetableTime,
    stopStationTime: StopStationTime?,
    modifier: Modifier = Modifier,
    short: Boolean = false,
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
                when {
                    short -> stringResource(
                        Res.string.route_code_name,
                        timetableTime.routeCode
                    )
                    else -> timetableTime.route?.name ?: stringResource(
                        Res.string.route_code_name,
                        timetableTime.routeCode
                    )
                }, timetableTime.heading
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