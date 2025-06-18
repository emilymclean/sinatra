package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.specialType

@Composable
fun FavouriteCard(
    favourite: Favourite,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: @Composable () -> Unit = {
        when (favourite.specialType) {
            SpecialFavouriteType.WORK -> WorkIcon()
            SpecialFavouriteType.HOME -> HomeIcon()
            else -> when (favourite) {
                is Favourite.Stop -> DefaultStopCardIcon(favourite.stop)
                is Favourite.Route -> DefaultRouteCardIcon(favourite.route)
                is Favourite.StopOnRoute -> DefaultStopCardIcon(favourite.stop)
                is Favourite.Place -> DefaultPlaceCardIcon(favourite.place)
            }
        }
    }

    when (favourite) {
        is Favourite.Stop -> IconStopCard(
            favourite.stop,
            onClick = { onClick() },
            modifier = modifier,
            icon = icon
        )

        is Favourite.Route -> IconRouteCard(
            favourite.route,
            onClick = { onClick() },
            modifier = modifier,
            icon = icon
        )

        is Favourite.StopOnRoute -> IconStopCard(
            favourite.stop,
            onClick = { onClick() },
            modifier = modifier,
            icon = icon
        )

        is Favourite.Place -> IconPlaceCard(
            favourite.place,
            onClick = { onClick() },
            modifier = modifier,
            icon = icon
        )
    }
}