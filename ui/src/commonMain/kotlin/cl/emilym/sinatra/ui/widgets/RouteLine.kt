package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiContentMeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.deg
import cl.emilym.sinatra.ui.color
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

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
    stops: List<Stop>
) {
    val color = route.colors?.color() ?: MaterialTheme.colorScheme.onSurface
    Box(Modifier.horizontalScroll(rememberScrollState())) {
        RouteLine(
            {
                for (i in stops.indices) {
                    RouteNode(
                        i == 0 || i == stops.size - 1,
                        color
                    )
                }
            },
            {
                for (s in stops) {
                    Text(
                        s.name,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.rotate(-80f)
                    )
                }
            },
            {
                Box(
                    Modifier
                        .background(color)
                        .height(0.5.rdp)
                        .fillMaxWidth()
                )
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
    textRotation: Float = -80f,
): MultiContentMeasurePolicy {

    private val textRotation = textRotation.deg

    override fun MeasureScope.measure(
        measurables: List<List<Measurable>>,
        constraints: Constraints
    ): MeasureResult {
        val lineOffset = 5

        val stopNodeMeasurables = measurables[0]
        val stopTextMeasurables = measurables[1]
        val lineMeasurable = measurables[2][0]

        val stopText = stopTextMeasurables.map { it.measure(constraints) }
        val stopNode = stopNodeMeasurables.map { it.measure(constraints) }

        val textHeights = stopText.map {
            abs(it.width * sin(textRotation)) + abs(it.height * cos(textRotation))
        }
        val textWidths = stopText.map {
            abs(it.width * cos(textRotation)) + abs(it.height * sin(textRotation))
        }
        val maxTextHeight = textHeights.max()
        val totalTextWidth = textWidths.sum()
        val totalNodeWidth = stopNode.fold(0f) { acc, it ->
            acc + it.width
        }

        val nodeHeight = stopNode[0].height
        val nodeWidth = stopNode[0].width
        val width = (
                max(totalTextWidth, totalNodeWidth) +
                (spaceBetweenNodes * stopNodeMeasurables.lastIndex)
        ).toInt()
        val height = (maxTextHeight + stopNode[0].height + spaceBetweenNodeAndText).toInt()

        val line = lineMeasurable.measure(constraints.copy(minWidth = width - (lineOffset * 2), maxWidth = width - (lineOffset * 2)))

        return layout(width + (horizontalMargin * 2), height) {
            val nodeY = height - (nodeHeight / 2)
            line.place(horizontalMargin + lineOffset, nodeY + (line.height/2))
            for (i in stopNode.indices) {
                val x = when {
                    i == 0 -> horizontalMargin
                    else -> i * (nodeWidth + spaceBetweenNodes) + horizontalMargin
                }
                stopNode[i].place(
                    x,
                    nodeY,
                )
                stopText[i].place(
                    (x + stopText[i].height + (nodeWidth / 2) - textHeights[i] + (stopText[i].width / 2)).toInt(),
                    (nodeY - (nodeHeight / 2) - spaceBetweenNodeAndText - textHeights[i] + (stopText[i].width / 2)).toInt()
                )
            }
        }
    }
}