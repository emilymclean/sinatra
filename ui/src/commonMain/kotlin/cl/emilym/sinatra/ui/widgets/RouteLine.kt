package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.ui.color

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
    Box(
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier
            .height(0.5.rdp)
            .fillMaxWidth()
            .background(color)
        )
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(1.rdp),
        ) {
            for (i in stops.indices) {
                RouteNode(
                    i == 0 || i == stops.size - 1,
                    color
                )
            }
        }
    }
}