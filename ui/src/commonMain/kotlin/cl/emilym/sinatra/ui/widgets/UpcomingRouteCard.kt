package cl.emilym.sinatra.ui.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import cl.emilym.sinatra.data.models.IStopTimetableTime
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.route_code_name
import sinatra.ui.generated.resources.route_with_heading
import sinatra.ui.generated.resources.stop_detail_from
import kotlin.math.max

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
                when(timetableTime.childStop) {
                    null -> it.text
                    else -> stringResource(
                        Res.string.stop_detail_from,
                        it.text,
                            timetableTime.childStop?.name?.platformName() ?: "null"
                        )
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun String.platformName(): String {
    val lc = toLowerCase(Locale("en-AU"))
    val idx = listOf(lc.indexOf("platform"), lc.indexOf("plt"), 0).max()

    return substring(idx)
}