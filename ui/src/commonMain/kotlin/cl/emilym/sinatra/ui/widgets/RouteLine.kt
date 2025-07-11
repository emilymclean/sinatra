package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiContentMeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.asRadians
import cl.emilym.sinatra.data.models.Degree
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.TimetableStationTime
import cl.emilym.sinatra.degrees
import cl.emilym.sinatra.sumOfIndexed
import cl.emilym.sinatra.ui.color
import cl.emilym.sinatra.ui.localization.LocalClock
import cl.emilym.sinatra.ui.localization.toTodayInstant
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

const val PASSED_ALPHA = 0.5f

@Composable
fun RouteNode(
    terminus: Boolean,
    color: Color = MaterialTheme.colorScheme.onSurface,
    size: Dp = 1.rdp,
    borderSize: Dp = 0.1.rdp
) {
    if (terminus) {
        Box(Modifier
            .size(size)
            .clip(CircleShape)
            .border(borderSize, color, CircleShape)
            .background(MaterialTheme.colorScheme.surface)
        )
    } else {
        Box(Modifier
            .size(size)
            .clip(CircleShape)
            .border(borderSize, MaterialTheme.colorScheme.surface, CircleShape)
            .background(color)
        )
    }
}

@Composable
fun RouteLine(
    route: Route,
    stops: List<Stop>,
    timetable: List<TimetableStationTime>? = null
) {
    val scrollState = rememberScrollState()
    if (stops.isEmpty()) return
    if (timetable == null) {
        _RouteLine(route, stops, timetable, scrollState)
    } else {
        RecomposeOnInstants(timetable.flatMap { it.times.map { it.time.toTodayInstant() } }) {
            _RouteLine(route, stops, timetable, scrollState)
        }
    }
}

@Composable
private fun _RouteLine(
    route: Route,
    stops: List<Stop>,
    timetable: List<TimetableStationTime>? = null,
    scrollState: ScrollState = rememberScrollState()
) {
    val arrivalProgress = when {
        timetable == null -> -1
        else -> {
            val now = LocalClock.current.now()
            val first = timetable.indexOfFirst { it.arrival.time.toTodayInstant() > now }
            if (first < 0) Int.MAX_VALUE else first
        }
    }
    val departureProgress = when {
        timetable == null -> -1
        else -> {
            val now = LocalClock.current.now()
            val first = timetable.indexOfFirst { it.departure.time.toTodayInstant() > now }
            if (first < 0) Int.MAX_VALUE else first
        }
    }

    val color = route.color()
    Box(Modifier.horizontalScroll(scrollState)) {
        RouteLine(
            {
                for (i in stops.indices) {
                    RouteNode(
                        i == 0 || i == stops.size - 1,
                        when {
                            i >= departureProgress -> color
                            else -> color.copy(PASSED_ALPHA)
                                .compositeOver(MaterialTheme.colorScheme.surface)
                        }
                    )
                }
            },
            {
                for (i in stops.indices) {
                    val stop = stops[i]
                    Text(
                        stop.simpleName,
                        style = MaterialTheme.typography.labelMedium,
                        color = when {
                            i >= departureProgress -> MaterialTheme.colorScheme.onSurface
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = PASSED_ALPHA)
                                .compositeOver(MaterialTheme.colorScheme.surface)
                        },
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.widthIn(max = 200.dp).rotate(-80f)
                    )
                }
            },
            {
                for (i in stops.indices - 1) {
                    Box(
                        Modifier
                            .background(
                                when {
                                    i >= arrivalProgress -> color
                                    else -> color.copy(alpha = PASSED_ALPHA)
                                        .compositeOver(MaterialTheme.colorScheme.surface)
                                }
                            )
                            .height(0.5.rdp)
                            .fillMaxWidth()
                    )
                }
            }
        )
    }
}

@Composable
internal fun RouteLine(
    stopNodes: @Composable () -> Unit,
    stopText: @Composable () -> Unit,
    line: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Layout(
        contents = listOf(stopNodes, stopText, line),
        modifier = modifier,
        measurePolicy = with(LocalDensity.current) {
            RouteLineMeasurePolicy(
                1.rdp.toPx().toInt(),
                1.rdp.toPx().toInt(),
                0.25.rdp.toPx().toInt()
            )
        }
    )
}

class RouteLineMeasurePolicy(
    private val horizontalMargin: Int,
    private val spaceBetweenNodes: Int,
    private val spaceBetweenNodeAndText: Int,
    textRotation: Degree = (-80f).degrees,
): MultiContentMeasurePolicy {

    private val textRotation = textRotation.asRadians

    override fun MeasureScope.measure(
        measurables: List<List<Measurable>>,
        constraints: Constraints
    ): MeasureResult {
        val lineOffset = 5

        val stopNodeMeasurables = measurables[0]
        val stopTextMeasurables = measurables[1]
        val lineMeasurables = measurables[2]

        val stopText = stopTextMeasurables.map { it.measure(constraints) }
        val stopNode = stopNodeMeasurables.map { it.measure(constraints) }

        val nodeHeight = stopNode[0].height
        val nodeWidth = stopNode[0].width

        val rotatedTextWidths = stopText.map {
            abs(it.width * cos(textRotation)) + abs(it.height * sin(textRotation))
        }
        val rotatedTextHeights = stopText.map {
            abs(it.width * sin(textRotation)) + abs(it.height * cos(textRotation))
        }
        val maxTextHeight = rotatedTextHeights.max()
        val totalTextWidth = rotatedTextWidths.sumOfIndexed { i,it ->
            val textWidth = max(it.toInt(), nodeWidth)
            when (i) {
                rotatedTextWidths.lastIndex -> textWidth
                else -> min(textWidth, spaceBetweenNodes + nodeWidth)
            }
        }
        val totalNodeWidth = stopNode.sumOf { it.width }

        val width = (max(totalTextWidth, totalNodeWidth + (spaceBetweenNodes * stopNodeMeasurables.lastIndex)))
        val height = (maxTextHeight + stopNode[0].height + spaceBetweenNodeAndText).toInt()

        val lineWidth = ((stopNode[0].width * 2) + spaceBetweenNodes - (lineOffset * 2))
        val lines = lineMeasurables.map { it.measure(constraints.copy(minWidth = lineWidth, maxWidth = lineWidth)) }

        return layout(width + (horizontalMargin * 2), height) {
            val nodeY = height - (nodeHeight / 2)
            for (i in stopNode.indices) {
                val x = when {
                    i == 0 -> horizontalMargin
                    else -> i * (nodeWidth + spaceBetweenNodes) + horizontalMargin
                }
                if (i < stopNode.lastIndex) {
                    val line = lines[i]
                    line.place(lineOffset + x, nodeY + (line.height / 2))
                }

                stopNode[i].place(
                    x,
                    nodeY,
                )
                stopText[i].place(
                    x + ((rotatedTextWidths[i] - stopText[i].width) / 2).toInt(),
                    height - (nodeHeight * 2) - spaceBetweenNodeAndText - ((rotatedTextHeights[i] - stopText[i].height) / 2).toInt()
                )
            }
        }
    }
}