package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.ui.color
import cl.emilym.sinatra.ui.onColor

val routeRandleSize
    @Composable
    get() = 2.5.rdp

@Composable
fun RouteRandle(
    route: Route,
    modifier: Modifier = Modifier,
    size: Dp = routeRandleSize
) {
    val borderSize = (size * 0.06f)
    val extraModifier = when {
        route.colors != null -> Modifier
            .clip(CircleShape)
            .then(
                when {
                    borderSize >= 2.dp -> Modifier.border(borderSize, Color.White, CircleShape)
                    else -> Modifier
                }
            )
            .background(route.colors!!.color())
        else -> Modifier
    }
    Box(
        Modifier.size(size).then(extraModifier).padding((size * 0.12f)).then(modifier),
        contentAlignment = Alignment.Center
    ) {
        AutoSizeText(
            route.displayCode,
            fontWeight = FontWeight.Bold,
            color = route.colors?.onColor() ?: LocalContentColor.current
        )
    }
}

@Composable
fun RouteCard(
    route: Route,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ListCard(
        { RouteRandle(route) },
        Modifier
            .semantics {
                contentDescription = "Route listing for ${route.displayCode}"
            }
            .then(modifier),
        onClick
    ) {
        Text(route.name)
    }
}