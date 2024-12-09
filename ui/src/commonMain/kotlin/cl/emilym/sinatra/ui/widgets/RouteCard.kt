package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import org.jetbrains.compose.resources.painterResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.forward

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
fun RouteCard(
    route: Route,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ListCard(
        { RouteRandle(route) },
        modifier,
        onClick
    ) {
        Text(route.name)
    }
}