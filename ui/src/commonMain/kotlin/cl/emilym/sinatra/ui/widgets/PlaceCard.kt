package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Place
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.semantics_place_listing

@Composable
fun DefaultPlaceCardIcon(place: Place) {
    GenericMarkerIcon()
}

@Composable
fun IconPlaceCard(
    place: Place,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)?,
) {
    val placeListingSemantics = stringResource(Res.string.semantics_place_listing, place.displayName)
    ListCard(
        icon?.let {
            {
                RandleScaffold {
                    it()
                }
            }
        },
        Modifier
            .semantics {
                contentDescription = placeListingSemantics
            }
            .then(modifier),
        onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.5.rdp)
        ) {
            Text(
                place.displayName,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}

@Composable
fun PlaceCard(
    place: Place,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showPlaceIcon: Boolean = false,
) {
    IconPlaceCard(
        place,
        modifier,
        onClick,
        when (showPlaceIcon) {
            true -> { { DefaultPlaceCardIcon(place) } }
            else -> null
        }
    )
}