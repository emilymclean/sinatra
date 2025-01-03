package cl.emilym.sinatra.ui.widgets

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.accessible
import sinatra.ui.generated.resources.back
import sinatra.ui.generated.resources.bike
import sinatra.ui.generated.resources.external_link
import sinatra.ui.generated.resources.forward
import sinatra.ui.generated.resources.info
import sinatra.ui.generated.resources.warning
import sinatra.ui.generated.resources.severe_warning
import sinatra.ui.generated.resources.map
import sinatra.ui.generated.resources.my_location
import sinatra.ui.generated.resources.no_results
import sinatra.ui.generated.resources.no_routes
import sinatra.ui.generated.resources.not_accessible
import sinatra.ui.generated.resources.search
import sinatra.ui.generated.resources.star
import sinatra.ui.generated.resources.star_outline
import sinatra.ui.generated.resources.marker_icon
import sinatra.ui.generated.resources.bus
import sinatra.ui.generated.resources.tram
import sinatra.ui.generated.resources.navigate
import sinatra.ui.generated.resources.walk
import sinatra.ui.generated.resources.journey_start

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

@Composable
fun StarOutlineIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.star_outline),
        contentDescription = "The outline of a star",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun StarIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.star),
        contentDescription = "A filled in star",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun FavouriteIcon(
    favourited: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    when (favourited) {
        true -> StarIcon(modifier, tint)
        false -> StarOutlineIcon(modifier, tint)
    }
}

@Composable
fun MapIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.map),
        contentDescription = "A map",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun NavigateIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.navigate),
        contentDescription = "A direction arrow",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun InfoIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.info),
        contentDescription = "An \"i\" in a circle",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun WarningIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.warning),
        contentDescription = "An \"!\" in a triangle",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun SevereWarningIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.severe_warning),
        contentDescription = "An \"!\" in an octogon",
        modifier = modifier,
        tint = tint
    )
}


@Composable
fun BackIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.back),
        contentDescription = "An arrow pointing to the left",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun ForwardIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.forward),
        contentDescription = "A forward arrow",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun ExternalLinkIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.external_link),
        contentDescription = "An arrow out of a box",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun GenericMarkerIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.marker_icon),
        contentDescription = "A map marker",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun BusIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.bus),
        contentDescription = "A bus",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun TramIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.tram),
        contentDescription = "A tram",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun WalkIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.walk),
        contentDescription = "A person walking",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun JourneyStartIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painterResource(Res.drawable.journey_start),
        contentDescription = "A circle with a centred dot",
        modifier = modifier,
        tint = tint
    )
}