package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop

@Composable
fun FavouriteCard(
    favourite: Favourite,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (favourite) {
        is Favourite.Stop -> StopCard(
            favourite.stop,
            onClick = { onClick() },
            showStopIcon = true,
            modifier = modifier
        )

        is Favourite.Route -> RouteCard(
            favourite.route,
            onClick = { onClick() },
            modifier = modifier
        )

        is Favourite.StopOnRoute -> StopCard(
            favourite.stop,
            onClick = { onClick() },
            showStopIcon = true,
            modifier = modifier
        )

        is Favourite.Place -> PlaceCard(
            favourite.place,
            modifier = modifier,
            showPlaceIcon = true,
            onClick = { onClick() },
        )
    }
}