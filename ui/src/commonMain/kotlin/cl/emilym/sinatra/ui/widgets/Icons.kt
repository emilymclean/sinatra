package cl.emilym.sinatra.ui.widgets

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.accessible
import sinatra.ui.generated.resources.not_accessible
import sinatra.ui.generated.resources.bike
import sinatra.ui.generated.resources.no_routes
import sinatra.ui.generated.resources.my_location
import sinatra.ui.generated.resources.search
import sinatra.ui.generated.resources.no_results

@Composable
fun AccessibleIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.accessible),
        contentDescription = "A person in a wheelchair",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun NotAccessibleIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.not_accessible),
        contentDescription = "A person in a wheelchair with a line through them",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun WheelchairAccessibleIcon(
    isAccessible: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    when {
        isAccessible -> AccessibleIcon(modifier, tint)
        else -> NotAccessibleIcon(modifier, tint)
    }
}

@Composable
fun BikeIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.bike),
        contentDescription = "A person in on a bike",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun NoBusIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.no_routes),
        contentDescription = "A bus with a strike through it",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun MyLocationIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.my_location),
        contentDescription = "A crosshair",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun SearchIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.search),
        contentDescription = "A magnifying glass",
        modifier = modifier,
        tint = tint,
    )
}

@Composable
fun NoResultsIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.no_results),
        contentDescription = "A magnifying glass with a cross beside it",
        modifier = modifier,
        tint = tint
    )
}