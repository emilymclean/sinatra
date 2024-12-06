package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.ui.color
import cl.emilym.sinatra.ui.onColor

@Composable
fun RouteRandle(
    route: Route,
    modifier: Modifier = Modifier
) {
    val extraModifier = when {
        route.colors != null -> Modifier
            .clip(CircleShape)
            .border(0.15.rdp, Color.White, CircleShape)
            .background(route.colors!!.color())
        else -> Modifier
    }
    Box(
        Modifier.size(2.5.rdp).then(extraModifier).padding(0.25.rdp).then(modifier),
        contentAlignment = Alignment.Center
    ) {
        AutoSizeText(
            route.code,
            fontWeight = FontWeight.Bold,
            color = route.colors?.onColor() ?: LocalContentColor.current
        )
    }
}

@Composable
fun RouteCard(route: Route) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 1.rdp),
        horizontalArrangement = Arrangement.spacedBy(1.rdp)
    ) {
        RouteRandle(route)
        Text(route.name)
    }
}